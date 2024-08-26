package pl.davidduke.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.davidduke.dto.RequestPersonDto;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.entity.Person;
import pl.davidduke.exception.IpnAlreadyExistsException;
import pl.davidduke.exception.PersonNotFoundException;
import pl.davidduke.repository.PersonRepository;
import pl.davidduke.util.PersonMapper;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonService {
    final PersonRepository personRepository;
    final PersonMapper mapper;

    public Page<PersonDto> findAllPeople(Pageable pageable) {
        return personRepository
                .findAll(pageable)
                .map(mapper::personToPersonDto);
    }

    public PersonDto findPersonById(int id) {
        return personRepository
                .findById(id)
                .map(mapper::personToPersonDto)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    @Transactional
    public PersonDto createPerson(RequestPersonDto createdPersonDto) {
        if (ipnExists(createdPersonDto.getIpn())) {
            throw new IpnAlreadyExistsException(createdPersonDto.getIpn());
        }

        Person savedPerson = personRepository.save(mapper.requestPersonDtoToPerson(createdPersonDto));
        return mapper.personToPersonDto(savedPerson);
    }

    private boolean ipnExists(String ipn) {
        return personRepository.findByIpn(ipn).isPresent();
    }

    @Transactional
    public PersonDto updatePerson(int id, RequestPersonDto requestPersonDto) {
        PersonDto foundPersonById = findPersonById(id);
        if (ipnExists(requestPersonDto.getIpn()) &&
                !isIpnOwnedBySamePerson(requestPersonDto, foundPersonById)) {
            throw new IpnAlreadyExistsException(requestPersonDto.getIpn());
        }
        Person personToBeSaved = mapper.requestPersonDtoToPerson(requestPersonDto);
        personToBeSaved.setId(id);
        personRepository.save(personToBeSaved);
        return mapper.personToPersonDto(personToBeSaved);
    }

    private boolean isIpnOwnedBySamePerson(RequestPersonDto requestPersonDto, PersonDto foundPersonById) {
        return foundPersonById.getIpn().equals(requestPersonDto.getIpn());
    }

    @Transactional
    public void deletePerson(int id) {
        personRepository.deleteById(findPersonById(id).getId());
    }
}
