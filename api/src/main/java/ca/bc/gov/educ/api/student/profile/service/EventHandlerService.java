package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileCommentsMapper;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileCommentRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileEventRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.struct.Event;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
  private final StudentProfileRepository studentProfileRepository;
  private static final StudentProfileEntityMapper mapper = StudentProfileEntityMapper.mapper;
  private static final StudentProfileCommentsMapper prcMapper = StudentProfileCommentsMapper.mapper;
  @Getter(PRIVATE)
  private final StudentProfileEventRepository penRequestEventRepository;
  @Getter(PRIVATE)
  private final StudentProfileCommentRepository penRequestCommentRepository;

  @Autowired
  public EventHandlerService(final StudentProfileRepository studentProfileRepository, final StudentProfileEventRepository penRequestEventRepository, StudentProfileCommentRepository penRequestCommentRepository) {
    this.studentProfileRepository = studentProfileRepository;
    this.penRequestEventRepository = penRequestEventRepository;
    this.penRequestCommentRepository = penRequestCommentRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleEvent(Event event) {

  }


}
