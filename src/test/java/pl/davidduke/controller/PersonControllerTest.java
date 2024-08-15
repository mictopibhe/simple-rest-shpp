package pl.davidduke.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.exception.PersonNotFoundException;
import pl.davidduke.service.PersonService;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class PersonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PersonService personService;

    PersonDto personDto;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        personDto = PersonDto.builder()
                .id(1)
                .firstName("Олександр")
                .lastName("Давидюк")
                .birthday(LocalDate.of(1995, 6, 5))
                .ipn("2248000331")
                .build();
    }

    @Test
    void createPersonShouldSaveNewPersonIntoDBAndReturnPersonDtoAndCreatedStatus() throws Exception {
        when(personService.createPerson(any(PersonDto.class))).thenReturn(personDto);

        mockMvc
                .perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDto)))
                .andExpect(status()
                        .isCreated())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.firstName")
                        .value("Олександр"))
                .andExpect(jsonPath("$.lastName")
                        .value("Давидюк"))
                .andExpect(jsonPath("$.birthday")
                        .value(LocalDate.of(1995, 6, 5).toString()))
                .andExpect(jsonPath("$.ipn")
                        .value("2248000331"));


        verify(personService, times(1)).createPerson(any(PersonDto.class));
    }

    @Test
    void createPersonShouldReturnBadRequestWhenDataForCreationIsInvalid() throws Exception {
        personDto.setFirstName(null);

        mockMvc
                .perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnAllPeopleShouldReturnAllPersonsWithPaginationAndStatusOk() throws Exception {
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by("firstName").descending()
                        .and(Sort.by("lastName").descending()));
        Page<PersonDto> personDtoPage = new PageImpl<>(Collections.singletonList(personDto), pageable, 1);
        when(personService.findAllPeople(any(Pageable.class))).thenReturn(personDtoPage);

        mockMvc
                .perform(get("/api/v1/people"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.content[0].id").value(1));

    }

    @Test
    void returnPersonByIdShouldReturnPersonDtoAndStatusOk() throws Exception {
        when(personService.findPersonById(1)).thenReturn(personDto);

        mockMvc
                .perform(get("/api/v1/people/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void returnPersonByIdShouldReturnStatusNotFoundWhenPersonWithSpecifiedIdNotExist() throws Exception {
        when(personService.findPersonById(2)).thenThrow(new PersonNotFoundException(2));

        mockMvc
                .perform(get("/api/v1/people/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePersonShouldUpdatePersonAndReturnStatusOkWhenPersonExistAndNewDataIsValid() throws Exception {
        PersonDto updatedPersonDto = PersonDto
                .builder()
                .id(personDto.getId())
                .firstName("David")
                .lastName("Duke")
                .birthday(personDto.getBirthday())
                .ipn(personDto.getIpn())
                .build();
        when(personService.updatePerson(anyInt(), any(PersonDto.class))).thenReturn(updatedPersonDto);

        mockMvc
                .perform(put("/api/v1/people/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPersonDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.firstName")
                        .value("David"))
                .andExpect(jsonPath("$.lastName")
                        .value("Duke"))
                .andExpect(jsonPath("$.birthday")
                        .value(personDto.getBirthday().toString()))
                .andExpect(jsonPath("$.ipn")
                        .value(personDto.getIpn()));
    }

    @Test
    void updatePersonShouldReturnStatusNotFoundWhenPersonWithSpecifiedIdNotExist() throws Exception {
        when(personService.updatePerson(anyInt(), any(PersonDto.class))).thenThrow(new PersonNotFoundException(2));

        mockMvc
                .perform(put("/api/v1/people/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePersonShouldReturnStatusBadRequestWhenNewPersonDataIsInvalid() throws Exception {
        personDto.setFirstName(null);
        mockMvc
                .perform(put("/api/v1/people/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePersonShouldDeletePersonAndReturnStatusOk() throws Exception {
        doNothing().when(personService).deletePerson(anyInt());

        mockMvc
                .perform(delete("/api/v1/people/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePersonShouldReturnStatusNotFoundWhenPersonWithSpecifiedIdNotExist() throws Exception {
        doThrow(new PersonNotFoundException(2)).when(personService).deletePerson(anyInt());

        mockMvc
                .perform(delete("/api/v1/people/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePersonShouldReturnStatusBadRequestWhenPersonIdIsInvalid() throws Exception {
        doNothing().when(personService).deletePerson(anyInt());

        mockMvc
                .perform(delete("/api/v1/people/asd"))
                .andExpect(status().isBadRequest());
    }
}