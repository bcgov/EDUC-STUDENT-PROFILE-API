package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.messaging.MessagePublisher;
import ca.bc.gov.educ.api.student.profile.struct.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventHandlerDelegatorService {
  public static final String PAYLOAD_LOG = "payload is :: {}";
  public static final String RESPONDING_BACK_TO_NATS_ON_CHANNEL = "responding back to NATS on {} channel ";
  private final MessagePublisher messagePublisher;
  private final EventHandlerService eventHandlerService;

  @Autowired
  public EventHandlerDelegatorService(MessagePublisher messagePublisher, EventHandlerService eventHandlerService) {
    this.messagePublisher = messagePublisher;
    this.eventHandlerService = eventHandlerService;
  }

  public void handleEvent(Event event) {
    byte[] response;
    try {
      switch (event.getEventType()) {
        case UPDATE_STUDENT_PROFILE:
          log.info("received UPDATE_STUDENT_PROFILE event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = eventHandlerService.handleUpdate(event);
          log.info(RESPONDING_BACK_TO_NATS_ON_CHANNEL, event.getReplyTo());
          messagePublisher.dispatchMessage(event.getReplyTo(), response);
          break;
        case GET_STUDENT_PROFILE:
          log.info("received GET_STUDENT_PROFILE event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = eventHandlerService.handleGet(event);
          log.info(RESPONDING_BACK_TO_NATS_ON_CHANNEL, event.getReplyTo());
          messagePublisher.dispatchMessage(event.getReplyTo(), response);
          break;
        case ADD_STUDENT_PROFILE_COMMENT:
          log.info("received ADD_STUDENT_PROFILE_COMMENT event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = eventHandlerService.handleAddComment(event);
          log.info(RESPONDING_BACK_TO_NATS_ON_CHANNEL, event.getReplyTo());
          messagePublisher.dispatchMessage(event.getReplyTo(), response);
          break;
        default:
          log.info("silently ignoring other event :: {}", event);
          break;
      }
    } catch (final Exception e) {
      log.error("Exception", e);
    }
  }

}
