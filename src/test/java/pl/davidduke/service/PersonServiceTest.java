package pl.davidduke.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.entity.Person;
import pl.davidduke.exception.PersonNotFoundException;
import pl.davidduke.repository.PersonRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class PersonServiceTest {
    final ModelMapper MAPPER = new ModelMapper();
    @Mock
    PersonRepository repository;
    @Mock
    ModelMapper modelMapperMock;

    @InjectMocks
    PersonService service;

    Person person;
    PersonDto personDto;

    @BeforeEach
    void setUp() {
        person = Person
                .builder()
                .id(1)
                .firstName("Олександр")
                .lastName("Давидюк")
                .birthday(LocalDate.of(1995, 6, 5))
                .ipn("2248000331")
                .build();
        personDto = MAPPER.map(person, PersonDto.class);
    }

    @Test
    void findAllPeopleShouldReturnPageWithPeopleList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Person> peoplePage =
                new PageImpl<>(Collections.singletonList(person));

        when(repository.findAll(pageable))
                .thenReturn(peoplePage);
        when(modelMapperMock.map(person, PersonDto.class))
                .thenReturn(personDto);

        Page<PersonDto> result = service.findAllPeople(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(personDto, result.getContent().get(0));

        verify(repository, times(1)).findAll(pageable);
        verify(modelMapperMock, times(1)).map(person, PersonDto.class);
    }

    @Test
    void createPersonShouldReturnCreatedPersonDto() {
        when(repository.save(any(Person.class)))
                .thenReturn(person);
        when(modelMapperMock.map(personDto, Person.class))
                .thenReturn(person);

        PersonDto result = service.createPerson(personDto);

        assertNotNull(result);

        verify(repository, times(1)).save(person);
        verify(modelMapperMock, times(1)).map(personDto, Person.class);
    }

    @Test
    void updatePersonShouldReturnUpdatedPersonDto() {
        PersonDto updatedPersonDto = personDto;
        updatedPersonDto.setFirstName("David");
        updatedPersonDto.setLastName("Duke");
        Person updatedPerson = Person
                .builder()
                .id(1)
                .firstName("David")
                .lastName("Duke")
                .birthday(LocalDate.of(1995, 6, 5))
                .ipn("2248000331")
                .build();

        when(repository.findById(anyInt()))
                .thenReturn(Optional.of(person));
        when(modelMapperMock.map(any(Person.class), eq(PersonDto.class)))
                .thenReturn(personDto);
        when(modelMapperMock.map(any(PersonDto.class), eq(Person.class)))
                .thenReturn(person);
        when(repository.save(any(Person.class)))
                .thenReturn(updatedPerson);

        service.updatePerson(1, updatedPersonDto);

        verify(repository, times(1)).findById(anyInt());
        verify(modelMapperMock, times(1)).map(any(PersonDto.class), eq(Person.class));
        verify(repository, times(1)).save(any(Person.class));
    }

    @Test
    void updatePersonShouldThrowPersonNotFoundExceptionWhenPersonWithSpecifiedIdIsNotExist() {
        when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () ->
                service.updatePerson(1, personDto));

        verify(repository, times(1)).findById(anyInt());
    }

    @Test
    void findPersonByIdShouldReturnPersonDtoWhenPersonWithSpecifiedIdExist() {
        when(repository.findById(anyInt()))
                .thenReturn(Optional.of(person));
        when(modelMapperMock.map(person, PersonDto.class))
                .thenReturn(personDto);

        service.findPersonById(1);

        verify(repository, times(1)).findById(anyInt());
        verify(modelMapperMock, times(1)).map(person, PersonDto.class);
    }

    @Test
    void findPersonByIdShouldThrowPersonNotFoundExceptionWhenPersonWithSpecifiedIdIsNotExist() {
        when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () ->
                service.findPersonById(1));

        verify(repository, times(1)).findById(anyInt());
    }

    @Test
    void deletePersonShouldDeletePersonWithSpecifiedIdWhenPersonExist() {
        when(repository.findById(anyInt()))
                .thenReturn(Optional.of(person));
        when(modelMapperMock.map(person, PersonDto.class))
                .thenReturn(personDto);
        doNothing().when(repository).deleteById(anyInt());

        service.deletePerson(1);

        verify(repository, times(1)).findById(anyInt());
        verify(repository, times(1)).deleteById(1);
    }
}