package ca.bc.gov.educ.api.student.profile.struct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentProfileMacro extends BaseRequest{
  private String macroId;

  @NotNull(message = "macroCode cannot be null")
  @Size(max = 10)
  private String macroCode;

  @NotNull(message = "macroTypeCode cannot be null")
  @Size(max = 10)
  private String macroTypeCode;

  @NotNull(message = "macroText cannot be null")
  @Size(max = 4000)
  private String macroText;
}
