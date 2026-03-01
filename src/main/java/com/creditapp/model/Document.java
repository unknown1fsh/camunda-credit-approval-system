package com.creditapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Başvuru belgeleri
 */
@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String applicationId;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @Column(length = 500)
    private String filePath; // Dosya yolu veya S3 URL

    @Column(length = 200)
    private String fileName;

    @Column
    private Long fileSize;

    @Column(length = 100)
    private String mimeType;

    @Column
    private LocalDateTime uploadedAt;

    @Column
    private LocalDateTime verifiedAt;

    @Column(length = 100)
    private String verifiedBy;

    @Column(length = 1000)
    private String verificationNotes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }

    // Enums
    public enum DocumentType {
        ID,                      // Kimlik
        INCOME_PROOF,            // Gelir belgesi
        BANK_STATEMENT_3M,       // 3 aylık banka hesap özeti
        BANK_STATEMENT_6M,       // 6 aylık banka hesap özeti
        BANK_STATEMENT_12M,      // 12 aylık banka hesap özeti
        EMPLOYMENT_LETTER,       // İşveren mektubu
        TAX_RETURN_2Y,          // 2 yıllık vergi beyannamesi
        BUSINESS_LICENSE,        // İşletme belgesi
        FINANCIAL_STATEMENT,     // Mali tablo
        COLLATERAL,             // Teminat belgesi
        GUARANTOR_INFO,         // Kefil bilgileri
        ADDITIONAL_COLLATERAL,  // Ek teminat
        ALL_DOCUMENTS           // Tümü
    }

    public enum DocumentStatus {
        PENDING,      // Bekleniyor
        UPLOADED,     // Yüklendi
        VERIFIED,     // Doğrulandı
        REJECTED,     // Reddedildi
        EXPIRED       // Süresi doldu
    }
}
