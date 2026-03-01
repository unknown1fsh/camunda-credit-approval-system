package com.creditapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Bildirim gönderme service (Email, SMS, Push)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    /**
     * Email gönder
     */
    public void sendEmail(String to, String subject, String body) {
        // Mock implementation - gerçek uygulamada SMTP veya email service kullan
        log.info("Sending email to {}: subject={}", to, subject);
        log.debug("Email body: {}", body);
        // In real app: use JavaMailSender or external service like SendGrid
    }

    /**
     * SMS gönder
     */
    public void sendSMS(String phoneNumber, String message) {
        // Mock implementation - gerçek uygulamada SMS gateway kullan
        log.info("Sending SMS to {}: message={}", phoneNumber, message);
        // In real app: use Twilio, AWS SNS, or other SMS provider
    }

    /**
     * Push notification gönder
     */
    public void sendPushNotification(String userId, String title, String message) {
        // Mock implementation - gerçek uygulamada FCM veya APNS kullan
        log.info("Sending push notification to user {}: title={}", userId, title);
        // In real app: use Firebase Cloud Messaging or Apple Push Notification Service
    }

    /**
     * Başvuru durumu bildirimi gönder
     */
    public void notifyApplicationStatus(String customerId, String email, String phone,
            String applicationId, String status) {
        String emailSubject = "Kredi Başvurunuz Hakkında - " + applicationId;
        String emailBody = String.format(
                "Sayın Müşterimiz,\n\n" +
                        "Kredi başvurunuz (No: %s) şu an %s durumundadır.\n\n" +
                        "Detaylı bilgi için lütfen sistemimize giriş yapınız.\n\n" +
                        "Saygılarımızla,\nKredi Onay Sistemi",
                applicationId, status
        );

        sendEmail(email, emailSubject, emailBody);

        String smsMessage = String.format("Başvurunuz (%s) %s durumunda. Detay için: xxx.com",
                applicationId, status);
        sendSMS(phone, smsMessage);
    }

    /**
     * Belge talep bildirimi
     */
    public void notifyDocumentRequest(String email, String phone, String applicationId,
            String documentType) {
        String subject = "Belge Talebi - " + applicationId;
        String body = String.format(
                "Sayın Müşterimiz,\n\n" +
                        "Kredi başvurunuz için %s belgesi beklenmektedir.\n" +
                        "Lütfen en kısa sürede belgeyi sisteme yükleyiniz.\n\n" +
                        "Başvuru No: %s\n\n" +
                        "Saygılarımızla",
                documentType, applicationId
        );

        sendEmail(email, subject, body);
    }

    /**
     * Onay bildirimi
     */
    public void notifyApproval(String email, String phone, String applicationId,
            double loanAmount) {
        String subject = "Kredi Başvurunuz Onaylandı! - " + applicationId;
        String body = String.format(
                "Tebrikler!\n\n" +
                        "Kredi başvurunuz (No: %s) onaylanmıştır.\n" +
                        "Onaylanan Tutar: %.2f TL\n\n" +
                        "Sözleşme imzalamak için şubemizi ziyaret edebilirsiniz.\n\n" +
                        "Saygılarımızla",
                applicationId, loanAmount
        );

        sendEmail(email, subject, body);
        sendSMS(phone, "Tebrikler! Kredi başvurunuz onaylandı. Detay: xxx.com");
    }

    /**
     * Red bildirimi
     */
    public void notifyRejection(String email, String phone, String applicationId,
            String reason) {
        String subject = "Kredi Başvurunuz Hakkında - " + applicationId;
        String body = String.format(
                "Sayın Müşterimiz,\n\n" +
                        "Üzgünüz, kredi başvurunuz (No: %s) olumsuz sonuçlanmıştır.\n" +
                        "Sebep: %s\n\n" +
                        "Detaylı bilgi için şubemizle iletişime geçebilirsiniz.\n\n" +
                        "Saygılarımızla",
                applicationId, reason
        );

        sendEmail(email, subject, body);
    }

    /**
     * Escalation bildirimi
     */
    public void notifyEscalation(String approverEmail, String applicationId,
            String fromLevel, String toLevel) {
        String subject = "Acil: Başvuru Escalation - " + applicationId;
        String body = String.format(
                "Sayın Yetkili,\n\n" +
                        "Başvuru No: %s\n" +
                        "Escalation: %s → %s\n\n" +
                        "SLA süresi aşılmıştır. Lütfen başvuruyu acilen değerlendiriniz.\n\n" +
                        "Sistem",
                applicationId, fromLevel, toLevel
        );

        sendEmail(approverEmail, subject, body);
    }
}
