package com.creditapp.controller.api;

import com.creditapp.model.CreditApplication;
import com.creditapp.service.CreditApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kredi başvurusu REST API
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Kredi Başvuruları", description = "Kredi başvuru yönetimi API")
public class ApplicationApiController {

    private final CreditApplicationService applicationService;

    @GetMapping
    @Operation(summary = "Tüm başvuruları listele")
    public ResponseEntity<List<CreditApplication>> listApplications() {
        return ResponseEntity.ok(applicationService.findAll());
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "Başvuru detayı")
    public ResponseEntity<CreditApplication> getApplication(@PathVariable String applicationId) {
        CreditApplication application = applicationService.findByApplicationId(applicationId);
        return ResponseEntity.ok(application);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Müşteriye ait başvurular")
    public ResponseEntity<List<CreditApplication>> getByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(applicationService.findByCustomerId(customerId));
    }
}
