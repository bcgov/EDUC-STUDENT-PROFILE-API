package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.endpoint.StudentProfileEndpoint;
import ca.bc.gov.educ.api.student.profile.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.student.profile.exception.errors.ApiError;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileGenderCodeMapper;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileStatusCodeMapper;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileService;
import ca.bc.gov.educ.api.student.profile.struct.GenderCode;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileStatusCode;
import ca.bc.gov.educ.api.student.profile.utils.UUIDUtil;
import ca.bc.gov.educ.api.student.profile.validator.StudentProfilePayloadValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@EnableResourceServer
@Slf4j
public class StudentProfileController extends BaseController implements StudentProfileEndpoint {

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfilePayloadValidator payloadValidator;
  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileService service;
  private static final StudentProfileEntityMapper mapper = StudentProfileEntityMapper.mapper;
  private static final StudentProfileStatusCodeMapper statusCodeMapper = StudentProfileStatusCodeMapper.mapper;
  private static final StudentProfileGenderCodeMapper genderCodeMapper = StudentProfileGenderCodeMapper.mapper;

  @Autowired
  StudentProfileController(final StudentProfileService studentProfile, final StudentProfilePayloadValidator payloadValidator) {
    this.service = studentProfile;
    this.payloadValidator = payloadValidator;
  }

  public StudentProfile retrieveStudentProfile(String id) {
    return mapper.toStructure(getService().retrieveStudentProfile(UUIDUtil.fromString(id)));
  }

  @Override
  public Iterable<StudentProfile> findStudentProfiles(final String digitalID, final String status, final String pen) {
    return getService().findStudentProfiles(UUIDUtil.fromString(digitalID), status, pen).stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  public StudentProfile createStudentProfile(StudentProfile studentProfile) {
    validatePayload(studentProfile, true);
    setAuditColumns(studentProfile);
    return mapper.toStructure(getService().createStudentProfile(mapper.toModel(studentProfile)));
  }

  public StudentProfile updateStudentProfile(StudentProfile studentProfile) {
    validatePayload(studentProfile, false);
    setAuditColumns(studentProfile);
    return mapper.toStructure(getService().updateStudentProfile(mapper.toModel(studentProfile)));
  }

  public List<StudentProfileStatusCode> getStudentProfileStatusCodes() {
    val studentProfileStatusCodes = new ArrayList<StudentProfileStatusCode>();
    getService().getStudentProfileStatusCodesList().forEach(element -> studentProfileStatusCodes.add(statusCodeMapper.toStructure(element)));
    return studentProfileStatusCodes;
  }

  public List<GenderCode> getGenderCodes() {
    return getService().getGenderCodesList().stream().map(genderCodeMapper::toStructure).collect(Collectors.toList());
  }


  private void validatePayload(StudentProfile studentProfile, boolean isCreateOperation) {
    val validationResult = getPayloadValidator().validatePayload(studentProfile, isCreateOperation);
    if (!validationResult.isEmpty()) {
      ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
  }

}

