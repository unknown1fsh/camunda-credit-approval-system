package com.creditapp.service;

import com.creditapp.exception.ApplicationNotFoundException;
import com.creditapp.model.CreditApplication;
import com.creditapp.repository.CreditApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Kredi başvurusu business logic service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditApplicationService {

        private final CreditApplicationRepository applicationRepository;
        private final RuntimeService runtimeService;

        /**
         * Yeni kredi başvurusu oluştur ve Camunda process'i başlat
         */
        @Transactional
        public CreditApplication createAndStartApplication(CreditApplication application) {
                String applicationId = generateApplicationId();
                application.setApplicationId(applicationId);
                application.setStatus(CreditApplication.ApplicationStatus.SUBMITTED);
                application.setCreatedAt(LocalDateTime.now());

                log.info("Starting process for credit application: {}", applicationId);

                CreditApplication saved = applicationRepository.saveAndFlush(application);
                Map<String, Object> variables = buildProcessVariables(application, applicationId);

                ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                                "credit-approval-main",
                                applicationId,
                                variables);

                saved.setProcessInstanceId(processInstance.getId());
                saved = applicationRepository.saveAndFlush(saved);

                log.info("Started process instance {} and saved application {}",
                                processInstance.getId(), applicationId);

                return saved;
        }

        private Map<String, Object> buildProcessVariables(CreditApplication application, String applicationId) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("applicationId", applicationId);
                variables.put("customerId", application.getCustomerId());
                variables.put("customerName", application.getCustomerName());
                variables.put("email", application.getEmail());
                variables.put("phone", application.getPhone());
                variables.put("loanAmount", application.getLoanAmount() != null ? application.getLoanAmount().doubleValue() : 0.0);
                variables.put("termMonths", application.getTermMonths());
                variables.put("age", application.getAge());
                variables.put("employmentStatus", application.getEmploymentStatus().name());
                variables.put("monthlyIncome", application.getMonthlyIncome() != null ? application.getMonthlyIncome().doubleValue() : null);
                variables.put("customerSegment",
                                application.getCustomerSegment() != null ? application.getCustomerSegment().name() : "BRONZE");
                variables.put("creditHistory", application.getCreditHistory() != null ? application.getCreditHistory() : 3);
                variables.put("debtToIncomeRatio",
                                application.getDebtToIncomeRatio() != null ? application.getDebtToIncomeRatio().doubleValue() : 0.0);
                variables.put("existingLoans", application.getExistingLoans() != null ? application.getExistingLoans() : 0);
                return variables;
        }

        /**
         * Başvuru durumunu güncelle
         */
        @Transactional
        public void updateApplicationStatus(String applicationId,
                        CreditApplication.ApplicationStatus status) {
                CreditApplication application = applicationRepository.findByApplicationId(applicationId)
                                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

                application.setStatus(status);
                application.setUpdatedAt(LocalDateTime.now());
                applicationRepository.save(application);

                log.info("Updated application {} status to {}", applicationId, status);
        }

        /**
         * Risk bilgilerini güncelle
         */
        @Transactional
        public void updateRiskInfo(String applicationId, String riskCategory,
                        Integer riskScore, String approverLevel) {
                CreditApplication application = applicationRepository.findByApplicationId(applicationId)
                                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

                application.setRiskCategory(CreditApplication.RiskCategory.valueOf(riskCategory));
                application.setRiskScore(riskScore);
                application.setApproverLevel(approverLevel);
                application.setUpdatedAt(LocalDateTime.now());
                applicationRepository.save(application);

                log.info("Updated risk info for application {}: category={}, score={}, approver={}",
                                applicationId, riskCategory, riskScore, approverLevel);
        }

        /**
         * Başvuruyu onayla
         */
        @Transactional
        public void approveApplication(String applicationId, String approvedBy) {
                CreditApplication application = applicationRepository.findByApplicationId(applicationId)
                                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

                application.setStatus(CreditApplication.ApplicationStatus.APPROVED);
                application.setApprovedBy(approvedBy);
                application.setApprovedAt(LocalDateTime.now());
                application.setUpdatedAt(LocalDateTime.now());
                applicationRepository.save(application);

                log.info("Approved application {} by {}", applicationId, approvedBy);
        }

        /**
         * Başvuruyu reddet
         */
        @Transactional
        public void rejectApplication(String applicationId, String rejectionReason) {
                CreditApplication application = applicationRepository.findByApplicationId(applicationId)
                                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

                application.setStatus(CreditApplication.ApplicationStatus.REJECTED);
                application.setRejectionReason(rejectionReason);
                application.setUpdatedAt(LocalDateTime.now());
                applicationRepository.save(application);

                log.info("Rejected application {}: {}", applicationId, rejectionReason);
        }

        /**
         * Başvuruyu bul
         */
        public CreditApplication findByApplicationId(String applicationId) {
                return applicationRepository.findByApplicationId(applicationId)
                                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));
        }

        /**
         * Tüm başvuruları listele
         */
        public List<CreditApplication> findAll() {
                return applicationRepository.findAll();
        }

        /**
         * Duruma göre başvuruları listele
         */
        public List<CreditApplication> findByStatus(CreditApplication.ApplicationStatus status) {
                return applicationRepository.findByStatus(status);
        }

        /**
         * Birden fazla duruma göre başvuruları listele (örn. Onay Bekleyen)
         */
        public List<CreditApplication> findByStatusIn(CreditApplication.ApplicationStatus... statuses) {
                return applicationRepository.findByStatusIn(Arrays.asList(statuses));
        }

        /**
         * Duruma göre başvuru sayısı
         */
        public long countByStatus(CreditApplication.ApplicationStatus status) {
                return applicationRepository.countByStatus(status);
        }

        /**
         * Müşterinin başvurularını listele
         */
        public List<CreditApplication> findByCustomerId(String customerId) {
                return applicationRepository.findByCustomerId(customerId);
        }

        /**
         * Unique application ID oluştur
         */
        private String generateApplicationId() {
                String year = String.valueOf(LocalDateTime.now().getYear());
                String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                return "APP-" + year + "-" + uuid;
        }
}
