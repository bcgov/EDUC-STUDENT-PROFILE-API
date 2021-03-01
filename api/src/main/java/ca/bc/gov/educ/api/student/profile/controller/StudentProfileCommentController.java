package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.endpoint.StudentProfileCommentEndpoint;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileCommentsMapper;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileCommentService;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileComments;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class StudentProfileCommentController extends BaseController implements StudentProfileCommentEndpoint {

  private static final StudentProfileCommentsMapper mapper = StudentProfileCommentsMapper.mapper;
  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileCommentService studentProfileCommentService;

  StudentProfileCommentController(@Autowired final StudentProfileCommentService studentProfileCommentService) {
    this.studentProfileCommentService = studentProfileCommentService;
  }

  @Override
  public List<StudentProfileComments> retrieveComments(String requestId) {
    return getStudentProfileCommentService().retrieveComments(UUID.fromString(requestId)).stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  @Override
  public StudentProfileComments save(String requestId, StudentProfileComments studentProfileComments) {
    setAuditColumns(studentProfileComments);
    return mapper.toStructure(getStudentProfileCommentService().save(UUID.fromString(requestId), mapper.toModel(studentProfileComments)));
  }
}
