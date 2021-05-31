package ca.bc.gov.educ.api.student.profile.utils;

import ca.bc.gov.educ.api.student.profile.model.v1.GenderCodeEntity;
import ca.bc.gov.educ.api.student.profile.repository.v1.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.GenderCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Spring boot utility class to manage transaction boundary.
 */
@Component
@Profile("test")
public class ProfileRequestAPITestUtils {

  @Autowired
  private DocumentRepository documentRepository;

  @Autowired
  GenderCodeTableRepository genderRepo;

  @Autowired
  private StudentProfileRepository profileRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void cleanDB() {
    this.documentRepository.deleteAll();
    this.profileRepository.deleteAll();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void saveGenderCode(final GenderCodeEntity genderCodeEntity) {
    this.genderRepo.save(genderCodeEntity);
  }

  @Transactional
  public byte[] getDocumentBlobByDocumentID(final UUID documentID) {
    return this.documentRepository.findById(documentID).orElseThrow().getDocumentData();
  }
}
