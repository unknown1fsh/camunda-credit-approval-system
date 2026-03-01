package com.creditapp.service;

import com.creditapp.model.Document;
import com.creditapp.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Belge doğrulama ve yönetim service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentVerificationService {

    private final DocumentRepository documentRepository;

    /**
     * Belge talep et
     */
    @Transactional
    public Document requestDocument(String applicationId, Document.DocumentType documentType) {
        Document document = Document.builder()
                .applicationId(applicationId)
                .documentType(documentType)
                .status(Document.DocumentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Document saved = documentRepository.save(document);
        log.info("Requested document {} for application {}",
                documentType, applicationId);
        return saved;
    }

    /**
     * Belge yükle
     */
    @Transactional
    public void uploadDocument(String applicationId, Document.DocumentType documentType,
            String fileName, String filePath) {
        Document document = documentRepository
                .findByApplicationIdAndDocumentType(applicationId, documentType)
                .orElseGet(() -> {
                    Document newDoc = Document.builder()
                            .applicationId(applicationId)
                            .documentType(documentType)
                            .build();
                    return newDoc;
                });

        document.setFileName(fileName);
        document.setFilePath(filePath);
        document.setStatus(Document.DocumentStatus.UPLOADED);
        document.setUploadedAt(LocalDateTime.now());

        if (document.getId() == null) {
            document.setCreatedAt(LocalDateTime.now());
        }

        documentRepository.save(document);
        log.info("Uploaded document {} for application {}",
                documentType, applicationId);
    }

    /**
     * Belge doğrula
     */
    @Transactional
    public boolean verifyDocument(String applicationId, Document.DocumentType documentType,
            String verifiedBy, String notes) {
        Document document = documentRepository
                .findByApplicationIdAndDocumentType(applicationId, documentType)
                .orElseThrow(() -> new RuntimeException(
                        "Document not found: " + applicationId + "/" + documentType));

        // Simple validation logic (in real app, use ML/AI or manual review)
        boolean isValid = document.getFileName() != null && !document.getFileName().isEmpty();

        document.setStatus(isValid ? Document.DocumentStatus.VERIFIED :
                Document.DocumentStatus.REJECTED);
        document.setVerifiedAt(LocalDateTime.now());
        document.setVerifiedBy(verifiedBy);
        document.setVerificationNotes(notes);

        documentRepository.save(document);
        log.info("Verified document {} for application {}: valid={}",
                documentType, applicationId, isValid);

        return isValid;
    }

    /**
     * Başvurunun tüm belgelerini listele
     */
    public List<Document> findByApplicationId(String applicationId) {
        return documentRepository.findByApplicationId(applicationId);
    }

    /**
     * Bekleyen belge sayısı
     */
    public long countPendingDocuments(String applicationId) {
        return documentRepository.countByApplicationIdAndStatus(
                applicationId, Document.DocumentStatus.PENDING);
    }

    /**
     * Tüm belgeler doğrulandı mı?
     */
    public boolean allDocumentsVerified(String applicationId) {
        List<Document> documents = documentRepository.findByApplicationId(applicationId);
        if (documents.isEmpty()) {
            return false;
        }
        return documents.stream()
                .allMatch(doc -> doc.getStatus() == Document.DocumentStatus.VERIFIED);
    }
}
