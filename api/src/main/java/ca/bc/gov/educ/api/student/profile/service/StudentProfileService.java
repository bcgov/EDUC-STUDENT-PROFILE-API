package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.student.profile.model.GenderCodeEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileStatusCodeEntity;
import ca.bc.gov.educ.api.student.profile.repository.GenderCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileStatusCodeTableRepository;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentProfileService {

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileRepository studentProfileRepository;

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileStatusCodeTableRepository studentProfileStatusCodeTableRepo;

  @Getter(AccessLevel.PRIVATE)
  private final GenderCodeTableRepository genderCodeTableRepo;

  @Autowired
  public StudentProfileService(final StudentProfileRepository studentProfileRepository, final StudentProfileStatusCodeTableRepository studentProfileStatusCodeTableRepo, final GenderCodeTableRepository genderCodeTableRepo) {
    this.studentProfileRepository = studentProfileRepository;
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
      studentProfile.setRequestStatusCode("INITREV");
      studentProfile.setInitialSubmitDate(LocalDateTime.now());
    } else {
      studentProfile.setRequestStatusCode("DRAFT");
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
}
