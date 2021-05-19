package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.constants.EventType;
import ca.bc.gov.educ.api.student.profile.mappers.DocumentMapper;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileCommentsMapper;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileRequestEvent;
import ca.bc.gov.educ.api.student.profile.repository.DocumentRepository;
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
import java.util.stream.Collectors;

import static ca.bc.gov.educ.api.student.profile.constants.EventStatus.MESSAGE_PUBLISHED;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService {
  public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing";
  public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
    " just updating the db status so that it will be polled and sent back again.";
  public static final String EVENT_PAYLOAD = "event is :: {}";
  @Getter(PRIVATE)
  private final StudentProfileRepository repository;
  private static final StudentProfileEntityMapper mapper = StudentProfileEntityMapper.mapper;
  private static final StudentProfileCommentsMapper commentsMapper = StudentProfileCommentsMapper.mapper;
  @Getter(PRIVATE)
  private final StudentProfileRequestEventRepository studentProfileRequestEventRepository;
  @Getter(PRIVATE)
  private final StudentProfileCommentRepository studentProfileCommentRepository;

  @Getter(PRIVATE)
  private final DocumentRepository documentRepository;
  @Autowired
  public EventHandlerService(final StudentProfileRepository repository, final StudentProfileRequestEventRepository studentProfileRequestEventRepository, StudentProfileCommentRepository studentProfileCommentRepository, DocumentRepository documentRepository) {
    this.repository = repository;
    this.studentProfileRequestEventRepository = studentProfileRequestEventRepository;
    this.studentProfileCommentRepository = studentProfileCommentRepository;
    this.documentRepository = documentRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleAddComment(Event event) throws JsonProcessingException {
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
      requestEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
    }
    getStudentProfileRequestEventRepository().save(requestEvent);
    return eventProcessed(requestEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleGet(Event event) throws JsonProcessingException {
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
      requestEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
    }
    getStudentProfileRequestEventRepository().save(requestEvent);
    return eventProcessed(requestEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleUpdate(Event event) throws JsonProcessingException {
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
      requestEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
    }
    getStudentProfileRequestEventRepository().save(requestEvent);
    return eventProcessed(requestEvent);
  }

  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public byte[] handleGetProfileRequestDocumentsMetadata(final Event event) throws JsonProcessingException {
    val documentList = this.getDocumentRepository().findByRequestStudentRequestID(UUID.fromString(event.getEventPayload())); // expect the payload contains the profile request id, which is a valid guid..
    if (documentList.isEmpty()) {
      event.setEventPayload("[]");
      event.setEventOutcome(EventOutcome.PROFILE_REQUEST_DOCUMENTS_NOT_FOUND);
    } else {
      event.setEventPayload(JsonUtil.getJsonStringFromObject(documentList.stream().map(DocumentMapper.mapper::toMetadataStructure).collect(Collectors.toList())));// need to convert to structure MANDATORY otherwise jackson will break.
      event.setEventOutcome(EventOutcome.PROFILE_REQUEST_DOCUMENTS_FOUND);
    }
    return eventProcessed(createEvent(event));
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
      .eventStatus(MESSAGE_PUBLISHED.toString())
      .eventOutcome(event.getEventOutcome().toString())
      .replyChannel(event.getReplyTo())
      .build();
  }

  private byte[] eventProcessed(StudentProfileRequestEvent requestEvent) throws JsonProcessingException {
    var event = Event.builder()
      .sagaId(requestEvent.getSagaId())
      .eventType(EventType.valueOf(requestEvent.getEventType()))
      .eventOutcome(EventOutcome.valueOf(requestEvent.getEventOutcome()))
      .eventPayload(requestEvent.getEventPayload()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }
}
