package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.student.profile.model.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.repository.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.repository.DocumentTypeCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.support.DocumentBuilder;
import ca.bc.gov.educ.api.student.profile.support.DocumentTypeCodeBuilder;
import ca.bc.gov.educ.api.student.profile.support.RequestBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//import javax.transaction.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
public class ReqDocumentServiceTests {

  @Autowired
  DocumentService service;

  @Autowired
  private DocumentRepository repository;

  @Autowired
  private StudentProfileRepository requestRepository;

  @Autowired
  private DocumentTypeCodeTableRepository documentTypeCodeRepository;

  private DocumentEntity bcscPhoto;

  private StudentProfileEntity request;

  private UUID requestID;

  @Before
  public void setUp() {
    DocumentTypeCodeBuilder.setUpDocumentTypeCodes(documentTypeCodeRepository);
    this.request = new RequestBuilder()
            .withoutRequestID().build();
    this.bcscPhoto = new DocumentBuilder()
            .withoutDocumentID()
            .withRequest(this.request)
            .build();
    this.request = this.requestRepository.save(this.request);
    this.bcscPhoto = this.repository.save(this.bcscPhoto);
    this.requestID = this.request.getRequestID();
  }

  @Test
  public void createValidDocumentTest() {
    DocumentEntity document = new DocumentBuilder()
            .withoutDocumentID()
            .withoutCreateAndUpdateUser()
            .build();
    document = service.createDocument(this.requestID, document);

    assertThat(document).isNotNull();
    assertThat(document.getDocumentID()).isNotNull();
    assertThat(document.getRequest().getRequestID()).isEqualTo(requestID);
  }

  @Test
  public void retrieveDocumentMetadataTest() {
    DocumentEntity retrievedDocument = service.retrieveDocumentMetadata(this.requestID, bcscPhoto.getDocumentID());
    assertThat(retrievedDocument).isNotNull();
    assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");

    assertThat(retrievedDocument.getRequest().getRequestID()).isEqualTo(this.requestID);
  }

  @Test
  public void retrieveDocumentMetadataThrowsExceptionWhenInvalidDocumentIdGivenTest() {
    assertThatThrownBy(() -> service.retrieveDocumentMetadata(this.requestID, UUID.randomUUID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
  }

  @Test
  public void retrieveDocumentMetadataThrowsExceptionWhenInvalidRequestIdGivenTest() {
    assertThatThrownBy(() -> service.retrieveDocumentMetadata(UUID.randomUUID(), bcscPhoto.getDocumentID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
  }

  @Test
  public void retrieveDocumentDataTest() {
    DocumentEntity retrievedDocument = service.retrieveDocument(this.requestID, bcscPhoto.getDocumentID(),"Y");
    assertThat(retrievedDocument).isNotNull();
    assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");

    assertThat(retrievedDocument.getDocumentData()).isEqualTo(bcscPhoto.getDocumentData());
  }

  @Test
  public void retrieveDocumentDataTest1() {
    DocumentEntity retrievedDocument = service.retrieveDocument(this.requestID, bcscPhoto.getDocumentID(),"TRUE");
    assertThat(retrievedDocument).isNotNull();
    assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");

    assertThat(retrievedDocument.getDocumentData()).isEqualTo(bcscPhoto.getDocumentData());
  }

  @Test
  public void retrieveDocumentDataTest2() {
    DocumentEntity retrievedDocument = service.retrieveDocument(this.requestID, bcscPhoto.getDocumentID(),"N");
    assertThat(retrievedDocument).isNotNull();
    assertThat(retrievedDocument.getDocumentTypeCode()).isEqualTo("BCSCPHOTO");

    assertThat(retrievedDocument.getDocumentData()).isEqualTo(null);
  }

  @Test
  public void retrieveAllDocumentMetadataTest() {
    DocumentEntity document = new DocumentBuilder()
            .withoutDocumentID()
            .withoutCreateAndUpdateUser()
            .withRequest(this.request)
            .build();
    this.repository.save(document);

    List<DocumentEntity> documents = service.retrieveAllDocumentMetadata(this.requestID);
    assertThat(documents.size()).isEqualTo(2);
  }


  @Test
  public void deleteDocumentTest() {
    DocumentEntity deletedDocument = service.deleteDocument(this.requestID, this.bcscPhoto.getDocumentID());
    assertThat(deletedDocument).isNotNull();

    assertThatThrownBy(() -> service.retrieveDocumentMetadata(this.requestID, this.bcscPhoto.getDocumentID()))
            .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  public void deleteDocumentThrowsExceptionWhenInvalidIdGivenTest() {
    assertThatThrownBy(() -> service.deleteDocument(this.requestID, UUID.randomUUID()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("DocumentEntity");
  }
}
