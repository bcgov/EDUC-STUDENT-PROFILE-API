package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.student.profile.model.GenderCodeEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileStatusCodeEntity;
import ca.bc.gov.educ.api.student.profile.repository.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Service
public class StudentProfileService {

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileRepository studentProfileRepository;

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileStatusCodeTableRepository studentProfileStatusCodeTableRepo;

  @Getter(AccessLevel.PRIVATE)
  private final GenderCodeTableRepository genderCodeTableRepo;

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileCommentRepository studentProfilCommentRepository;

  @Getter(AccessLevel.PRIVATE)
  private final DocumentRepository documentRepository;

  @Autowired
  public StudentProfileService(final StudentProfileRepository studentProfileRepository, StudentProfileCommentRepository studentProfilCommentRepository, DocumentRepository documentRepository, final StudentProfileStatusCodeTableRepository studentProfileStatusCodeTableRepo, final GenderCodeTableRepository genderCodeTableRepo) {
    this.studentProfileRepository = studentProfileRepository;
    this.studentProfilCommentRepository = studentProfilCommentRepository;
    this.documentRepository = documentRepository;
    this.studentProfileStatusCodeTableRepo = studentProfileStatusCodeTableRepo;
    this.genderCodeTableRepo = genderCodeTableRepo;
  }

  public StudentProfileEntity retrieveStudentProfile(UUID id) {
    Optional<StudentProfileEntity> res = getStudentProfileRepository().findById(id);
    if (res.isPresent()) {
      return res.get();
    } else {
      throw new EntityNotFoundException(StudentProfileEntity.class, "studentProfileId", id.toString());
    }
  }

  /**
   * set the status to DRAFT in the initial submit of pen request.
   *
   * @param studentProfile the pen request object to be persisted in the DB.
   * @return the persisted entity.
   */
  public StudentProfileEntity createStudentProfile(StudentProfileEntity studentProfile) {
    if(studentProfile.getRecordedEmail() != null && studentProfile.getRecordedEmail().equals(studentProfile.getEmail())) {
      studentProfile.setStudentRequestStatusCode("INITREV");
      studentProfile.setInitialSubmitDate(LocalDateTime.now());
    } else {
      studentProfile.setStudentRequestStatusCode("DRAFT");
    }
    studentProfile.setStatusUpdateDate(LocalDateTime.now());
    return getStudentProfileRepository().save(studentProfile);
  }


  public Iterable<StudentProfileStatusCodeEntity> getStudentProfileStatusCodesList() {
    return getStudentProfileStatusCodeTableRepo().findAll();
  }

  public List<StudentProfileEntity> findStudentProfiles(UUID digitalID, String statusCode, String pen) {
    return getStudentProfileRepository().findProfiles(digitalID, statusCode, pen);
  }

  /**
   * Returns the full list of access channel codes
   *
   * @return {@link List<GenderCodeEntity>}
   */
  @Cacheable("genderCodes")
  public List<GenderCodeEntity> getGenderCodesList() {
    return genderCodeTableRepo.findAll();
  }

  private Map<String, GenderCodeEntity> loadGenderCodes() {
    return getGenderCodesList().stream().collect(Collectors.toMap(GenderCodeEntity::getGenderCode, genderCodeEntity -> genderCodeEntity));
  }

  public Optional<GenderCodeEntity> findGenderCode(String genderCode) {
    return Optional.ofNullable(loadGenderCodes().get(genderCode));
  }

  /**
   * This method has to add some DB fields values to the incoming to keep track of audit columns and parent child relationship.
   *
   * @param studentProfile the object which needs to be updated.
   * @return updated object.
   */
  public StudentProfileEntity updateStudentProfile(StudentProfileEntity studentProfile) {
    Optional<StudentProfileEntity> curStudentProfile = getStudentProfileRepository().findById(studentProfile.getRequestID());

    if (curStudentProfile.isPresent()) {
      StudentProfileEntity newStudentProfile = curStudentProfile.get();
      studentProfile.setStudentProfileComments(newStudentProfile.getStudentProfileComments());
      BeanUtils.copyProperties(studentProfile, newStudentProfile);
      newStudentProfile = studentProfileRepository.save(newStudentProfile);
      return newStudentProfile;
    } else {
      throw new EntityNotFoundException(StudentProfileEntity.class, "StudentProfile", studentProfile.getRequestID().toString());
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void deleteAll() {
    List<StudentProfileEntity> requests = getStudentProfileRepository().findAll();
    for (val entity : requests) {
      deleteAssociatedDocumentsAndComments(entity);
    }
    getStudentProfileRepository().deleteAll();
  }

  private void deleteAssociatedDocumentsAndComments(StudentProfileEntity entity) {
    val documents = getDocumentRepository().findByRequestRequestID(entity.getRequestID());
    if (documents != null && !documents.isEmpty()) {
      getDocumentRepository().deleteAll(documents);
    }
    if (entity.getStudentProfileComments() != null && !entity.getStudentProfileComments().isEmpty()) {
      getStudentProfilCommentRepository().deleteAll(entity.getStudentProfileComments());
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void deleteById(UUID id) {
    val entity = getStudentProfileRepository().findById(id);
    if (entity.isPresent()) {
      deleteAssociatedDocumentsAndComments(entity.get());
      getStudentProfileRepository().delete(entity.get());
    } else {
      throw new EntityNotFoundException(StudentProfileEntity.class, "RequestID", id.toString());
    }
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public CompletableFuture<Page<StudentProfileEntity>> findAll(Specification<StudentProfileEntity> requestSpecs, final Integer pageNumber, final Integer pageSize, final List<Sort.Order> sorts) {
    Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sorts));
    try {
      val result = getStudentProfileRepository().findAll(requestSpecs, paging);
      return CompletableFuture.completedFuture(result);
    } catch (final Exception ex) {
      throw new CompletionException(ex);
    }
  }
}
