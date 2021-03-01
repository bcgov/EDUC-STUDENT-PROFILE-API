package ca.bc.gov.educ.api.student.profile.endpoint;

import ca.bc.gov.educ.api.student.profile.struct.StudentProfileComments;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;


@RequestMapping("/")
public interface StudentProfileCommentEndpoint {

  @PreAuthorize("hasAuthority('SCOPE_READ_STUDENT_PROFILE')")
  @GetMapping("/{requestId}/comments")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional
  List<StudentProfileComments> retrieveComments(@PathVariable String requestId);

  @PreAuthorize("hasAuthority('SCOPE_WRITE_STUDENT_PROFILE')")
  @PostMapping("/{requestId}/comments")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
  @ResponseStatus(CREATED)
  @Transactional
  StudentProfileComments save(@PathVariable String requestId, @Validated @RequestBody StudentProfileComments requestComments);


}
