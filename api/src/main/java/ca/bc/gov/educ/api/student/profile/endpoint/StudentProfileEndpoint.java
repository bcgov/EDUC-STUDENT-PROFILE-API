package ca.bc.gov.educ.api.student.profile.endpoint;

import ca.bc.gov.educ.api.student.profile.struct.GenderCode;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileStatusCode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/")
@OpenAPIDefinition(info = @Info(title = "API for Student Profile.", description = "This CRUD API is for Student Profile, tied to a Digital ID for a particular student in BC.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_STUDENT_PROFILE", "WRITE_STUDENT_PROFILE"})})
public interface StudentProfileEndpoint {

  @PreAuthorize("#oauth2.hasScope('READ_STUDENT_PROFILE')")
  @GetMapping("/{id}")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  StudentProfile retrieveStudentProfile(@PathVariable String id);

  @PreAuthorize("#oauth2.hasScope('READ_STUDENT_PROFILE')")
  @GetMapping
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @Tag(name = "findStudentProfiles", description = "This api method will accept all or individual parameters and search the DB. if any parameter is null then it will be not included in the query.")
  Iterable<StudentProfile> findStudentProfiles(@RequestParam(name = "digitalID", required = false) String digitalID, @RequestParam(name = "status", required = false) String status, @RequestParam(name = "pen", required = false) String pen);

  @PreAuthorize("#oauth2.hasScope('WRITE_STUDENT_PROFILE')")
  @PostMapping
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "200", description = "OK")})
  @ResponseStatus(CREATED)
  @Transactional
  StudentProfile createStudentProfile(@Validated @RequestBody StudentProfile penRequest);

  @PreAuthorize("#oauth2.hasScope('WRITE_STUDENT_PROFILE')")
  @PutMapping
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @Transactional
  StudentProfile updateStudentProfile(@Validated @RequestBody StudentProfile penRequest);

  @PreAuthorize("#oauth2.hasScope('READ_STUDENT_PROFILE_STATUSES')")
  @GetMapping("/statuses")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<StudentProfileStatusCode> getStudentProfileStatusCodes();

  @PreAuthorize("#oauth2.hasScope('READ_STUDENT_PROFILE_CODES')")
  @GetMapping("/gender-codes")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<GenderCode> getGenderCodes();

}
