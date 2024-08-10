package pl.davidduke.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import pl.davidduke.validation.IPN;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonDto {

    int id;

    @NotBlank
    @Size(min = 2, max = 100, message = "The first name must contain between 2 and 100 characters")
    String firstName;

    @NotBlank
    @Size(min = 2, max = 100, message = "The first name must contain between 2 and 100 characters")
    String lastName;

    @Past(message = "Birthday must be a past date")
    @NotNull(message = "Birthday should not be empty")
    LocalDate birthday;

    @IPN
    String ipn;
}
