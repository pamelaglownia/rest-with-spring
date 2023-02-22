package com.baeldung.rws.web.error;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class CustomExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ EntityNotFoundException.class, SQLIntegrityConstraintViolationException.class })
    public ErrorResponse handlePersistenceNotFoundException() throws IOException {
        // We handle entities not found in our logic, these are unexpected references to non existing entities
        return new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

}
