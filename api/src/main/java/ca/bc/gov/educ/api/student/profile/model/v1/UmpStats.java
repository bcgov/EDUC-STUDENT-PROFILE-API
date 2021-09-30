package ca.bc.gov.educ.api.student.profile.model.v1;

import java.time.LocalDate;

public interface UmpStats {
  LocalDate getStatusUpdateDate();
  String getStatus();
  double getAverageCompletionTime();
}
