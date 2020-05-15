package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileMacroMapper;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileMacroRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileMacroTypeCodeRepository;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileMacroService;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileMacroTypeCodeEntity;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileMacro;
import ca.bc.gov.educ.api.student.profile.support.WithMockOAuth2Scope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReqMacroControllerTest extends BaseReqControllerTest {

  private static final StudentProfileMacroMapper mapper = StudentProfileMacroMapper.mapper;
  @Autowired
  StudentProfileMacroController controller;

  @Autowired
  StudentProfileMacroService service;

  private MockMvc mockMvc;

  @Autowired
  StudentProfileMacroTypeCodeRepository macroTypeCodeRepository;

  @Autowired
  StudentProfileMacroRepository macroRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new RestExceptionHandler()).build();
    macroTypeCodeRepository.save(createReqMacroTypeCode());
  }

  @After
  public void after() {
    macroTypeCodeRepository.deleteAll();
    macroRepository.deleteAll();
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE_MACRO")
  public void testRetrieveRequestMacros_ShouldReturnStatusOK() throws Exception {
    this.mockMvc.perform(get("/student-profile-macro")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE_MACRO")
  public void testRetrieveRequestMacros_GivenInvalidMacroID_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(get("/student-profile-macro" + UUID.randomUUID().toString())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE_MACRO")
  public void testRetrieveRequestMacros_GivenValidMacroID_ShouldReturnStatusOK() throws Exception {
    val entity = mapper.toModel(getRequestMacroEntityFromJsonString());
    entity.setMacroId(null);
    entity.setCreateDate(LocalDateTime.now());
    entity.setUpdateDate(LocalDateTime.now());
    val savedEntity = service.createMacro(entity);
    this.mockMvc.perform(get("/student-profile-macro/" + savedEntity.getMacroId().toString())).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.macroId").value(entity.getMacroId().toString()));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE_MACRO")
  public void testRetrieveRequestMacros_GivenValidMacroTypeCode_ShouldReturnStatusOK() throws Exception {
    val entity = mapper.toModel(getRequestMacroEntityFromJsonString());
    entity.setMacroId(null);
    entity.setCreateDate(LocalDateTime.now());
    entity.setUpdateDate(LocalDateTime.now());
    val savedEntity = service.createMacro(entity);
    this.mockMvc.perform(get("/student-profile-macro/?macroTypeCode=" + savedEntity.getMacroTypeCode())).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE_MACRO")
  public void testCreateRequestMacros_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post("/student-profile-macro").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyRequestMacroJson())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE_MACRO")
  public void testCreateRequestMacros_GivenInValidPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/student-profile-macro").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyRequestMacroJsonWithId())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE_MACRO")
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
    this.mockMvc.perform(put("/student-profile-macro/" + savedEntity.getMacroId().toString()).contentType(MediaType.APPLICATION_JSON)
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
