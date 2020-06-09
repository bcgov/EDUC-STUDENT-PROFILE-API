package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileCommentsMapper;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileRequestEvent;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileCommentRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRequestEventRepository;
import ca.bc.gov.educ.api.student.profile.struct.Event;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileComments;
import ca.bc.gov.educ.api.student.profile.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.api.student.profile.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.student.profile.constants.EventStatus.MESSAGE_PUBLISHED;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService {
  public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing";
  public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
      " just updating the db status so that it will be polled and sent back again.";
  public static final String PAYLOAD_LOG = "payload is :: {}";
  public static final String EVENT_PAYLOAD = "event is :: {}";
  @Getter(PRIVATE)
  private final StudentProfileRepository repository;
  private static final StudentProfileEntityMapper mapper = StudentProfileEntityMapper.mapper;
  private static final StudentProfileCommentsMapper commentsMapper = StudentProfileCommentsMapper.mapper;
  @Getter(PRIVATE)
  private final StudentProfileRequestEventRepository studentProfileRequestEventRepository;
  @Getter(PRIVATE)
  private final StudentProfileCommentRepository studentProfileCommentRepository;

  @Autowired
  public EventHandlerService(final StudentProfileRepository repository, final StudentProfileRequestEventRepository studentProfileRequestEventRepository, StudentProfileCommentRepository studentProfileCommentRepository) {
    this.repository = repository;
    this.studentProfileRequestEventRepository = studentProfileRequestEventRepository;
    this.studentProfileCommentRepository = studentProfileCommentRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleEvent(Event event) {
    try {
      switch (event.getEventType()) {
        case STUDENT_PROFILE_EVENT_OUTBOX_PROCESSED:
          log.info("received outbox processed event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleOutboxProcess(event.getEventPayload());
          break;
        case UPDATE_STUDENT_PROFILE:
          log.info("received UPDATE_STUDENT_PROFILE event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleUpdate(event);
          break;
        case GET_STUDENT_PROFILE:
          log.info("received GET_STUDENT_PROFILE event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleGet(event);
          break;
        case ADD_STUDENT_PROFILE_COMMENT:
          log.info("received ADD_STUDENT_PROFILE_COMMENT event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleAddComment(event);
          break;
        default:
          log.info("silently ignoring other events.");
          break;
      }
    } catch (final Exception e) {
      log.error("Exception", e);
    }
  }

  private void handleAddComment(Event event) throws JsonProcessingException {
    var eventOptional = getStudentProfileRequestEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    StudentProfileRequestEvent requestEvent;
    if (eventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      var entity = commentsMapper.toModel(JsonUtil.getJsonObjectFromString(StudentProfileComments.class, event.getEventPayload()));
      var comments = getStudentProfileCommentRepository().findByCommentContentAndCommentTimestamp(entity.getCommentContent(), entity.getCommentTimestamp());
      if (comments.isPresent()) {
        event.setEventOutcome(EventOutcome.STUDENT_PROFILE_COMMENT_ALREADY_EXIST);
        event.setEventPayload(JsonUtil.getJsonStringFromObject(commentsMapper.toStructure(comments.get())));
      } else {
        var result = getRepository().findById(entity.getStudentRequestID());
        if (result.isPresent()) {
          entity.setStudentProfileEntity(result.get());
          entity.setCreateDate(LocalDateTime.now());
          entity.setUpdateDate(LocalDateTime.now());
          getStudentProfileCommentRepository().save(entity);
          event.setEventOutcome(EventOutcome.STUDENT_PROFILE_COMMENT_ADDED);
          event.setEventPayload(JsonUtil.getJsonStringFromObject(commentsMapper.toStructure(entity)));
        }
      }
      requestEvent = createEvent(event);
    } else {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      requestEvent = eventOptional.get();
      requestEvent.setEventStatus(DB_COMMITTED.toString());
    }
    getStudentProfileRequestEventRepository().save(requestEvent);
  }

  private void handleGet(Event event) throws JsonProcessingException {
    val eventOptional = getStudentProfileRequestEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    StudentProfileRequestEvent requestEvent;
    if (eventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      var entityOptional = getRepository().findById(UUID.fromString(event.getEventPayload())); // expect the payload contains the student profile request id.
      if (entityOptional.isPresent()) {
        var attachedEntity = entityOptional.get();
        event.setEventPayload(JsonUtil.getJsonStringFromObject(mapper.toStructure(attachedEntity)));// need to convert to structure MANDATORY otherwise jackson will break.
        event.setEventOutcome(EventOutcome.STUDENT_PROFILE_FOUND);
      } else {
        event.setEventOutcome(EventOutcome.STUDENT_PROFILE_NOT_FOUND);
      }
      requestEvent = createEvent(event);
    } else {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      requestEvent = eventOptional.get();
      requestEvent.setEventStatus(DB_COMMITTED.toString());
    }
    getStudentProfileRequestEventRepository().save(requestEvent);
  }

  private void handleUpdate(Event event) throws JsonProcessingException {
    val eventOptional = getStudentProfileRequestEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    StudentProfileRequestEvent requestEvent;
    if (eventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      var entity = mapper.toModel(JsonUtil.getJsonObjectFromString(StudentProfile.class, event.getEventPayload()));
      val entityOptional = getRepository().findById(entity.getStudentRequestID());
      if (entityOptional.isPresent()) {
        val attachedEntity = entityOptional.get();
        entity.setStudentProfileComments(attachedEntity.getStudentProfileComments()); // need to add this , otherwise child entities will be out of reference.
        BeanUtils.copyProperties(entity, attachedEntity);
        attachedEntity.setUpdateDate(LocalDateTime.now());
        getRepository().save(attachedEntity);
        event.setEventPayload(JsonUtil.getJsonStringFromObject(mapper.toStructure(attachedEntity)));// need to convert to structure MANDATORY otherwise jackson will break.
        event.setEventOutcome(EventOutcome.STUDENT_PROFILE_UPDATED);
      } else {
        event.setEventOutcome(EventOutcome.STUDENT_PROFILE_NOT_FOUND);
      }
      requestEvent = createEvent(event);
    } else {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      requestEvent = eventOptional.get();
      requestEvent.setEventStatus(DB_COMMITTED.toString());
    }
    getStudentProfileRequestEventRepository().save(requestEvent);
  }

  private void handleOutboxProcess(String eventId) {
    var eventOptional = getStudentProfileRequestEventRepository().findById(UUID.fromString(eventId));
    if (eventOptional.isPresent()) {
      var event = eventOptional.get();
      event.setEventStatus(MESSAGE_PUBLISHED.toString());
      getStudentProfileRequestEventRepository().save(event);
    }
  }


  private StudentProfileRequestEvent createEvent(final Event event) {
    return StudentProfileRequestEvent.builder()
        .createDate(LocalDateTime.now())
        .updateDate(LocalDateTime.now())
        .createUser(event.getEventType().toString()) //need to discuss what to put here.
        .updateUser(event.getEventType().toString())
        .eventPayload(event.getEventPayload())
        .eventType(event.getEventType().toString())
        .sagaId(event.getSagaId())
        .eventStatus(DB_COMMITTED.toString())
        .eventOutcome(event.getEventOutcome().toString())
        .replyChannel(event.getReplyTo())
        .build();
  }
}
