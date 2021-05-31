package ca.bc.gov.educ.api.student.profile.endpoint.v1;

import ca.bc.gov.educ.api.student.profile.constants.v1.URL;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocMetadata;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocRequirement;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocTypeCode;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocument;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping(URL.BASE_URL)
public interface StudentProfileDocumentEndpoint {

  @GetMapping(URL.STUDENT_PROFILE_REQUEST_ID_DOCUMENTS + URL.DOCUMENT_ID)
  @PreAuthorize("hasAuthority('SCOPE_READ_DOCUMENT_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  StudentProfileDocument readDocument(@PathVariable String studentProfileRequestId, @PathVariable String documentID, @RequestParam(value = "includeDocData", defaultValue = "Y") String includeDocData);

  @PostMapping(URL.STUDENT_PROFILE_REQUEST_ID_DOCUMENTS)
  @PreAuthorize("hasAuthority('SCOPE_WRITE_DOCUMENT_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "200", description = "OK")})
  @ResponseStatus(CREATED)
  StudentProfileDocMetadata createDocument(@PathVariable String studentProfileRequestId, @Validated @RequestBody StudentProfileDocument penReqDocument);

  @PutMapping(URL.STUDENT_PROFILE_REQUEST_ID_DOCUMENTS + URL.DOCUMENT_ID)
  @PreAuthorize("hasAuthority('SCOPE_WRITE_DOCUMENT_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  StudentProfileDocMetadata updateDocument(@PathVariable UUID studentProfileRequestId, @PathVariable UUID documentID, @Validated @RequestBody StudentProfileDocument penReqDocument);

  @DeleteMapping(URL.STUDENT_PROFILE_REQUEST_ID_DOCUMENTS + URL.DOCUMENT_ID)
  @PreAuthorize("hasAuthority('SCOPE_DELETE_DOCUMENT_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  StudentProfileDocMetadata deleteDocument(@PathVariable String studentProfileRequestId, @PathVariable String documentID);

  @GetMapping(URL.STUDENT_PROFILE_REQUEST_ID_DOCUMENTS)
  @PreAuthorize("hasAuthority('SCOPE_READ_DOCUMENT_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<StudentProfileDocMetadata> readAllDocumentMetadata(@PathVariable String studentProfileRequestId);

  @GetMapping(URL.FILE_REQUIREMENTS)
  @PreAuthorize("hasAuthority('SCOPE_READ_DOCUMENT_REQUIREMENTS_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  StudentProfileDocRequirement getDocumentRequirements();

  @PreAuthorize("hasAuthority('SCOPE_READ_DOCUMENT_TYPES_STUDENT_PROFILE')")
  @GetMapping(URL.DOCUMENT_TYPES)
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  Iterable<StudentProfileDocTypeCode> getDocumentTypeCodes();
}
