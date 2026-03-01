package com.creditapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Compensation/Rollback işlemleri için service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompensationService {

    private final CreditApplicationService applicationService;
    private final NotificationService notificationService;

    /**
     * Başvuruyu geri al (compensation)
     */
    @Transactional
    public void rollbackApplication(String applicationId, String reason) {
        log.warn("Rolling back application {}: reason={}", applicationId, reason);

        try {
            // Update application status to CANCELLED
            applicationService.updateApplicationStatus(
                    applicationId,
                    com.creditapp.model.CreditApplication.ApplicationStatus.CANCELLED
            );

            // Additional rollback operations
            log.info("Application {} rolled back successfully", applicationId);
        } catch (Exception e) {
            log.error("Error rolling back application {}: {}",
                    applicationId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Ücretleri iade et
     */
    public void refundFees(String applicationId, double amount) {
        log.info("Refunding fees for application {}: amount={}",
                applicationId, amount);
        // Mock implementation - gerçek uygulamada payment gateway'e bağlan
        // In real app: call payment service to refund
    }

    /**
     * Kaynakları temizle
     */
    public void cleanupResources(String applicationId) {
        log.info("Cleaning up resources for application {}", applicationId);

        // Clean up temporary files
        // Clean up external system reservations
        // Clean up cached data
        // etc.

        log.info("Resources cleaned up for application {}", applicationId);
    }

    /**
     * Müşteriyi bilgilendir
     */
    public void notifyCustomerOfCancellation(String applicationId, String email,
            String phone, String reason) {
        log.info("Notifying customer of cancellation for application {}",
                applicationId);

        String subject = "Başvurunuz İptal Edildi - " + applicationId;
        String body = String.format(
                "Sayın Müşterimiz,\n\n" +
                        "Kredi başvurunuz (No: %s) iptal edilmiştir.\n" +
                        "Sebep: %s\n\n" +
                        "Ücret iadeniz 5-7 iş günü içinde hesabınıza yansıyacaktır.\n\n" +
                        "Saygılarımızla",
                applicationId, reason
        );

        notificationService.sendEmail(email, subject, body);
    }

    /**
     * Full compensation işlemi - tüm adımlar
     */
    @Transactional
    public void executeFullCompensation(String applicationId, String reason,
            String email, String phone) {
        log.warn("Executing full compensation for application {}", applicationId);

        // 1. Rollback application
        rollbackApplication(applicationId, reason);

        // 2. Refund fees
        refundFees(applicationId, 100.0); // Başvuru ücreti

        // 3. Cleanup resources
        cleanupResources(applicationId);

        // 4. Notify customer
        notifyCustomerOfCancellation(applicationId, email, phone, reason);

        log.info("Full compensation completed for application {}", applicationId);
    }
}
