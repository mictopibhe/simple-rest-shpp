package pl.davidduke.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.entity.Person;
import pl.davidduke.exception.PersonNotFoundException;
import pl.davidduke.repository.PersonRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonService {
    final PersonRepository personRepository;
    final ModelMapper modelMapper;

    @Autowired
    public PersonService(PersonRepository personRepository, ModelMapper modelMapper) {
        this.personRepository = personRepository;
        this.modelMapper = modelMapper;
    }

    public Page<PersonDto> findAllPeople(Pageable pageable) {
        return personRepository.findAll(pageable).map(person -> modelMapper.map(person, PersonDto.class));
    }

    @Transactional
    public PersonDto createPerson(PersonDto personDto) {
        Person savedPerson = personRepository.save(modelMapper.map(personDto, Person.class));
        personDto.setId(savedPerson.getId());
        return personDto;
    }

    @Transactional
    public PersonDto updatePerson(int id, PersonDto personDto) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isPresent()) {
            personDto.setId(id);
            Person person = modelMapper.map(personDto, Person.class);
            personRepository.save(person);
            return personDto;
        } else {
            throw new PersonNotFoundException(id);
        }
    }

    public PersonDto findPersonById(int id) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isPresent()) {
            return modelMapper.map(optionalPerson.get(), PersonDto.class);
        } else {
            throw new PersonNotFoundException(id);
        }
    }

    @Transactional
    public void deletePerson(int id) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isPresent()) {
            personRepository.deleteById(id);
        } else {
            throw new PersonNotFoundException(id);
        }
    }
}
