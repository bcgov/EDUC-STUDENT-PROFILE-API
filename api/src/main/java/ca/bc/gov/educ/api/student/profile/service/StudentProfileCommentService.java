package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileCommentsEntity;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileCommentRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class StudentProfileCommentService {

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileRepository studentProfileRepository;

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileCommentRepository studentProfileCommentRepository;

  @Autowired
  StudentProfileCommentService(final StudentProfileRepository studentProfileRepository, final StudentProfileCommentRepository studentProfileCommentRepository) {
    this.studentProfileRepository = studentProfileRepository;
    this.studentProfileCommentRepository = studentProfileCommentRepository;
  }

  public Set<StudentProfileCommentsEntity> retrieveComments(UUID penRetrievalRequestId) {
    final Optional<StudentProfileEntity> entity = getStudentProfileRepository().findById(penRetrievalRequestId);
    if (entity.isPresent()) {
      return entity.get().getStudentProfileComments();
    }
    throw new EntityNotFoundException(StudentProfileEntity.class, "StudentProfile", penRetrievalRequestId.toString());
  }

  /**
   * Need to find the entity first as it is the parent entity and system is trying to persist the child entity so need to attach it to the parent entity otherwise hibernate will throw detach entity exception.
   *
   * @param penRetrievalRequestId    The ID of the Pen Retrieval Request.
   * @param studentProfileCommentsEntity The individual comment by staff or student.
   * @return StudentProfileCommentsEntity, the saved instance.
   */
  public StudentProfileCommentsEntity save(UUID penRetrievalRequestId, StudentProfileCommentsEntity studentProfileCommentsEntity) {
    val result = getStudentProfileRepository().findById(penRetrievalRequestId);
    if (result.isPresent()) {
      studentProfileCommentsEntity.setStudentProfileEntity(result.get());
      return getStudentProfileCommentRepository().save(studentProfileCommentsEntity);
    }
    throw new EntityNotFoundException(StudentProfileEntity.class, "StudentProfile", penRetrievalRequestId.toString());
  }

}
