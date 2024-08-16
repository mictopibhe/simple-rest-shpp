package pl.davidduke.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IpnAlreadyExistsException extends RuntimeException {
    public IpnAlreadyExistsException(String ipn) {
        super(String.format("Person with IPN %s already exists.", ipn));
    }
}
