package com.creditapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Risk skoru bulunamadığında fırlatılan exception.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RiskScoreNotFoundException extends RuntimeException {

    public RiskScoreNotFoundException(String applicationId) {
        super("Risk score not found: " + applicationId);
    }
}
