package ca.bc.gov.educ.api.student.profile.model;

import ca.bc.gov.educ.api.student.profile.mappers.v1.DocumentMapper;
import ca.bc.gov.educ.api.student.profile.mappers.v1.DocumentMapperImpl;
import ca.bc.gov.educ.api.student.profile.model.v1.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocMetadata;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocument;
import ca.bc.gov.educ.api.student.profile.support.DocumentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DocumentMapperImpl.class})
@AutoConfigureJsonTesters
public class ReqDocumentEntityJsonTests {
    @Autowired
    private JacksonTester<StudentProfileDocument> jsonTester;

    @Autowired
    private JacksonTester<StudentProfileDocMetadata> documentMetadataTester;

    @Autowired
    private final DocumentMapper mapper = DocumentMapper.mapper;

    private DocumentEntity document;

    @Before
    public void setUp() {
        this.document = new DocumentBuilder().build();
    }

  @Test
  public void documentSerializeTest() throws Exception {
    JsonContent<StudentProfileDocument> json = this.jsonTester.write(mapper.toStructure(this.document));

    assertThat(json).hasJsonPathStringValue("@.documentID");
    assertThat(json).extractingJsonPathStringValue("@.documentTypeCode")
      .isEqualToIgnoringCase("BCSCPHOTO");
    assertThat(json).extractingJsonPathStringValue("@.documentData")
      .isEqualToIgnoringCase("TXkgY2FyZCE=");

    assertThat(json).doesNotHaveJsonPathValue("@.request");
  }

  @Test
  public void documentMetadataSerializeTest() throws Exception {
    JsonContent<StudentProfileDocMetadata> json = this.documentMetadataTester.write(mapper.toMetadataStructure(this.document));

    assertThat(json).hasJsonPathStringValue("@.documentID");
    assertThat(json).extractingJsonPathStringValue("@.documentTypeCode")
      .isEqualToIgnoringCase("BCSCPHOTO");
    assertThat(json).doesNotHaveJsonPathValue("@.documentData");

    assertThat(json).doesNotHaveJsonPathValue("@.request");
  }

    @Test
    public void documentDeserializeTest() throws Exception {
        StudentProfileDocument penReqDocument = this.jsonTester.readObject("document.json");
        DocumentEntity documentEntity = mapper.toModel(penReqDocument);
        assertThat(documentEntity.getDocumentData()).isEqualTo("My card!".getBytes());
    }

    @Test
    public void documentDeserializeWithExtraTest() throws Exception {
        StudentProfileDocument penReqDocument = this.jsonTester.readObject("document-extra-properties.json");
        assertThat(penReqDocument.getDocumentData()).isEqualTo("TXkgY2FyZCE=");
    }

}
