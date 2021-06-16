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
  public static final String CORRELATION_ID = "correlationID";
  @Value("${file.maxsize}")
  private int maxFileSize;
  @Value("${file.maxEncodedSize}")
  private int maxEncodedFileSize;
  @Value("${file.extensions}")
  private List<String> fileExtensions;

  @Value("${nats.server}")
  private String server;

  @Value("${nats.maxReconnect}")
  private int maxReconnect;

  @Value("${nats.connectionName}")
  private String connectionName;

}
