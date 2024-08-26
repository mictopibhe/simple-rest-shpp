package pl.davidduke.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.davidduke.dto.ApiErrorDto;
import pl.davidduke.dto.SubApiError;
import pl.davidduke.exception.IpnAlreadyExistsException;
import pl.davidduke.exception.PersonNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidationException(MethodArgumentNotValidException e) {
        List<SubApiError> subErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> SubApiError.builder()
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        logError(subErrors.toString(), HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .badRequest()
                .body(
                        ApiErrorDto
                                .builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Validation failed")
                                .errors(subErrors)
                                .build()
                );
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handlePersonNotFoundException(PersonNotFoundException e) {
        logError(e.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ApiErrorDto
                                .builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND)
                                .message(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(IpnAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDto> handleIpnAlreadyExistsException(IpnAlreadyExistsException e) {
        logError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .badRequest()
                .body(
                        ApiErrorDto
                                .builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST)
                                .message(e.getMessage())
                                .build()
                );
    }

    private static void logError(String message, HttpStatus status) {
        log.error("{} Status: {}", message, status);
    }
}
