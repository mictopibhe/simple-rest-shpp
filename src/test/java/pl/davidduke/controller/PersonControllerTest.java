package pl.davidduke.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.service.PersonService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class PersonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PersonService personService;

    @InjectMocks
    PersonController personController;

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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Олександр"))
                .andExpect(jsonPath("$.lastName").value("Давидюк"))
                .andExpect(jsonPath("$.birthday").value(
                        LocalDate.of(1995, 6, 5).toString()))
                .andExpect(jsonPath("$.ipn").value("2248000331"));


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
        var secondPersonDto = PersonDto.builder()
                .id(2)
                .firstName("Артем")
                .lastName("Балаболка")
                .birthday(LocalDate.of(1990, 1, 5))
                .ipn("3248000333")
                .build();
        var people = List.of(personDto, secondPersonDto);
        var pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastName"));
        var peoplePage = new PageImpl<>(people, pageable, people.size());

        when(personService.findAllPeople(any(PageRequest.class))).thenReturn(peoplePage);

        mockMvc
                .perform(get("/api/v1/people")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "lastName")
                        .param("sortDirection", "desc")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Артем"))
                .andExpect(jsonPath("$.content[0].lastName").value("Балаболка"))
                .andExpect(jsonPath("$.content[1].firstName").value("Олександр"))
                .andExpect(jsonPath("$.content[1].lastName").value("Давидюк"));

        verify(personService, times(1)).findAllPeople(any(PageRequest.class));
    }
}