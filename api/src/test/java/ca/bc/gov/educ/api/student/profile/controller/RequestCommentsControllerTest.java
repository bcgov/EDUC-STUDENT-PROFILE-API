package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileCommentRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.support.WithMockOAuth2Scope;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RequestCommentsControllerTest extends BaseReqControllerTest {
  private static final StudentProfileEntityMapper mapper = StudentProfileEntityMapper.mapper;
  private MockMvc mockMvc;
  @Autowired
  StudentProfileCommentController controller;
  @Autowired
  StudentProfileRepository requestRepository;
  @Autowired
  StudentProfileCommentRepository repository;

  @BeforeClass
  public static void beforeClass() {

  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new RestExceptionHandler()).build();
  }

  @After
  public void after() {
    repository.deleteAll();
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE")
  public void testRetrieveRequestComments_GivenInvalidPenReqID_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(get("/" + UUID.randomUUID().toString() + "/comments")).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_STUDENT_PROFILE")
  public void testRetrieveRequestComments_GivenValidPenReqID_ShouldReturnStatusOk() throws Exception {
    StudentProfileEntity entity = requestRepository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
    String penReqId = entity.getStudentRequestID().toString();
    this.mockMvc.perform(get("/" + penReqId + "/comments")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE")
  public void testCreateRequestComments_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    StudentProfileEntity entity = requestRepository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
    String penReqId = entity.getStudentRequestID().toString();
    this.mockMvc.perform(post("/" + penReqId + "/comments").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyRequestCommentsJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_STUDENT_PROFILE")
  public void testCreateRequestComments_GivenInvalidPenReqId_ShouldReturnStatusNotFound() throws Exception {
    String penReqId = UUID.randomUUID().toString();
    this.mockMvc.perform(post("/" + penReqId + "/comments").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyRequestCommentsJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isNotFound());
  }

  private String dummyRequestCommentsJsonWithValidPenReqID(String reqId) {
    return "{\n" +
            "  \"studentRequestID\": \"" + reqId + "\",\n" +
            "  \"commentContent\": \"" + "comment1" + "\",\n" +
            "  \"commentTimestamp\": \"2020-02-09T00:00:00\"\n" +
            "}";
  }
}
