package com.creditapp.controller.api;

import com.creditapp.listener.DataSeeder;
import com.creditapp.model.CreditApplication;
import com.creditapp.service.CreditApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Örnek veri seed API - 10 gerçekçi başvuru oluşturur.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Yönetim API")
public class SeedApiController {

    private final CreditApplicationService applicationService;

    @PostMapping("/seed")
    @Operation(summary = "10 örnek başvuru oluştur")
    public ResponseEntity<Map<String, Object>> seedApplications() {
        long countBefore = applicationService.findAll().size();
        int toCreate = Math.max(0, 10 - (int) countBefore);

        if (toCreate == 0) {
            return ResponseEntity.ok(Map.of(
                    "message", "Zaten 10 veya daha fazla başvuru mevcut.",
                    "total", countBefore,
                    "created", 0
            ));
        }

        int created = 0;
        for (CreditApplication app : DataSeeder.createStaticApplications()) {
            if (created >= toCreate) break;
            try {
                applicationService.createAndStartApplication(app);
                created++;
            } catch (Exception e) {
                // devam et
            }
        }

        long countAfter = applicationService.findAll().size();
        return ResponseEntity.ok(Map.of(
                "message", created + " adet başvuru oluşturuldu. Manager görev listesinde görebilir.",
                "created", created,
                "total", countAfter
        ));
    }
}
