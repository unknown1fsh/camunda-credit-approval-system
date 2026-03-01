package com.creditapp.repository;

import com.creditapp.model.ApprovalDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalDecisionRepository extends JpaRepository<ApprovalDecision, Long> {

    List<ApprovalDecision> findByApplicationId(String applicationId);

    Optional<ApprovalDecision> findByTaskId(String taskId);

    List<ApprovalDecision> findByApprover(String approver);

    List<ApprovalDecision> findByApplicationIdOrderByCreatedAtDesc(String applicationId);

    @Query("SELECT ad FROM ApprovalDecision ad WHERE ad.applicationId = :applicationId AND ad.escalated = true")
    List<ApprovalDecision> findEscalatedDecisions(@Param("applicationId") String applicationId);

    long countByApplicationIdAndDecision(String applicationId, ApprovalDecision.DecisionType decision);
}
