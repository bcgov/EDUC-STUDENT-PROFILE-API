package ca.bc.gov.educ.api.student.profile.controller;
import ca.bc.gov.educ.api.student.profile.endpoint.StudentProfileDocumentEndpoint;
import ca.bc.gov.educ.api.student.profile.mappers.DocumentMapper;
import ca.bc.gov.educ.api.student.profile.mappers.DocumentTypeCodeMapper;
import ca.bc.gov.educ.api.student.profile.service.DocumentService;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocMetadata;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocRequirement;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocTypeCode;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocument;
import ca.bc.gov.educ.api.student.profile.validator.StudentProfileDocumentsValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@EnableResourceServer
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
  public StudentProfileDocument readDocument(String studentProfileID, String documentID, String includeDocData) {
    return mapper.toStructure(getDocumentService().retrieveDocument(UUID.fromString(studentProfileID), UUID.fromString(documentID), includeDocData));
  }

  @Override
  public StudentProfileDocMetadata createDocument(String studentProfileID, StudentProfileDocument penReqDocument) {
    setAuditColumns(penReqDocument);
    val model = mapper.toModel(penReqDocument);
    getValidator().validateDocumentPayload(model, true);
    return mapper.toMetadataStructure(getDocumentService().createDocument(UUID.fromString(studentProfileID), model));
  }

  @Override
  public StudentProfileDocMetadata updateDocument(UUID studentProfileID, UUID documentID, StudentProfileDocument penReqDocument) {
    setAuditColumns(penReqDocument);
    val model = mapper.toModel(penReqDocument);
    getValidator().validateDocumentPayload(model, false);
    return mapper.toMetadataStructure(getDocumentService().updateDocument(studentProfileID, documentID, model));
  }

  public StudentProfileDocMetadata deleteDocument(String studentProfileID, String documentID) {
    return mapper.toMetadataStructure(getDocumentService().deleteDocument(UUID.fromString(studentProfileID), UUID.fromString(documentID)));
  }

  public Iterable<StudentProfileDocMetadata> readAllDocumentMetadata(String studentProfileID) {
    return getDocumentService().retrieveAllDocumentMetadata(UUID.fromString(studentProfileID))
            .stream().map(mapper::toMetadataStructure).collect(Collectors.toList());
  }

  public StudentProfileDocRequirement getDocumentRequirements() {
    return documentService.getDocumentRequirements();
  }

  public List<StudentProfileDocTypeCode> getDocumentTypeCodes() {
    return getDocumentService().getDocumentTypeCodeList().stream()
            .map(documentTypeCodeMapper::toStructure).collect(Collectors.toList());
  }

}
