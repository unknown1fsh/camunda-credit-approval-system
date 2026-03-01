package com.creditapp.dto;

import com.creditapp.model.CreditApplication;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Kredi başvuru formu için DTO - @ModelAttribute binding ile kullanılır.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFormDto {

    @NotBlank(message = "Müşteri ID gerekli")
    private String customerId;

    @NotBlank(message = "Ad soyad gerekli")
    private String customerName;

    @NotBlank(message = "TC Kimlik No gerekli")
    @Size(min = 11, max = 11)
    private String nationalId;

    @NotNull(message = "Yaş gerekli")
    @Min(18)
    @Max(80)
    private Integer age;

    @NotBlank(message = "E-posta gerekli")
    @Email
    private String email;

    @NotBlank(message = "Telefon gerekli")
    private String phone;

    @NotNull(message = "Kredi tutarı gerekli")
    @DecimalMin("5000")
    @DecimalMax("1000000")
    private BigDecimal loanAmount;

    @NotNull(message = "Vade gerekli")
    @Min(6)
    @Max(120)
    private Integer termMonths;

    @NotNull(message = "İstihdam durumu gerekli")
    private String employmentStatus;

    private BigDecimal monthlyIncome;
    private String employer;
    private String customerSegment;
    private Integer creditHistory;
    private Integer existingLoans;

    public CreditApplication toEntity() {
        return CreditApplication.builder()
                .customerId(customerId)
                .customerName(customerName)
                .nationalId(nationalId)
                .age(age)
                .email(email)
                .phone(phone)
                .loanAmount(loanAmount)
                .termMonths(termMonths)
                .employmentStatus(CreditApplication.EmploymentStatus.valueOf(employmentStatus))
                .monthlyIncome(monthlyIncome)
                .employer(employer)
                .customerSegment(customerSegment != null && !customerSegment.isEmpty()
                        ? CreditApplication.CustomerSegment.valueOf(customerSegment)
                        : CreditApplication.CustomerSegment.BRONZE)
                .creditHistory(creditHistory != null ? creditHistory : 3)
                .existingLoans(existingLoans != null ? existingLoans : 0)
                .build();
    }
}
