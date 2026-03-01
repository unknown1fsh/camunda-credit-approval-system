package com.creditapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Onay kararları ve geçmişi
 */
@Entity
@Table(name = "approval_decisions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String applicationId;

    @Column(nullable = false)
    private String taskId; // Camunda task ID

    @Column(nullable = false, length = 30)
    private String approverLevel; // MANAGER, SENIOR, DIRECTOR

    @Column(nullable = false, length = 100)
    private String approver; // Username

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DecisionType decision;

    @Column(length = 2000)
    private String comments;

    @Column(length = 1000)
    private String conditions; // Onay koşulları (varsa)

    @Column
    @Builder.Default
    private Boolean escalated = false;

    @Column
    private String escalatedFrom; // Hangi seviyeden escalate edildi

    @Column
    private String escalatedTo; // Hangi seviyeye escalate edildi

    @Column
    private LocalDateTime escalatedAt;

    @Column(nullable = false)
    private LocalDateTime decidedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (decidedAt == null) {
            decidedAt = LocalDateTime.now();
        }
    }

    // Enums
    public enum DecisionType {
        APPROVED, // Onaylandı
        REJECTED, // Reddedildi
        PENDING, // Beklemede
        CONDITIONAL_APPROVAL, // Koşullu onay
        RETURNED, // Geri döndürüldü
        ESCALATED // Escalate edildi
    }
}
