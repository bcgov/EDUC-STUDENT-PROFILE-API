package ca.bc.gov.educ.api.student.profile.struct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentProfile extends BaseRequest implements Serializable {
  private static final long serialVersionUID = 583620260139143932L;

  private String studentRequestID;
  @NotNull(message = "digitalID cannot be null")
  private String digitalID;
  @Size(max = 10)
  private String studentRequestStatusCode;
  @Size(max = 40)
  private String legalFirstName;
  @Size(max = 255)
  private String legalMiddleNames;
  @NotNull(message = "legalLastName cannot be null")
  @Size(max = 40)
  private String legalLastName;
  @NotNull(message = "dob cannot be null")
  private String dob;
  @Size(max = 1)
  private String genderCode;
  @NotNull(message = "email cannot be null")
  @Size(max = 255)
  private String email;
  @NotNull(message = "recordedPen cannot be null")
  private String recordedPen;
  @Size(max = 40)
  private String recordedLegalFirstName;
  @Size(max = 255)
  private String recordedLegalMiddleNames;
  @NotNull(message = "recordedLegalLastName cannot be null")
  @Size(max = 40)
  private String recordedLegalLastName;
  @NotNull(message = "recordedDob cannot be null")
  private String recordedDob;
  @Size(max = 1)
  private String recordedGenderCode;
  @Size(max = 255)
  private String recordedEmail;
  @Size(max = 255)
  private String reviewer;
  private String failureReason;
  private String initialSubmitDate;
  private String statusUpdateDate;
  @Size(max = 1)
  @Pattern(regexp = "[YN]")
  @NotNull(message = "emailVerified cannot be null")
  private String emailVerified;
  private String completeComment;
}
