package ca.bc.gov.educ.api.student.profile.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentProfileDocRequirement implements Serializable {

  private static final long serialVersionUID = 1290327040864105148L;

  private int maxSize;

  private List<String> extensions;

}
