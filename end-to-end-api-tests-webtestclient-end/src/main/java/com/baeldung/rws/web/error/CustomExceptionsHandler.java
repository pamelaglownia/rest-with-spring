package com.baeldung.rws.web.error;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionsHandler extends ResponseEntityExceptionHandler {

    @ResponseBody
    @ExceptionHandler({ EntityNotFoundException.class, SQLIntegrityConstraintViolationException.class })
    public void handlePersistenceNotFoundException(HttpServletResponse response, Throwable ex) throws IOException {
        // We handle entities not found in our logic, these are unexpected references to non existing entities
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

}
