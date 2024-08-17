package pl.davidduke.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.service.PersonService;


@RestController
@RequestMapping("/api/v1/people")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@RequiredArgsConstructor
public class PersonController {
    final PersonService personService;

    @Operation(summary = "Get all people from database",
            description = "Retrieve all people with pagination and sorting. " +
                    "Use 'page' and 'size' for pagination, 'sortBy' to specify the sorting field, " +
                    "and 'sortDirection' to specify the sort order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Returns a page of people",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "object", implementation = PersonDto.class,
                                    description = "A page containing a list of PersonDto objects"))}
            ),
            @ApiResponse(responseCode = "500",
                    description = "Invalid parameters",
                    content = @Content
            )
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PersonDto> returnAllPeople(
            Pageable pageable
    ) {
        log.info("Received request to get page people with {} elements", pageable.getPageSize());
        Page<PersonDto> responsePage = personService.findAllPeople(pageable);
        log.info("Returned page people with {} elements. Status: {}", responsePage.getTotalElements(), HttpStatus.OK);

        return responsePage;
    }

    @Operation(summary = "Get a person by ID", description = "Returns a specific person by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Person exists in the database and was returned " +
                            "successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonDto.class))}
            ),
            @ApiResponse(responseCode = "404",
                    description = "Person not found in the database",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonDto returnPersonById(
            @Parameter(description = "ID of the person to retrieve", example = "1")
            @PathVariable int id
    ) {
        log.info("Received request to retrieve person by ID {}", id);
        PersonDto foundPerson = personService.findPersonById(id);
        log.info("Person with specific ID {} was returned. Status: {}", id, HttpStatus.OK);

        return foundPerson;
    }

    @Operation(summary = "Create new person and save in the database",
            description = "Creates a new person based on the provided details and saves them in the database. " +
                    "Returns the created person.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Person was successfully created and saved in the database",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonDto.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input, person could not be created due to validation errors",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonDto createPerson(
            @RequestBody @Valid PersonDto personDto
    ) {
        log.info("Received request to create a new person: {}", personDto);
        PersonDto createdPerson = personService.createPerson(personDto);
        log.info("Person {} was successfully created. Status: {}", createdPerson.getId(), HttpStatus.CREATED);

        return createdPerson;
    }

    @Operation(summary = "Update an existing person in the database",
            description = "Updates a specified person based on the provided details and saves them in the database. " +
                    "Returns the updated person.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Person was successfully updated and saved in the database",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonDto.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input, person could not be updated due to validation errors",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Person with the specified ID was not found in the database",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonDto updatePerson(
            @Parameter(description = "ID of the person to update", example = "1")
            @PathVariable("id") int id,
            @RequestBody @Valid PersonDto personDto
    ) {
        log.info("Received request to update person with ID {}", id);
        PersonDto updatedPerson = personService.updatePerson(id, personDto);
        log.info("Person {} was successfully updated. Status: {}", updatedPerson.getId(), HttpStatus.OK);
        return updatedPerson;
    }

    @Operation(summary = "Removed an existing person from the database",
            description = "Removed a specified person from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Person was successfully removed from the database",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400",
                    description = "Invalid person ID",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404",
                    description = "Person with the specified ID was not found in the database",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(
            @PathVariable("id") int id
    ) {
        log.info("Received request to delete person with ID {}", id);
        personService.deletePerson(id);
        log.info("Person with ID {} was successfully deleted. Status: {}", id, HttpStatus.NO_CONTENT);
    }
}
