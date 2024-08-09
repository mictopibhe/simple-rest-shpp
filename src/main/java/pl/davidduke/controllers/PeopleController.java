package pl.davidduke.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.services.PeopleService;

@RestController
@RequestMapping("/api/v1/people")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PeopleController {
    final PeopleService peopleService;

    @Autowired
    public PeopleController(PeopleService service) {
        peopleService = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PersonDto> returnAllPeople(
            Pageable pageable,
            @RequestParam(name = "sortBy", defaultValue = "lastName") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection) {
        Sort.Direction direction = sortDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        var request = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortBy));
        return peopleService.findAllPeople(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonDto returnPersonById(@PathVariable int id) {
        return peopleService.findPersonById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonDto createPerson(@RequestBody @Valid PersonDto personDto) {
        return peopleService.createPerson(personDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonDto updatePerson(@PathVariable("id") int id, @RequestBody @Valid PersonDto personDto) {
        return peopleService.updatePerson(id, personDto);
    }
}
