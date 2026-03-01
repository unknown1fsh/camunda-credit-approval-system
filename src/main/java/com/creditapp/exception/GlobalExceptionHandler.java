package com.creditapp.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * favicon.ico ve .well-known gibi statik kaynak isteklerini sessizce 404 ile karşıla
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        String path = ex.getResourcePath();
        if (path != null && (path.equals("favicon.ico") || path.startsWith(".well-known"))) {
            return ResponseEntity.notFound().build();
        }
        log.warn("Resource not found: {}", path);
        return ResponseEntity.notFound().build();
    }

    /**
     * ApplicationNotFoundException - API için 404, Thymeleaf için error view
     */
    @ExceptionHandler(ApplicationNotFoundException.class)
    public Object handleApplicationNotFound(ApplicationNotFoundException ex, HttpServletRequest request) {
        log.warn("Application not found: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.notFound().build();
        }
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", ex.getMessage());
        return mav;
    }

    /**
     * RiskScoreNotFoundException - API için 404
     */
    @ExceptionHandler(RiskScoreNotFoundException.class)
    public ResponseEntity<Void> handleRiskScoreNotFound(RiskScoreNotFoundException ex) {
        log.warn("Risk score not found: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage());
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI() != null && request.getRequestURI().startsWith("/api/");
    }
}
