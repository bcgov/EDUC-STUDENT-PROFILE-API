package ca.bc.gov.educ.api.student.profile.struct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentProfileCreate extends StudentProfile {
  private String[] documentIDs;
}
