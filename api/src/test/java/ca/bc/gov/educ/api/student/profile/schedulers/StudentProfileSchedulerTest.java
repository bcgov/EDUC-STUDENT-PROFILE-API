package ca.bc.gov.educ.api.student.profile.schedulers;

import ca.bc.gov.educ.api.student.profile.BaseProfileRequestAPITest;
import ca.bc.gov.educ.api.student.profile.model.v1.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.repository.v1.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.DocumentTypeCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.support.DocumentBuilder;
import ca.bc.gov.educ.api.student.profile.support.DocumentTypeCodeBuilder;
import ca.bc.gov.educ.api.student.profile.support.RequestBuilder;
import lombok.val;
import net.javacrumbs.shedlock.core.LockAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentProfileSchedulerTest extends BaseProfileRequestAPITest {
  @Autowired
  private DocumentRepository repository;
  @Autowired
  private StudentProfileScheduler scheduler;

  @Autowired
  private DocumentTypeCodeTableRepository documentTypeCodeRepository;
  @Autowired
  private StudentProfileRepository studentProfileRepository;

  @Before
  public void setUp() throws IOException {
    LockAssert.TestHelper.makeAllAssertsPass(true);
    DocumentTypeCodeBuilder.setUpDocumentTypeCodes(this.documentTypeCodeRepository);
    StudentProfileEntity studentProfile = new RequestBuilder().withRequestStatusCode("COMPLETED")
        .withoutRequestID().build();
    studentProfile.setStatusUpdateDate(LocalDateTime.now().minusHours(25));
    this.studentProfileRepository.save(studentProfile);
    this.saveDocument(studentProfile, 5);
    //create orphan records
    this.saveDocument(null, 1);
  }

  @After
  public void after() {
    this.repository.deleteAll();
  }

  @Test
  public void removeOrphanRecordsAndBlobContentsFromUploadedDocuments() {
    this.scheduler.removeOrphanRecordsAndBlobContentsFromUploadedDocuments();
    val results = this.repository.findAll();
    assertThat(results).size().isEqualTo(1);
    assertThat(results.get(0)).isNotNull();
    assertThat(results.get(0).getDocumentTypeCode()).isNotBlank();
    assertThat(results.get(0).getFileSize()).isZero();
    val doc = this.profileRequestAPITestUtils.getDocumentBlobByDocumentID(results.get(0).getDocumentID());
    assertThat(doc).isNull();
  }

  private void saveDocument(StudentProfileEntity studentProfile, int minusDays) throws IOException {
    DocumentEntity document = new DocumentBuilder()
      .withoutDocumentID()
      //.withoutCreateAndUpdateUser()
      .withRequest(studentProfile)
      .withTypeCode("CAPASSPORT")
      .withData(Files.readAllBytes(new ClassPathResource(
        "../model/document-req.json", StudentProfileSchedulerTest.class).getFile().toPath()))
      .build();
    document.setCreateDate(LocalDateTime.now().minusDays(minusDays));
    this.repository.save(document);
  }
}
