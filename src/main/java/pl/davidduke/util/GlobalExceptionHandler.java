package pl.davidduke.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.davidduke.exception.IpnAlreadyExistsException;
import pl.davidduke.exception.PersonNotFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        logError(errors, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handlePersonNotFoundException(PersonNotFoundException e) {
        logError(e.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
    }
    
    @ExceptionHandler(IpnAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //todo: або аноташка зі статусом або статус в респонсентіті
    public ResponseEntity<String> handleIpnAlreadyExistsException(IpnAlreadyExistsException e) {
        logError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private static void logError(String message, HttpStatus status) {
        log.error("{} Status: {}", message, status);
    }
}
