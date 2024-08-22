package pl.davidduke.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.entity.Person;
import pl.davidduke.exception.IpnAlreadyExistsException;
import pl.davidduke.exception.PersonNotFoundException;
import pl.davidduke.repository.PersonRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonService {
    final PersonRepository personRepository;
    //todo мапстракт
    final ModelMapper modelMapper;

    public Page<PersonDto> findAllPeople(Pageable pageable) {
        return personRepository.findAll(pageable).map(person ->
                modelMapper.map(person, PersonDto.class));
    }

    @Transactional
    public PersonDto createPerson(PersonDto personDto) {
        if (personRepository.findByIpn(personDto.getIpn()).isPresent()) {
            throw new IpnAlreadyExistsException(personDto.getIpn());
        }

        Person savedPerson = personRepository.save(modelMapper.map(personDto, Person.class));
        personDto.setId(savedPerson.getId());
        return personDto;
    }

    @Transactional
    public PersonDto updatePerson(int id, PersonDto personDto) {
        PersonDto foundPersonById = findPersonById(id);
        if (isIpnExists(personDto, foundPersonById)) {
            throw new IpnAlreadyExistsException(personDto.getIpn());
        }
        personDto.setId(id);
        personRepository.save(modelMapper.map(personDto, Person.class));
        return personDto;
    }

    private boolean isIpnExists(PersonDto personDto, PersonDto foundPersonById) {
        return !foundPersonById.getIpn().equals(personDto.getIpn()) &&
                personRepository.findByIpn(personDto.getIpn()).isPresent();
    }

    public PersonDto findPersonById(int id) {
//       todo: new ResponseStatusException();
        return personRepository.findById(id)
                .map(person -> modelMapper.map(person, PersonDto.class))
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    @Transactional
    public void deletePerson(int id) {
        personRepository.deleteById(findPersonById(id).getId());
    }
}
