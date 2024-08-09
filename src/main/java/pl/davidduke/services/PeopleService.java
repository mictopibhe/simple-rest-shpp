package pl.davidduke.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.entities.Person;
import pl.davidduke.repositories.PeopleRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PeopleService {
    final PeopleRepository peopleRepository;
    final ModelMapper modelMapper;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, ModelMapper modelMapper) {
        this.peopleRepository = peopleRepository;
        this.modelMapper = modelMapper;
    }

    public Page<PersonDto> findAllPeople(Pageable pageable) {
        return peopleRepository.findAll(pageable).map(person -> modelMapper.map(person, PersonDto.class));
    }

    @Transactional
    public PersonDto createPerson(PersonDto personDto) {
        peopleRepository.save(modelMapper.map(personDto, Person.class));
        return personDto;
    }

    @Transactional
    public PersonDto updatePerson(int id, PersonDto personDto) {
        Optional<Person> optionalPerson = peopleRepository.findById(id);
        if (optionalPerson.isPresent()) {
            Person person = modelMapper.map(personDto, Person.class);
            person.setId(id);
            peopleRepository.save(person);
            return personDto;
        } else {
            throw new RuntimeException(); // todo
        }
    }
}
