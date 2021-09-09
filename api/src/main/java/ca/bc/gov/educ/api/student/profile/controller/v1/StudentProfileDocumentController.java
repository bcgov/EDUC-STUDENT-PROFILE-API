package ca.bc.gov.educ.api.student.profile.controller.v1;

import ca.bc.gov.educ.api.student.profile.controller.BaseController;
import ca.bc.gov.educ.api.student.profile.endpoint.v1.StudentProfileDocumentEndpoint;
import ca.bc.gov.educ.api.student.profile.mappers.v1.DocumentMapper;
import ca.bc.gov.educ.api.student.profile.mappers.v1.DocumentTypeCodeMapper;
import ca.bc.gov.educ.api.student.profile.service.DocumentService;
import ca.bc.gov.educ.api.student.profile.struct.*;
import ca.bc.gov.educ.api.student.profile.validator.StudentProfileDocumentsValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class StudentProfileDocumentController extends BaseController implements StudentProfileDocumentEndpoint {

  private static final DocumentMapper mapper = DocumentMapper.mapper;

  private static final DocumentTypeCodeMapper documentTypeCodeMapper = DocumentTypeCodeMapper.mapper;

  @Getter(AccessLevel.PRIVATE)
  private final DocumentService documentService;
  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileDocumentsValidator validator;

  @Autowired
  StudentProfileDocumentController(final DocumentService documentService, final StudentProfileDocumentsValidator validator) {
    this.documentService = documentService;
    this.validator = validator;
  }

  @Override
  public StudentProfileDocument readDocument(String studentProfileRequestId, String documentID, String includeDocData) {
    return mapper.toStructure(getDocumentService().retrieveDocument(UUID.fromString(studentProfileRequestId), UUID.fromString(documentID), includeDocData));
  }

  @Override
  public StudentProfileDocMetadata createDocument(String studentProfileRequestId, StudentProfileDocument penReqDocument) {
    setAuditColumns(penReqDocument);
    val model = mapper.toModel(penReqDocument);
    getValidator().validateDocumentPayload(model, true);
    return mapper.toMetadataStructure(getDocumentService().createDocument(UUID.fromString(studentProfileRequestId), model));
  }

  @Override
  public StudentProfileDocMetadata updateDocument(UUID studentProfileRequestId, UUID documentID, StudentProfileDocument penReqDocument) {
    setAuditColumns(penReqDocument);
    val model = mapper.toModel(penReqDocument);
    getValidator().validateDocumentPayload(model, false);
    return mapper.toMetadataStructure(getDocumentService().updateDocument(studentProfileRequestId, documentID, model));
  }

  public StudentProfileDocMetadata deleteDocument(String studentProfileRequestId, String documentID) {
    return mapper.toMetadataStructure(getDocumentService().deleteDocument(UUID.fromString(studentProfileRequestId), UUID.fromString(documentID)));
  }

  public Iterable<StudentProfileDocMetadata> readAllDocumentMetadata(String studentProfileRequestId) {
    return getDocumentService().retrieveAllDocumentMetadata(UUID.fromString(studentProfileRequestId))
      .stream().map(mapper::toMetadataStructure).collect(Collectors.toList());
  }

  public StudentProfileDocRequirement getDocumentRequirements() {
    return documentService.getDocumentRequirements();
  }

  public List<StudentProfileDocTypeCode> getDocumentTypeCodes() {
    return getDocumentService().getDocumentTypeCodeList().stream()
      .map(documentTypeCodeMapper::toStructure).collect(Collectors.toList());
  }

  @Override
  public List<StudentProfileDocumentMetadata> readAllDocumentsMetadata() {
    return getDocumentService().retrieveAllDocumentsMetadata()
      .stream().map(mapper::toMetaData).collect(Collectors.toList());
  }

}
