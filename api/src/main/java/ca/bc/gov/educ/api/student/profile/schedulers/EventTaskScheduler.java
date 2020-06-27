package ca.bc.gov.educ.api.student.profile.schedulers;

import ca.bc.gov.educ.api.student.profile.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.constants.EventType;
import ca.bc.gov.educ.api.student.profile.messaging.MessagePublisher;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileRequestEvent;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRequestEventRepository;
import ca.bc.gov.educ.api.student.profile.struct.Event;
import ca.bc.gov.educ.api.student.profile.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static ca.bc.gov.educ.api.student.profile.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.student.profile.constants.EventType.STUDENT_PROFILE_EVENT_OUTBOX_PROCESSED;
import static ca.bc.gov.educ.api.student.profile.constants.Topics.STUDENT_PROFILE_API_TOPIC;
import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
public class EventTaskScheduler {

  @Getter(PRIVATE)
  private final MessagePublisher messagePubSub;
  @Getter(PRIVATE)
  private final StudentProfileRequestEventRepository eventRepository;

  @Autowired
  public EventTaskScheduler(MessagePublisher messagePubSub, StudentProfileRequestEventRepository eventRepository) {
    this.messagePubSub = messagePubSub;
    this.eventRepository = eventRepository;
  }

  //@Scheduled(cron = "0/1 * * * * *")
  @SchedulerLock(name = "EventTablePoller",
      lockAtLeastFor = "900ms", lockAtMostFor = "950ms")
  public void pollEventTableAndPublish() throws InterruptedException, IOException, TimeoutException {
    var events = getEventRepository().findByEventStatus(DB_COMMITTED.toString());
    if (!events.isEmpty()) {
      for (var event : events) {
        try {
          if (event.getReplyChannel() != null) {
            getMessagePubSub().dispatchMessage(event.getReplyChannel(), eventProcessed(event));
          }
          getMessagePubSub().dispatchMessage(STUDENT_PROFILE_API_TOPIC.toString(), createOutboxEvent(event));
        } catch (InterruptedException | TimeoutException | IOException e) {
          log.error("exception occurred", e);
          throw e;
        }
      }
    }
  }

  private byte[] eventProcessed(StudentProfileRequestEvent requestEvent) throws JsonProcessingException {
    var event = Event.builder()
        .sagaId(requestEvent.getSagaId())
        .eventType(EventType.valueOf(requestEvent.getEventType()))
        .eventOutcome(EventOutcome.valueOf(requestEvent.getEventOutcome()))
        .eventPayload(requestEvent.getEventPayload()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }

  private byte[] createOutboxEvent(StudentProfileRequestEvent requestEvent) throws JsonProcessingException {
    var event = Event.builder().eventType(STUDENT_PROFILE_EVENT_OUTBOX_PROCESSED).eventPayload(requestEvent.getEventId().toString()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }
}
