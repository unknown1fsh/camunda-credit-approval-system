package com.creditapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * favicon.ico isteklerini karşılar - log hatalarını önler
 * .well-known istekleri GlobalExceptionHandler'da NoResourceFoundException ile yakalanır
 */
@Controller
public class FaviconController {

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }
}
