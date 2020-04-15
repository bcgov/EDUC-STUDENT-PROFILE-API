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

  @PreAuthorize("#oauth2.hasScope('READ_STUDENT_PROFILE')")
  @GetMapping("/{penRequestId}/comments")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional
  List<StudentProfileComments> retrieveComments(@PathVariable String penRequestId);

  @PreAuthorize("#oauth2.hasScope('WRITE_STUDENT_PROFILE')")
  @PostMapping("/{penRequestId}/comments")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
  @ResponseStatus(CREATED)
  @Transactional
  StudentProfileComments save(@PathVariable String penRequestId, @Validated @RequestBody StudentProfileComments penRequestComments);


}
