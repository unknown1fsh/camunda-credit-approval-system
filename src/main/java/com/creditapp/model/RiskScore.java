package com.creditapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Risk skorlama bilgileri
 */
@Entity
@Table(name = "risk_scores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String applicationId;

    @Column(nullable = false)
    private Integer totalScore; // 0-100

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CreditApplication.RiskCategory category;

    // Bireysel skor bileşenleri
    @Column
    private Integer creditHistoryScore;

    @Column
    private Integer incomeScore;

    @Column
    private Integer debtRatioScore;

    @Column
    private Integer employmentScore;

    @Column
    private Integer ageScore;

    // Analiz sonuçları
    @Column
    private Integer creditAnalystScore;

    @Column(length = 1000)
    private String creditAnalystNotes;

    @Column
    private Integer riskAnalystScore;

    @Column(length = 1000)
    private String riskAnalystNotes;

    // Fraud detection
    @Column
    private Integer fraudScore; // 0-100

    @Column
    private Boolean fraudDetected = false;

    @Column(length = 1000)
    private String fraudIndicators;

    // DMN decision result
    @Column(length = 20)
    private String recommendedAction; // APPROVE, REVIEW, REJECT

    @Column
    private LocalDateTime evaluatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (evaluatedAt == null) {
            evaluatedAt = LocalDateTime.now();
        }
    }
}
