package ca.bc.gov.educ.api.student.profile.validator;


import ca.bc.gov.educ.api.student.profile.model.v1.GenderCodeEntity;
import ca.bc.gov.educ.api.student.profile.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileService;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StudentProfilePayloadValidator {

  public static final String GENDER_CODE = "genderCode";
  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileService requestService;
  @Getter
  private final ApplicationProperties applicationProperties;

  @Autowired
  public StudentProfilePayloadValidator(StudentProfileService requestService, ApplicationProperties applicationProperties) {
    this.requestService = requestService;
    this.applicationProperties = applicationProperties;
  }

  public List<FieldError> validatePayload(StudentProfile request, boolean isCreateOperation) {
    final List<FieldError> apiValidationErrors = new ArrayList<>();
    if (isCreateOperation && request.getStudentRequestID() != null) {
      apiValidationErrors.add(createFieldError("requestID", request.getStudentRequestID(), "requestID should be null for post operation."));
    }

    if (isCreateOperation && request.getInitialSubmitDate() != null) {
      apiValidationErrors.add(createFieldError("initialSubmitDate", request.getStudentRequestID(), "initialSubmitDate should be null for post operation."));
    }
    validateGenderCode(request, apiValidationErrors);
    return apiValidationErrors;
  }

  protected void validateGenderCode(StudentProfile request, List<FieldError> apiValidationErrors) {
    if (request.getGenderCode() != null) {
      Optional<GenderCodeEntity> genderCodeEntity = requestService.findGenderCode(request.getGenderCode());
      if (genderCodeEntity.isEmpty()) {
        apiValidationErrors.add(createFieldError(GENDER_CODE, request.getGenderCode(), "Invalid Gender Code."));
      } else if (genderCodeEntity.get().getEffectiveDate() != null && genderCodeEntity.get().getEffectiveDate().isAfter(LocalDateTime.now())) {
        apiValidationErrors.add(createFieldError(GENDER_CODE, request.getGenderCode(), "Gender Code provided is not yet effective."));
      } else if (genderCodeEntity.get().getExpiryDate() != null && genderCodeEntity.get().getExpiryDate().isBefore(LocalDateTime.now())) {
        apiValidationErrors.add(createFieldError(GENDER_CODE, request.getGenderCode(), "Gender Code provided has expired."));
      }
    }
  }

  private FieldError createFieldError(String fieldName, Object rejectedValue, String message) {
    return new FieldError("studentProfileRequest", fieldName, rejectedValue, false, null, null, message);
  }

}
