package com.creditapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Kredi başvurusu entity
 */
@Entity
@Table(name = "credit_applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "applicationId")
public class CreditApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String applicationId; // Business key for Camunda

    @Column
    private String processInstanceId; // Camunda process instance ID (geçici null olabilir)

    // Müşteri bilgileri
    @Column(nullable = false, length = 100)
    private String customerId;

    @Column(nullable = false, length = 200)
    private String customerName;

    @Column(nullable = false, length = 11)
    private String nationalId;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    // Kredi bilgileri
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(nullable = false)
    private Integer termMonths; // Vade (ay)

    @Column(length = 10)
    private String currency = "TRY";

    // İş bilgileri
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    @Column(precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(length = 200)
    private String employer;

    // Risk bilgileri
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private CustomerSegment customerSegment; // PREMIUM, GOLD, SILVER, BRONZE

    @Column
    private Integer creditHistory; // 1-5 arası skor

    @Column(precision = 5, scale = 2)
    private BigDecimal debtToIncomeRatio;

    @Column
    private Integer existingLoans;

    // Başvuru durumu
    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private RiskCategory riskCategory;

    @Column
    private Integer riskScore;

    @Column(length = 30)
    private String approverLevel; // MANAGER, SENIOR, DIRECTOR

    // Sonuç
    @Column(length = 1000)
    private String rejectionReason;

    @Column(length = 200)
    private String approvedBy;

    @Column
    private LocalDateTime approvedAt;

    // Audit fields
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum ApplicationStatus {
        DRAFT,
        SUBMITTED,
        DOCUMENT_PENDING,
        UNDER_REVIEW,
        AWAITING_APPROVAL,
        APPROVED,
        REJECTED,
        CANCELLED
    }

    public enum EmploymentStatus {
        PERMANENT,
        TEMPORARY,
        SELF_EMPLOYED,
        UNEMPLOYED
    }

    public enum CustomerSegment {
        PREMIUM,
        GOLD,
        SILVER,
        BRONZE
    }

    public enum RiskCategory {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
