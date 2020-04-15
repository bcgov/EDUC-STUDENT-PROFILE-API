package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.student.profile.model.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.model.DocumentTypeCodeEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.repository.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.repository.DocumentTypeCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class DocumentService {

  private final DocumentRepository documentRepository;

  private final StudentProfileRepository studentProfileRepository;

  private final DocumentTypeCodeTableRepository documentTypeCodeRepository;

  private final ApplicationProperties properties;

  @Autowired
  public DocumentService(final DocumentRepository documentRepository, final StudentProfileRepository studentProfileRepository, final DocumentTypeCodeTableRepository documentTypeCodeRepository, final ApplicationProperties properties) {
    this.documentRepository = documentRepository;
    this.studentProfileRepository = studentProfileRepository;
    this.documentTypeCodeRepository = documentTypeCodeRepository;
    this.properties = properties;
  }


  /**
   * Search for Document Metadata by id
   *
   * @param documentID the documentID to fetch the Document from DB
   * @return The Document {@link DocumentEntity} if found.
   * @throws EntityNotFoundException if no document found by the ID or penRequestID does not match.
   */
  public DocumentEntity retrieveDocumentMetadata(UUID studentProfileId, UUID documentID) {
    log.info("retrieving Document Metadata, documentID: " + documentID.toString());

    Optional<DocumentEntity> result = documentRepository.findById(documentID);
    if (!result.isPresent()) {
      throw new EntityNotFoundException(DocumentEntity.class, "documentID", documentID.toString());
    }

    DocumentEntity document = result.get();

    if (!document.getStudentProfileEntity().getStudentProfileID().equals(studentProfileId)) {
      throw new EntityNotFoundException(DocumentEntity.class, "studentProfileId", studentProfileId.toString());
    }

    return document;
  }

  /**
   * Search for Document with data by id
   *
   * @param documentID the documentID to fetch the Document from DB
   * @return The Document {@link DocumentEntity} if found.
   * @throws EntityNotFoundException if no document found by the ID or penRequestID does not match.
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public DocumentEntity retrieveDocument(UUID studentProfileId, UUID documentID, String includeDocData) {
    log.info("retrieving Document, documentID: " + documentID.toString());

    DocumentEntity document = retrieveDocumentMetadata(studentProfileId, documentID);
    // trigger lazy loading
    if (ApplicationProperties.YES.equalsIgnoreCase(includeDocData) || ApplicationProperties.TRUE.equalsIgnoreCase(includeDocData)) {
      if (document.getDocumentData().length == 0) {
        document.setFileSize(0);
      }
    } else {
      // set it to null so that map struct would not trigger lazy loading.
      document.setDocumentData(null);
    }
    return document;
  }

  /**
   * Search for all document metadata by studentProfileId
   *
   * @return {@link List<DocumentEntity> }
   */
  public List<DocumentEntity> retrieveAllDocumentMetadata(UUID studentProfileId) {
    return documentRepository.findByStudentProfileEntity(studentProfileId);
  }

  /**
   * Creates a DocumentEntity
   *
   * @param document DocumentEntity payload to be saved in DB
   * @return saved DocumentEntity.
   * @throws EntityNotFoundException if payload contains invalid parameters
   */
  public DocumentEntity createDocument(UUID penRequestId, DocumentEntity document) {
    log.info(
            "creating Document, penRequestId: " + penRequestId.toString() + ", document: " + document.toString());
    Optional<StudentProfileEntity> option = studentProfileRepository.findById(penRequestId);
    if (option.isPresent()) {
      StudentProfileEntity studentProfileEntity = option.get();
      document.setStudentProfileEntity(studentProfileEntity);
      return documentRepository.save(document);
    } else {
      throw new EntityNotFoundException(StudentProfileEntity.class, "penRequestId", penRequestId.toString());
    }
  }

  /**
   * Delete DocumentEntity by id
   *
   * @param documentID delete the document with this id.
   * @return DocumentEntity which was deleted.
   * @throws EntityNotFoundException if no entity exist by this id
   */
  public DocumentEntity deleteDocument(UUID penRequestId, UUID documentID) {
    log.info("deleting Document, documentID: " + documentID.toString());
    DocumentEntity document = retrieveDocumentMetadata(penRequestId, documentID);
    documentRepository.delete(document);
    return document;
  }

  @Cacheable("documentTypeCodeList")
  public List<DocumentTypeCodeEntity> getDocumentTypeCodeList() {
    return documentTypeCodeRepository.findAll();
  }

  /**
   * Get File Upload Requirement
   *
   * @return DocumentRequirementEntity
   */
  public StudentProfileDocRequirement getDocumentRequirements() {
    log.info("retrieving Document Requirements");
    return new StudentProfileDocRequirement(properties.getMaxFileSize(), properties.getFileExtensions());
  }

  /**
   * updates a DocumentEntity
   *
   * @param document DocumentEntity payload to be saved in DB
   * @return saved DocumentEntity.
   * @throws EntityNotFoundException if payload contains invalid parameters
   */
  public DocumentEntity updateDocument(UUID studentProfileId, UUID documentId, DocumentEntity document) {
    log.info(
            "updating Document, documentId :: {} studentProfileId :: {} :: ", documentId, studentProfileId);
    Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(documentId);
    if (documentEntityOptional.isPresent()) {
      DocumentEntity documentEntity = documentEntityOptional.get();
      StudentProfileEntity studentProfileEntity = documentEntity.getStudentProfileEntity();
      if (!studentProfileEntity.getStudentProfileID().equals(studentProfileId)) {
        throw new EntityNotFoundException(StudentProfileEntity.class, "studentProfileId", studentProfileId.toString());
      }
      documentEntity.setFileExtension(document.getFileExtension());
      documentEntity.setDocumentTypeCode(document.getDocumentTypeCode());
      documentEntity.setFileName(document.getFileName());
      documentEntity.setUpdateUser(document.getUpdateUser());
      documentEntity.setUpdateDate(document.getUpdateDate());
      return documentRepository.save(documentEntity);
    } else {
      throw new EntityNotFoundException(DocumentEntity.class, "documentId", documentId.toString());
    }
  }
}
