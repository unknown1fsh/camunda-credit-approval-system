package com.creditapp.repository;

import com.creditapp.model.CreditApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditApplicationRepository extends JpaRepository<CreditApplication, Long> {

    Optional<CreditApplication> findByApplicationId(String applicationId);

    Optional<CreditApplication> findByProcessInstanceId(String processInstanceId);

    List<CreditApplication> findByCustomerId(String customerId);

    List<CreditApplication> findByStatus(CreditApplication.ApplicationStatus status);

    long countByStatus(CreditApplication.ApplicationStatus status);

    @Query("SELECT ca FROM CreditApplication ca WHERE ca.status IN :statuses")
    List<CreditApplication> findByStatusIn(@Param("statuses") List<CreditApplication.ApplicationStatus> statuses);

    @Query("SELECT ca FROM CreditApplication ca WHERE ca.customerId = :customerId AND ca.status = :status")
    List<CreditApplication> findByCustomerIdAndStatus(
            @Param("customerId") String customerId,
            @Param("status") CreditApplication.ApplicationStatus status);

    boolean existsByApplicationId(String applicationId);
}
