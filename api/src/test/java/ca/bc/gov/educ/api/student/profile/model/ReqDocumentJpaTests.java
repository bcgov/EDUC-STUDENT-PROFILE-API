package ca.bc.gov.educ.api.student.profile.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.student.profile.repository.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.support.DocumentBuilder;
import ca.bc.gov.educ.api.student.profile.support.RequestBuilder;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReqDocumentJpaTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentRepository repository;

    private DocumentEntity document;

    private StudentProfileEntity request;

    @Before
    public void setUp() {
        this.request = new RequestBuilder()
                                            .withoutRequestID().build();
        this.document = new DocumentBuilder()
                            .withoutDocumentID()
                            .withRequest(this.request).build();

        this.entityManager.persist(this.request);
        this.entityManager.persist(this.document);
        this.entityManager.flush();
        //document = this.repository.save(document);
        this.entityManager.clear();
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

        assertThat(this.repository.findByRequestRequestID(this.request.getRequestID()).size()).isEqualTo(2);
    }

    @Test
    public void deleteDocumentTest() {
        this.repository.deleteById(this.document.getDocumentID());
        assertThat(this.repository.findById(this.document.getDocumentID()).isPresent()).isFalse();
    }
}