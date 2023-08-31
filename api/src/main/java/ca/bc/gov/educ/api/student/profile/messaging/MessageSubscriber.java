package ca.bc.gov.educ.api.student.profile.messaging;

import ca.bc.gov.educ.api.student.profile.helpers.LogHelper;
import ca.bc.gov.educ.api.student.profile.service.EventHandlerDelegatorService;
import ca.bc.gov.educ.api.student.profile.struct.Event;
import ca.bc.gov.educ.api.student.profile.utils.JsonUtil;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import static ca.bc.gov.educ.api.student.profile.constants.Topics.STUDENT_PROFILE_API_TOPIC;


@Component
@Slf4j
public class MessageSubscriber {

  private final EventHandlerDelegatorService eventHandlerDelegatorService;
  private final Connection connection;

  @Autowired
  public MessageSubscriber(final Connection con, EventHandlerDelegatorService eventHandlerDelegatorService) {
    this.eventHandlerDelegatorService = eventHandlerDelegatorService;
    this.connection = con;
  }

  @PostConstruct
  public void subscribe() {
    String queue = STUDENT_PROFILE_API_TOPIC.toString().replace("_", "-");
    var dispatcher = connection.createDispatcher(onMessage());
    dispatcher.subscribe(STUDENT_PROFILE_API_TOPIC.toString(), queue);
  }

  /**
   * On message message handler.
   *
   * @return the message handler
   */
  private MessageHandler onMessage() {
    return (Message message) -> {
      if (message != null) {
        log.info("Message received is :: {} ", message);
        try {
          var eventString = new String(message.getData());
          LogHelper.logMessagingEventDetails(eventString);
          var event = JsonUtil.getJsonObjectFromString(Event.class, eventString);
          eventHandlerDelegatorService.handleEvent(event);
          log.debug("Event is :: {}", event);
        } catch (final Exception e) {
          log.error("Exception ", e);
        }
      }
    };
  }


}
