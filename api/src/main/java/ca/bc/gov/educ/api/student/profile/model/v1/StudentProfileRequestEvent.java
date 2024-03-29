package ca.bc.gov.educ.api.student.profile.model.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "STUDENT_PROFILE_REQUEST_EVENT")
@Data
@DynamicUpdate
public class StudentProfileRequestEvent {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
      @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "EVENT_ID", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  private UUID eventId;

  @Lob
  @Column(name = "EVENT_PAYLOAD")
  private byte[] eventPayloadBytes;

  @Column(name = "EVENT_STATUS")
  private String eventStatus;

  @Column(name = "EVENT_TYPE")
  private String eventType;

  @Column(name = "CREATE_USER", updatable = false)
  String createUser;

  @Column(name = "CREATE_DATE", updatable = false)
  @PastOrPresent
  LocalDateTime createDate;

  @Column(name = "UPDATE_USER")
  String updateUser;

  @Column(name = "UPDATE_DATE")
  @PastOrPresent
  LocalDateTime updateDate;

  @Column(name = "SAGA_ID", updatable = false)
  private UUID sagaId;

  @Column(name = "EVENT_OUTCOME")
  private String eventOutcome;

  @Column(name = "REPLY_CHANNEL")
  private String replyChannel;

  public String getEventPayload() {
    return new String(getEventPayloadBytes(), StandardCharsets.UTF_8);
  }

  public void setEventPayload(String eventPayload) {
    setEventPayloadBytes(eventPayload.getBytes(StandardCharsets.UTF_8));
  }

  public static class StudentProfileRequestEventBuilder {
    byte[] eventPayloadBytes;

    public StudentProfileRequestEvent.StudentProfileRequestEventBuilder eventPayload(String eventPayload) {
      this.eventPayloadBytes = eventPayload.getBytes(StandardCharsets.UTF_8);
      return this;
    }
  }
}
