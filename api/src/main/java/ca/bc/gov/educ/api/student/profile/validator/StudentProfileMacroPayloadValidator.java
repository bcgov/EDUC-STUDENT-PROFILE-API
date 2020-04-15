package ca.bc.gov.educ.api.student.profile.validator;


import ca.bc.gov.educ.api.student.profile.service.StudentProfileMacroService;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileMacro;
import lombok.Getter;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
public class StudentProfileMacroPayloadValidator {
  public static final String MACRO_TYPE_CODE = "macroTypeCode";
  @Getter(PRIVATE)
  private final StudentProfileMacroService studentProfileMacroService;

  public StudentProfileMacroPayloadValidator(StudentProfileMacroService penRequestMacroService) {
    this.studentProfileMacroService = penRequestMacroService;
  }

  public List<FieldError> validatePayload(StudentProfileMacro penRequestMacro, boolean isCreateOperation) {
    final List<FieldError> apiValidationErrors = new ArrayList<>();
    if (isCreateOperation && penRequestMacro.getMacroId() != null) {
      apiValidationErrors.add(createFieldError("macroId", penRequestMacro.getMacroId(), "macroId should be null for post operation."));
    }
    validateMacroTypeCode(penRequestMacro.getMacroTypeCode(), apiValidationErrors);
    return apiValidationErrors;
  }

  private void validateMacroTypeCode(String macroTypeCode, List<FieldError> apiValidationErrors) {
    val result = getStudentProfileMacroService().getMacroTypeCode(macroTypeCode);
    if (result.isPresent()) {
      val entity = result.get();
      if (entity.getEffectiveDate().isAfter(LocalDate.now())) {
        apiValidationErrors.add(createFieldError(MACRO_TYPE_CODE, macroTypeCode, "macroTypeCode is not yet effective."));
      } else if (entity.getExpiryDate().isBefore(LocalDate.now())) {
        apiValidationErrors.add(createFieldError(MACRO_TYPE_CODE, macroTypeCode, "macroTypeCode is expired."));
      }
    } else {
      apiValidationErrors.add(createFieldError(MACRO_TYPE_CODE, macroTypeCode, "macroTypeCode Invalid."));
    }
  }

  private FieldError createFieldError(String fieldName, Object rejectedValue, String message) {
    return new FieldError(StudentProfileMacro.class.getName(), fieldName, rejectedValue, false, null, null, message);
  }
}
