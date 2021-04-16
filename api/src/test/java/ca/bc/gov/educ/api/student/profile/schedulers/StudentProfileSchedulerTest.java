package ca.bc.gov.educ.api.student.profile.schedulers;

import ca.bc.gov.educ.api.student.profile.BaseProfileRequestAPITest;
import ca.bc.gov.educ.api.student.profile.model.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.repository.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.repository.DocumentTypeCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
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
    StudentProfileEntity studentProfile = new RequestBuilder()
        .withoutRequestID().build();
    DocumentEntity document = new DocumentBuilder()
        .withoutDocumentID()
        //.withoutCreateAndUpdateUser()
        .withRequest(studentProfile)
        .withTypeCode("CAPASSPORT")
        .withData(Files.readAllBytes(new ClassPathResource(
            "../model/document-req.json", StudentProfileSchedulerTest.class).getFile().toPath()))
        .build();
    document.setCreateDate(LocalDateTime.now().minusDays(5));
    this.studentProfileRepository.save(studentProfile);
    this.repository.save(document);
  }

  @After
  public void after() {
    this.repository.deleteAll();
  }

  @Test
  public void removeBlobContentsFromUploadedDocuments() {
    this.scheduler.removeBlobContentsFromUploadedDocuments();
    val results = this.repository.findAll();
    assertThat(results).size().isEqualTo(1);
    assertThat(results.get(0)).isNotNull();
    assertThat(results.get(0).getDocumentTypeCode()).isNotBlank();
    val doc = this.profileRequestAPITestUtils.getDocumentBlobByDocumentID(results.get(0).getDocumentID());
    assertThat(doc).isNotNull();
    assertThat(doc.length).isZero();
  }
}
