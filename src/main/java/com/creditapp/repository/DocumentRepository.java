package com.creditapp.repository;

import com.creditapp.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByApplicationId(String applicationId);

    Optional<Document> findByApplicationIdAndDocumentType(String applicationId, Document.DocumentType documentType);

    List<Document> findByApplicationIdAndStatus(String applicationId, Document.DocumentStatus status);

    @Query("SELECT d FROM Document d WHERE d.applicationId = :applicationId AND d.status = :status")
    List<Document> findPendingDocuments(
            @Param("applicationId") String applicationId,
            @Param("status") Document.DocumentStatus status);

    long countByApplicationIdAndStatus(String applicationId, Document.DocumentStatus status);
}
