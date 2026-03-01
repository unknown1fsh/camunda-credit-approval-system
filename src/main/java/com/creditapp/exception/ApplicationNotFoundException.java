package com.creditapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Kredi başvurusu bulunamadığında fırlatılan exception.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ApplicationNotFoundException extends RuntimeException {

    public ApplicationNotFoundException(String applicationId) {
        super("Application not found: " + applicationId);
    }
}
