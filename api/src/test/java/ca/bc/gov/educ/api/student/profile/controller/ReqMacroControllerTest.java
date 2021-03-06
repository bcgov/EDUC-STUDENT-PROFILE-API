package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.constants.v1.URL;
import ca.bc.gov.educ.api.student.profile.controller.v1.StudentProfileMacroController;
import ca.bc.gov.educ.api.student.profile.mappers.v1.StudentProfileMacroMapper;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileMacroTypeCodeEntity;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileMacroRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileMacroTypeCodeRepository;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileMacroService;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileMacro;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("java:S2699")
public class ReqMacroControllerTest extends BaseReqControllerTest {

  private static final StudentProfileMacroMapper mapper = StudentProfileMacroMapper.mapper;
  @Autowired
  StudentProfileMacroController controller;

  @Autowired
  StudentProfileMacroService service;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  StudentProfileMacroTypeCodeRepository macroTypeCodeRepository;

  @Autowired
  StudentProfileMacroRepository macroRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    macroTypeCodeRepository.save(createReqMacroTypeCode());
  }

  @After
  public void after() {
    macroTypeCodeRepository.deleteAll();
    macroRepository.deleteAll();
  }

  @Test
  public void testRetrieveRequestMacros_ShouldReturnStatusOK() throws Exception {
    this.mockMvc.perform(get(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_MACRO)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_MACRO"))))
            .andDo(print()).andExpect(status().isOk());
  }

  @Test
  public void testRetrieveRequestMacros_GivenInvalidMacroID_ShouldReturnStatusBadReqeuest() throws Exception {
    this.mockMvc.perform(get(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_MACRO + URL.MACRO_ID, UUID.randomUUID().toString())
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_MACRO"))))
            .andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testRetrieveRequestMacros_GivenValidMacroID_ShouldReturnStatusOK() throws Exception {
    val entity = mapper.toModel(getRequestMacroEntityFromJsonString());
    entity.setMacroId(null);
    entity.setCreateDate(LocalDateTime.now());
    entity.setUpdateDate(LocalDateTime.now());
    val savedEntity = service.createMacro(entity);
    this.mockMvc.perform(get(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_MACRO + URL.MACRO_ID, savedEntity.getMacroId().toString())
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_MACRO"))))
            .andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.macroId").value(entity.getMacroId().toString()));
  }

  @Test
  public void testRetrieveRequestMacros_GivenValidMacroTypeCode_ShouldReturnStatusOK() throws Exception {
    val entity = mapper.toModel(getRequestMacroEntityFromJsonString());
    entity.setMacroId(null);
    entity.setCreateDate(LocalDateTime.now());
    entity.setUpdateDate(LocalDateTime.now());
    val savedEntity = service.createMacro(entity);
    this.mockMvc.perform(get(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_MACRO).queryParam("macroTypeCode", savedEntity.getMacroTypeCode())
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_MACRO"))))
            .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void testCreateRequestMacros_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_MACRO)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE_MACRO")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyRequestMacroJson())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  public void testCreateRequestMacros_GivenInValidPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_MACRO)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE_MACRO")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyRequestMacroJsonWithId())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateRequestMacros_GivenValidPayload_ShouldReturnStatusOK() throws Exception {
    val entity = mapper.toModel(getRequestMacroEntityFromJsonString());
    entity.setMacroId(null);
    entity.setCreateDate(LocalDateTime.now());
    entity.setUpdateDate(LocalDateTime.now());
    val savedEntity = service.createMacro(entity);
    savedEntity.setCreateDate(null);
    savedEntity.setUpdateDate(null);
    savedEntity.setMacroText("updated text");
    String jsonString = new ObjectMapper().writeValueAsString(mapper.toStructure(savedEntity));
    this.mockMvc.perform(put(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_MACRO + URL.MACRO_ID, savedEntity.getMacroId().toString())
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE_MACRO")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(jsonString)).andDo(print()).andExpect(status().isOk());
  }

  private StudentProfileMacroTypeCodeEntity createReqMacroTypeCode() {
    return StudentProfileMacroTypeCodeEntity.builder()
            .createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now())
            .createUser("TEST")
            .updateUser("TEST")
            .description("TEST")
            .displayOrder(1)
            .effectiveDate(LocalDate.now().minusDays(2))
            .expiryDate(LocalDate.now().plusDays(2))
            .label("TEST")
            .macroTypeCode("REJECT")
            .build();
  }

  protected String dummyRequestMacroJson() {
    return " {\n" +
            "    \"createUser\": \"om\",\n" +
            "    \"updateUser\": \"om\",\n" +
            "    \"macroCode\": \"hi\",\n" +
            "    \"macroTypeCode\": \"REJECT\",\n" +
            "    \"macroText\": \"hello\"\n" +
            "  }";
  }

  protected String dummyRequestMacroJsonWithId() {
    return " {\n" +
            "    \"createUser\": \"om\",\n" +
            "    \"updateUser\": \"om\",\n" +
            "    \"macroCode\": \"hi\",\n" +
            "    \"macroId\": \"7f000101-7151-1d84-8171-5187006c0000\",\n" +
            "    \"macroTypeCode\": \"REJECT\",\n" +
            "    \"macroText\": \"hello\"\n" +
            "  }";
  }

  protected StudentProfileMacro getRequestMacroEntityFromJsonString() {
    try {
      return new ObjectMapper().readValue(dummyRequestMacroJson(), StudentProfileMacro.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
