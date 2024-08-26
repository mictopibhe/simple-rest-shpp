package pl.davidduke.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.davidduke.dto.RequestPersonDto;
import pl.davidduke.dto.PersonDto;
import pl.davidduke.entity.Person;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    PersonDto personToPersonDto(Person person);

    @Mapping(target = "id", ignore = true)
    Person requestPersonDtoToPerson(RequestPersonDto requestPersonDto);
}
