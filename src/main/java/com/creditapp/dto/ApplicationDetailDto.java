package com.creditapp.dto;

import com.creditapp.model.CreditApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Başvuru detayı için DTO - flash attribute ve view için
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetailDto {
    private String applicationId;
    private String processInstanceId;
    private String customerId;
    private String customerName;
    private String nationalId;
    private Integer age;
    private String email;
    private String phone;
    private BigDecimal loanAmount;
    private Integer termMonths;
    private String currency;
    private String employmentStatus;
    private BigDecimal monthlyIncome;
    private String employer;
    private String customerSegment;
    private String status;
    private String riskCategory;
    private Integer riskScore;
    private String approverLevel;
    private String rejectionReason;
    private LocalDateTime createdAt;

    public static ApplicationDetailDto from(CreditApplication app) {
        if (app == null) return null;
        return ApplicationDetailDto.builder()
                .applicationId(app.getApplicationId())
                .processInstanceId(app.getProcessInstanceId())
                .customerId(app.getCustomerId())
                .customerName(app.getCustomerName())
                .nationalId(app.getNationalId())
                .age(app.getAge())
                .email(app.getEmail())
                .phone(app.getPhone())
                .loanAmount(app.getLoanAmount())
                .termMonths(app.getTermMonths())
                .currency(app.getCurrency())
                .employmentStatus(app.getEmploymentStatus() != null ? app.getEmploymentStatus().name() : null)
                .monthlyIncome(app.getMonthlyIncome())
                .employer(app.getEmployer())
                .customerSegment(app.getCustomerSegment() != null ? app.getCustomerSegment().name() : null)
                .status(app.getStatus() != null ? app.getStatus().name() : null)
                .riskCategory(app.getRiskCategory() != null ? app.getRiskCategory().name() : null)
                .riskScore(app.getRiskScore())
                .approverLevel(app.getApproverLevel())
                .rejectionReason(app.getRejectionReason())
                .createdAt(app.getCreatedAt())
                .build();
    }
}
