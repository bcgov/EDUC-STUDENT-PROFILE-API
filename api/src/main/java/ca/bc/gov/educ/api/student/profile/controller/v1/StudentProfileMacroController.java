package ca.bc.gov.educ.api.student.profile.controller.v1;

import ca.bc.gov.educ.api.student.profile.controller.BaseController;
import ca.bc.gov.educ.api.student.profile.endpoint.v1.StudentProfileMacroEndpoint;
import ca.bc.gov.educ.api.student.profile.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.student.profile.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.student.profile.exception.errors.ApiError;
import ca.bc.gov.educ.api.student.profile.mappers.v1.StudentProfileMacroMapper;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileMacroService;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileMacro;
import ca.bc.gov.educ.api.student.profile.validator.StudentProfileMacroPayloadValidator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@Slf4j
public class StudentProfileMacroController extends BaseController implements StudentProfileMacroEndpoint {

  private static final StudentProfileMacroMapper mapper = StudentProfileMacroMapper.mapper;
  @Getter(PRIVATE)
  private final StudentProfileMacroService studentProfileMacroService;
  @Getter(PRIVATE)
  private final StudentProfileMacroPayloadValidator studentProfileMacroPayloadValidator;

  @Autowired
  public StudentProfileMacroController(StudentProfileMacroService studentProfileMacroService, StudentProfileMacroPayloadValidator studentProfileMacroPayloadValidator) {
    this.studentProfileMacroService = studentProfileMacroService;
    this.studentProfileMacroPayloadValidator = studentProfileMacroPayloadValidator;
  }

  @Override
  public List<StudentProfileMacro> findPenReqMacros(String macroTypeCode) {
    if (StringUtils.isNotBlank(macroTypeCode)) {
      return getStudentProfileMacroService().findMacrosByMacroTypeCode(macroTypeCode).stream().map(mapper::toStructure).collect(Collectors.toList());
    }
    return getStudentProfileMacroService().findAllMacros().stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  @Override
  public StudentProfileMacro findPenReqMacroById(UUID macroId) {
    val result = getStudentProfileMacroService().getMacro(macroId);
    if (result.isPresent()) {
      return mapper.toStructure(result.get());
    }
    throw new EntityNotFoundException(StudentProfileMacro.class, macroId.toString());
  }

  @Override
  public StudentProfileMacro createPenReqMacro(StudentProfileMacro studentProfileMacro) {
    validatePayload(studentProfileMacro, true);
    setAuditColumns(studentProfileMacro);
    return mapper.toStructure(getStudentProfileMacroService().createMacro(mapper.toModel(studentProfileMacro)));
  }

  @Override
  public StudentProfileMacro updatePenReqMacro(UUID macroId, StudentProfileMacro studentProfileMacro) {
    validatePayload(studentProfileMacro, false);
    setAuditColumns(studentProfileMacro);
    return mapper.toStructure(getStudentProfileMacroService().updateMacro(macroId, mapper.toModel(studentProfileMacro)));
  }

  private void validatePayload(StudentProfileMacro studentProfileMacro, boolean isCreateOperation) {
    val validationResult = getStudentProfileMacroPayloadValidator().validatePayload(studentProfileMacro, isCreateOperation);
    if (!validationResult.isEmpty()) {
      ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
  }
}
