package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.endpoint.StudentProfileCommentEndpoint;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileCommentsMapper;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileCommentService;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileComments;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@EnableResourceServer
public class StudentProfileCommentsController extends BaseController implements StudentProfileCommentEndpoint {

  private static final StudentProfileCommentsMapper mapper = StudentProfileCommentsMapper.mapper;
  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileCommentService studentProfileCommentService;

  StudentProfileCommentsController(@Autowired final StudentProfileCommentService studentProfileCommentService) {
    this.studentProfileCommentService = studentProfileCommentService;
  }

  @Override
  public List<StudentProfileComments> retrieveComments(String studentProfileId) {
    return getStudentProfileCommentService().retrieveComments(UUID.fromString(studentProfileId)).stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  @Override
  public StudentProfileComments save(String studentProfileId, StudentProfileComments studentProfileComments) {
    setAuditColumns(studentProfileComments);
    return mapper.toStructure(getStudentProfileCommentService().save(UUID.fromString(studentProfileId), mapper.toModel(studentProfileComments)));
  }
}
