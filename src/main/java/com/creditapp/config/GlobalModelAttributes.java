package com.creditapp.config;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Tüm controller'lara ortak model attribute'ları ekler.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("username")
    public String addUsername(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }
}
