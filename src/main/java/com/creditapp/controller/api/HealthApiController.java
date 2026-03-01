package com.creditapp.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Sistem durumu REST API
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Sistem", description = "Sistem durumu API")
public class HealthApiController {

    @GetMapping("/health")
    @Operation(summary = "API durumu")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "application", "Credit Approval System - Boss Fight Edition"
        ));
    }
}
