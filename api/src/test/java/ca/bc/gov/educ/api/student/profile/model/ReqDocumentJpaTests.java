package ca.bc.gov.educ.api.student.profile.model;

import ca.bc.gov.educ.api.student.profile.StudentProfileApiResourceApplication;
import ca.bc.gov.educ.api.student.profile.model.v1.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.repository.v1.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.support.DocumentBuilder;
import ca.bc.gov.educ.api.student.profile.support.RequestBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {StudentProfileApiResourceApplication.class})
@ActiveProfiles("test")
public class ReqDocumentJpaTests {

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    private DocumentEntity document;

    private StudentProfileEntity request;

    @Before
    public void setUp() {
        this.request = studentProfileRepository.save(new RequestBuilder().withoutRequestID().build());

        this.document = new DocumentBuilder()
                            .withoutDocumentID()
                            .withRequest(this.request).build();

        document = this.repository.save(document);
    }

    @After
    public void after() {
        this.repository.deleteAll();
        this.studentProfileRepository.deleteAll();
    }

    @Test
    public void findDocumentTest() {
        Optional<DocumentEntity> myDocument = this.repository.findById(this.document.getDocumentID());
        assertThat(myDocument.isPresent()).isTrue();
        assertThat(myDocument.get().getDocumentTypeCode()).isEqualTo("BCSCPHOTO");
    }

    @Test
    public void saveDocumentTest() {
        DocumentEntity myDocument = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withRequest(this.request).build();
        DocumentEntity savedDocument = this.repository.save(myDocument);
        assertThat(savedDocument.getDocumentID()).isNotEqualTo(this.document.getDocumentID());
        assertThat(savedDocument.getRequest()).isNotNull();

        assertThat(this.repository.findById(savedDocument.getDocumentID()).isPresent()).isTrue();
    }

    @Test
    public void findDocumentByRequestTest() {
        DocumentEntity myDocument = new DocumentBuilder()
                                        .withoutDocumentID()
                                        .withRequest(this.request).build();
        DocumentEntity savedDocument = this.repository.save(myDocument);
        assertThat(savedDocument.getDocumentID()).isNotEqualTo(this.document.getDocumentID());

        assertThat(this.repository.findByRequestStudentRequestID(this.request.getStudentRequestID()).size()).isEqualTo(2);
    }

    @Test
    public void deleteDocumentTest() {
        this.repository.deleteById(this.document.getDocumentID());
        assertThat(this.repository.findById(this.document.getDocumentID()).isPresent()).isFalse();
    }
}
