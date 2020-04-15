package ca.bc.gov.educ.api.student.profile.endpoint;

import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocMetadata;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocRequirement;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocTypeCode;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocument;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/")
public interface StudentProfileDocumentEndpoint {

  @GetMapping("/{studentProfileID}/documents/{documentID}")
  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  StudentProfileDocument readDocument(@PathVariable String studentProfileID, @PathVariable String documentID, @RequestParam(value = "includeDocData", defaultValue = "Y") String includeDocData);

  @PostMapping("/{studentProfileID}/documents")
  @PreAuthorize("#oauth2.hasAnyScope('WRITE_DOCUMENT_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "200", description = "OK")})
  @ResponseStatus(CREATED)
  StudentProfileDocMetadata createDocument(@PathVariable String studentProfileID, @Validated @RequestBody StudentProfileDocument penReqDocument);

  @PutMapping("/{studentProfileID}/documents/{documentID}")
  @PreAuthorize("#oauth2.hasScope('WRITE_DOCUMENT_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  StudentProfileDocMetadata updateDocument(@PathVariable UUID studentProfileID, @PathVariable UUID documentID, @Validated @RequestBody StudentProfileDocument penReqDocument);

  @DeleteMapping("/{studentProfileID}/documents/{documentID}")
  @PreAuthorize("#oauth2.hasScope('DELETE_DOCUMENT')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  StudentProfileDocMetadata deleteDocument(@PathVariable String studentProfileID, @PathVariable String documentID);

  @GetMapping("/{studentProfileID}/documents")
  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<StudentProfileDocMetadata> readAllDocumentMetadata(@PathVariable String studentProfileID);

  @GetMapping("/file-requirements")
  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT_REQUIREMENTS_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  StudentProfileDocRequirement getDocumentRequirements();

  @PreAuthorize("#oauth2.hasScope('READ_DOCUMENT_TYPES_STUDENT_PROFILE')")
  @GetMapping("/document-types")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<StudentProfileDocTypeCode> getDocumentTypeCodes();
}
