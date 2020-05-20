package ca.bc.gov.educ.api.student.profile.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

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

import ca.bc.gov.educ.api.student.profile.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.model.GenderCodeEntity;
import ca.bc.gov.educ.api.student.profile.repository.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.repository.GenderCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileStatusCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.support.WithMockOAuth2Scope;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileStatusCodeEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RequestControllerTest extends BaseReqControllerTest {

  private static final StudentProfileEntityMapper mapper = StudentProfileEntityMapper.mapper;
  private MockMvc mockMvc;
  @Autowired
  StudentProfileController controller;

  @Autowired
  GenderCodeTableRepository genderRepo;
  
  @Autowired
  StudentProfileRepository repository;

  @Autowired
  DocumentRepository documentRepository;

  @Autowired
  StudentProfileStatusCodeTableRepository statusCodeTableRepo;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new RestExceptionHandler()).build();
    genderRepo.save(createGenderCodeData());
  }

  @After
  public void after() {
    documentRepository.deleteAll();
    repository.deleteAll();
    genderRepo.deleteAll();
  }

  private GenderCodeEntity createGenderCodeData() {
    return GenderCodeEntity.builder().genderCode("M").description("Male")
            .effectiveDate(LocalDateTime.now()).expiryDate(LocalDateTime.MAX).displayOrder(1).label("label").createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now()).createUser("TEST").updateUser("TEST").build();
  }
  

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE")
  public void testRetrieveRequest_GivenRandomID_ShouldThrowEntityNotFoundException() throws Exception {
    this.mockMvc.perform(get("/" + UUID.randomUUID())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE")
  public void testRetrieveRequest_GivenValidID_ShouldReturnOkStatus() throws Exception {
    StudentProfileEntity entity = repository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
    this.mockMvc.perform(get("/" + entity.getRequestID())).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.requestID").value(entity.getRequestID().toString()));
  }

//  @Test
//  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
//  public void testFindRequest_GivenOnlyPenInQueryParam_ShouldReturnOkStatusAndEntities() throws Exception {
//    StudentProfileEntity entity = repository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
//    this.mockMvc.perform(get("?pen" + entity.getPen())).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$[0].pen").value(entity.getPen()));
//  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE")
  public void testRetrieveRequest_GivenRandomDigitalIdAndStatusCode_ShouldReturnOkStatus() throws Exception {
    this.mockMvc.perform(get("/?digitalID=" + UUID.randomUUID() + "&status=" + "INT")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE")
  public void testCreateRequest_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJson())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE")
  public void testCreateRequest_GivenInitialSubmitDateInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithInitialSubmitDate())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE")
  public void testCreateRequest_GivenPenReqIdInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithInvalidReqID())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE")
  public void testCreateRequest_LowercaseEmailVerifiedFlag_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithInvalidEmailVerifiedFlag())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE")
  public void testUpdateRequest_GivenInvalidPenReqIDInPayload_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(put("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithInvalidReqID())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE")
  public void testUpdateRequest_GivenValidPenReqIDInPayload_ShouldReturnStatusOk() throws Exception {
    StudentProfileEntity entity = repository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
    String penReqId = entity.getRequestID().toString();
    this.mockMvc.perform(put("/").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithValidReqID(penReqId))).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE_STATUSES")
  public void testReadRequestStatus_Always_ShouldReturnStatusOkAndAllDataFromDB() throws Exception {
    statusCodeTableRepo.save(createPenReqStatus());
    this.mockMvc.perform(get("/statuses")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }


//  @Test
//  public void testHealth_GivenServerIsRunning_ShouldReturnOK() throws Exception {
//    this.mockMvc.perform(get("/health")).andDo(print()).andExpect(status().isOk())
//            .andExpect(content().string(containsString("OK")));
//  }

  private StudentProfileStatusCodeEntity createPenReqStatus() {
    StudentProfileStatusCodeEntity entity = new StudentProfileStatusCodeEntity();
    entity.setStudentProfileStatusCode("INITREV");
    entity.setDescription("Initial Review");
    entity.setDisplayOrder(1);
    entity.setEffectiveDate(LocalDateTime.now());
    entity.setLabel("Initial Review");
    entity.setCreateDate(LocalDateTime.now());
    entity.setCreateUser("TEST");
    entity.setUpdateUser("TEST");
    entity.setUpdateDate(LocalDateTime.now());
    entity.setExpiryDate(LocalDateTime.from(new GregorianCalendar(2099, Calendar.FEBRUARY, 1).toZonedDateTime()));
    return entity;
  }


}
