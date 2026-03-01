package com.creditapp.repository;

import com.creditapp.model.RiskScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskScoreRepository extends JpaRepository<RiskScore, Long> {

    Optional<RiskScore> findByApplicationId(String applicationId);

    List<RiskScore> findByFraudDetected(Boolean fraudDetected);

    List<RiskScore> findByCategory(com.creditapp.model.CreditApplication.RiskCategory category);
}
