package ca.bc.gov.educ.api.student.profile.schedulers;

import ca.bc.gov.educ.api.student.profile.BaseProfileRequestAPITest;
import ca.bc.gov.educ.api.student.profile.constants.EventStatus;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileRequestEvent;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileRequestEventRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class PurgeOldRecordsSchedulerTest extends BaseProfileRequestAPITest {

  @Autowired
  StudentProfileRequestEventRepository studentProfileRequestEventRepository;

  @Autowired
  PurgeOldRecordsScheduler purgeOldRecordsScheduler;


  @Test
  public void testPurgeOldRecords_givenOldRecordsPresent_shouldBeDeleted() {
    final var payload = " {\n" +
        "    \"createUser\": \"test\",\n" +
        "    \"updateUser\": \"test\",\n" +
        "    \"legalFirstName\": \"Jack\"\n" +
        "  }";

    final var yesterday = LocalDateTime.now().minusDays(1);

    this.studentProfileRequestEventRepository.save(this.getPenRequestEvent(payload, LocalDateTime.now()));

    this.studentProfileRequestEventRepository.save(this.getPenRequestEvent(payload, yesterday));

    this.purgeOldRecordsScheduler.setEventRecordStaleInDays(1);
    this.purgeOldRecordsScheduler.purgeOldRecords();

    final var servicesEvents = this.studentProfileRequestEventRepository.findAll();
    assertThat(servicesEvents).hasSize(1);
  }


  private StudentProfileRequestEvent getPenRequestEvent(final String payload, final LocalDateTime createDateTime) {
    return StudentProfileRequestEvent
      .builder()
      .eventPayloadBytes(payload.getBytes())
      .eventStatus(EventStatus.MESSAGE_PUBLISHED.toString())
      .eventType("UPDATE_STUDENT_PROFILE")
      .sagaId(UUID.randomUUID())
      .eventOutcome("STUDENT_PROFILE_UPDATED")
      .replyChannel("TEST_CHANNEL")
      .createDate(createDateTime)
      .createUser("STUDENT_PROFILE_API")
      .updateUser("STUDENT_PROFILE_API")
      .updateDate(createDateTime)
      .build();
  }
}
