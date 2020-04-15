package ca.bc.gov.educ.api.student.profile.endpoint;

import ca.bc.gov.educ.api.student.profile.struct.StudentProfileMacro;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/student-profile-macro")
public interface StudentProfileMacroEndpoint {

  @GetMapping
  @PreAuthorize("#oauth2.hasScope('READ_STUDENT_PROFILE_MACRO')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  List<StudentProfileMacro> findPenReqMacros(@RequestParam(value = "macroTypeCode", required = false) String macroTypeCode);

  @GetMapping("/{macroId}")
  @PreAuthorize("#oauth2.hasScope('READ_STUDENT_PROFILE_MACRO')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  StudentProfileMacro findPenReqMacroById(@PathVariable UUID macroId);

  @PostMapping
  @PreAuthorize("#oauth2.hasAnyScope('WRITE_STUDENT_PROFILE_MACRO')")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED")})
  @ResponseStatus(CREATED)
  StudentProfileMacro createPenReqMacro(@Validated @RequestBody StudentProfileMacro studentProfileMacro);

  @PutMapping("/{macroId}")
  @PreAuthorize("#oauth2.hasAnyScope('WRITE_STUDENT_PROFILE_MACRO')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  StudentProfileMacro updatePenReqMacro(@PathVariable UUID macroId, @Validated @RequestBody StudentProfileMacro studentProfileMacro);
}
