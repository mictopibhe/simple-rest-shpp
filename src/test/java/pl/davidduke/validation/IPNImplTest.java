package pl.davidduke.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class IPNImplTest {

    final IPNImpl validator = new IPNImpl();

    @Test
    void isValidShouldReturnTrueWhenIPNIsValid() {
        String ipn = "2248000331";
        assertTrue(validator.isValid(ipn, null));
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "2248000332",
            "1",
            "avdkgyatsd",
            "11111111111",
    })
    void isValidShouldReturnFalseWhenIPNIsInvalid(String value) {
        assertFalse(validator.isValid(value, null));
    }
}