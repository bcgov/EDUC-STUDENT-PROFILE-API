package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.student.profile.model.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.repository.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.repository.DocumentTypeCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.support.DocumentBuilder;
import ca.bc.gov.educ.api.student.profile.support.DocumentTypeCodeBuilder;
import ca.bc.gov.educ.api.student.profile.support.RequestBuilder;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReqDocumentControllerTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  StudentProfileDocumentController documentController;

  @Autowired
  private DocumentRepository repository;

  @Autowired
  private StudentProfileRepository studentProfileRepository;

  @Autowired
  private DocumentTypeCodeTableRepository documentTypeCodeRepository;

  @Autowired
  private ApplicationProperties props;

  private UUID documentID;

  private UUID reqID = UUID.randomUUID();

  @Before
  public void setUp() {

    DocumentTypeCodeBuilder.setUpDocumentTypeCodes(documentTypeCodeRepository);
    StudentProfileEntity studentProfile = new RequestBuilder()
            .withoutRequestID().build();
    DocumentEntity document = new DocumentBuilder()
            .withoutDocumentID()
            //.withoutCreateAndUpdateUser()
            .withRequest(studentProfile)
            .withTypeCode("CAPASSPORT")
            .build();
    studentProfile = this.studentProfileRepository.save(studentProfile);
    document = this.repository.save(document);
    this.reqID = studentProfile.getStudentRequestID();
    this.documentID = document.getDocumentID();
  }

  @Test
  public void readDocumentTest() throws Exception {
    this.mvc.perform(get("/" + this.reqID.toString() + "/documents/" + this.documentID.toString())
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DOCUMENT_STUDENT_PROFILE")))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.documentTypeCode", is("CAPASSPORT")))
            .andExpect(jsonPath("$.documentData", is("TXkgY2FyZCE=")));
  }

  @Test
  public void createDocumentTest() throws Exception {
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                    "../model/document-req.json", ReqDocumentControllerTest.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", not(is(this.documentID.toString()))))
            .andExpect(jsonPath("$.documentTypeCode", is("BCSCPHOTO")))
            .andExpect(jsonPath("$.documentData").doesNotExist())
            .andExpect(jsonPath("$.studentRequestID").doesNotExist());
  }

  @Test
  public void testCreateDocument_GivenMandatoryFieldsNullValues_ShouldReturnStatusBadRequest() throws Exception {
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(geNullDocumentJsonAsString())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.subErrors", hasSize(4)));
  }

  @Test
  public void testCreateDocument_GivenDocumentIdInPayload_ShouldReturnStatusBadRequest() throws Exception {
    StudentProfileDocument document = getDummyDocument(UUID.randomUUID().toString());
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(getDummyDocJsonString(document))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.message", is(notNullValue())));
  }

  @Test
  public void testCreateDocument_GivenInvalidFileExtension_ShouldReturnStatusBadRequest() throws Exception {
    StudentProfileDocument document = getDummyDocument(null);
    document.setFileExtension("exe");
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(getDummyDocJsonString(document))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.message", containsStringIgnoringCase("fileExtension")));
  }

  @Test
  public void testCreateDocument_GivenInvalidDocumentTypeCode_ShouldReturnStatusBadRequest() throws Exception {
    StudentProfileDocument document = getDummyDocument(null);
    document.setDocumentTypeCode("doc");
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(getDummyDocJsonString(document))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.message", containsStringIgnoringCase("documentTypeCode")));
  }

  @Test
  public void testCreateDocument_GivenFileSizeIsMore_ShouldReturnStatusBadRequest() throws Exception {
    StudentProfileDocument document = getDummyDocument(null);
    document.setFileSize(99999999);
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(getDummyDocJsonString(document))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.message", containsStringIgnoringCase("fileSize")));
  }

  @Test
  public void testCreateDocument_GivenDocTypeNotEffective_ShouldReturnStatusBadRequest() throws Exception {
    StudentProfileDocument document = getDummyDocument(null);
    document.setDocumentTypeCode("BCeIdPHOTO");
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(getDummyDocJsonString(document))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.message", containsStringIgnoringCase("documentTypeCode")));
  }

  @Test
  public void testCreateDocument_GivenDocTypeExpired_ShouldReturnStatusBadRequest() throws Exception {
    StudentProfileDocument document = getDummyDocument(null);
    document.setDocumentTypeCode("dl");
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(getDummyDocJsonString(document))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.message", containsStringIgnoringCase("documentTypeCode")));
  }

  @Test
  public void createDocumentWithInvalidFileSizeTest() throws Exception {
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                    "../model/document-req-invalid-filesize.json", ReqDocumentControllerTest.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.message", containsStringIgnoringCase("documentData")));
  }

  @Test
  public void createDocumentWithoutDocumentDataTest() throws Exception {
    this.mvc.perform(post("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_DOCUMENT_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                    "../model/document-req-without-doc-data.json", ReqDocumentControllerTest.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  public void deleteDocumentTest() throws Exception {
    this.mvc.perform(delete("/" + this.reqID.toString() + "/documents/" + this.documentID.toString())
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "DELETE_DOCUMENT_STUDENT_PROFILE")))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.documentTypeCode", is("CAPASSPORT")))
            .andExpect(jsonPath("$.documentData").doesNotExist());


    assertThat(repository.findById(this.documentID).isPresent()).isFalse();
  }

  @Test
  public void readAllDocumentMetadataTest() throws Exception {
    this.mvc.perform(get("/" + this.reqID.toString() + "/documents")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DOCUMENT_STUDENT_PROFILE")))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.length()", is(1)))
            .andExpect(jsonPath("$.[0].documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.[0].documentTypeCode", is("CAPASSPORT")))
            .andExpect(jsonPath("$.[0].documentData").doesNotExist());
  }

  @Test
  public void getDocumentRequirementsTest() throws Exception {
    this.mvc.perform(get("/file-requirements")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DOCUMENT_REQUIREMENTS_STUDENT_PROFILE")))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.maxSize", is(props.getMaxFileSize())))
            .andExpect(jsonPath("$.extensions.length()", is(props.getFileExtensions().size())))
            .andExpect(jsonPath("$.extensions[0]", is(props.getFileExtensions().get(0))));
  }

  @Test
  public void getDocumentTypesTest() throws Exception {
    this.mvc.perform(get("/document-types")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_DOCUMENT_TYPES_STUDENT_PROFILE")))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.length()", is(4)));
  }

  private String geNullDocumentJsonAsString() {
    return "{\n" +
            "    \"documentTypeCode\":" + null + ",\n" +
            "    \"fileName\":" + null + ",\n" +
            "    \"fileExtension\":" + null + ",\n" +
            "    \"fileSize\":" + null + ",\n" +
            "    \"documentData\":" + null + "\n" +
            "}";
  }

  private StudentProfileDocument getDummyDocument(String documentId) {
    StudentProfileDocument document = new StudentProfileDocument();
    document.setDocumentID(documentId);
    document.setDocumentData("TXkgY2FyZCE=");
    document.setDocumentTypeCode("BCSCPHOTO");
    document.setFileName("card.jpg");
    document.setFileExtension("jpg");
    document.setFileSize(8);
    return document;
  }

  protected String getDummyDocJsonString(StudentProfileDocument document) {
    try {
      return new ObjectMapper().writeValueAsString(document);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


}
