package ca.bc.gov.educ.api.student.profile.props;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class ApplicationProperties {
  public static final String CLIENT_ID = "STUDENT-PROFILE-API";
  public static final String YES = "Y";
  public static final String TRUE = "TRUE";

  @Value("${file.maxsize}")
  private int maxFileSize;
  @Value("${file.extensions}")
  private List<String> fileExtensions;

}
