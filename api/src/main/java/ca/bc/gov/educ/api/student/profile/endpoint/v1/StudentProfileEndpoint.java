package ca.bc.gov.educ.api.student.profile.endpoint.v1;

import ca.bc.gov.educ.api.student.profile.constants.StatsType;
import ca.bc.gov.educ.api.student.profile.constants.v1.URL;
import ca.bc.gov.educ.api.student.profile.struct.GenderCode;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileStats;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileStatusCode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping(URL.BASE_URL)
@OpenAPIDefinition(info = @Info(title = "API for Student Profile.", description = "This CRUD API is for Student Profile, tied to a Digital ID for a particular student in BC.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_STUDENT_PROFILE", "WRITE_STUDENT_PROFILE"})})
public interface StudentProfileEndpoint {

  @PreAuthorize("hasAuthority('SCOPE_READ_STUDENT_PROFILE')")
  @GetMapping(URL.STUDENT_PROFILE_REQUEST_ID)
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  StudentProfile retrieveStudentProfile(@PathVariable String studentProfileRequestId);

  @PreAuthorize("hasAuthority('SCOPE_READ_STUDENT_PROFILE')")
  @GetMapping
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @Tag(name = "findStudentProfiles", description = "This api method will accept all or individual parameters and search the DB. if any parameter is null then it will be not included in the query.")
  Iterable<StudentProfile> findStudentProfiles(@RequestParam(name = "digitalID", required = false) String digitalID, @RequestParam(name = "status", required = false) String status, @RequestParam(name = "pen", required = false) String pen);

  @PreAuthorize("hasAuthority('SCOPE_WRITE_STUDENT_PROFILE')")
  @PostMapping
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "200", description = "OK")})
  @ResponseStatus(CREATED)
  @Transactional
  StudentProfile createStudentProfile(@Validated @RequestBody StudentProfile penRequest);

  @PreAuthorize("hasAuthority('SCOPE_WRITE_STUDENT_PROFILE')")
  @PutMapping
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @Transactional
  StudentProfile updateStudentProfile(@Validated @RequestBody StudentProfile penRequest);

  @PreAuthorize("hasAuthority('SCOPE_READ_STUDENT_PROFILE_STATUSES')")
  @GetMapping(URL.STATUSES)
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<StudentProfileStatusCode> getStudentProfileStatusCodes();

  @PreAuthorize("hasAuthority('SCOPE_READ_STUDENT_PROFILE_CODES')")
  @GetMapping(URL.GENDER_CODES)
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<GenderCode> getGenderCodes();

  @DeleteMapping(URL.STUDENT_PROFILE_REQUEST_ID)
  @PreAuthorize("hasAuthority('SCOPE_DELETE_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "404", description = "NOT FOUND."), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> deleteById(@PathVariable UUID studentProfileRequestId);

  @GetMapping(URL.PAGINATED)
  @Async
  @PreAuthorize("hasAuthority('SCOPE_READ_STUDENT_PROFILE')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  CompletableFuture<Page<StudentProfile>> findAll(@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              @RequestParam(name = "sort", defaultValue = "") String sortCriteriaJson,
                                              @RequestParam(name = "searchCriteriaList", required = false) String searchCriteriaListJson);

  @GetMapping(URL.STATS)
  @PreAuthorize("hasAuthority('SCOPE_READ_STUDENT_PROFILE_STATS')")
  @Transactional(readOnly = true)
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<StudentProfileStats> getStats(@RequestParam(name = "statsType") StatsType statsType);
}
