package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.student.profile.mappers.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileCommentRepository;
import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RequestCommentsControllerTest extends BaseReqControllerTest {
  private static final StudentProfileEntityMapper mapper = StudentProfileEntityMapper.mapper;

  @Autowired
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
    MockitoAnnotations.openMocks(this);
  }

  @After
  public void after() {
    repository.deleteAll();
  }

  @Test
  public void testRetrieveRequestComments_GivenInvalidPenReqID_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(get("/" + UUID.randomUUID().toString() + "/comments")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE"))))
            .andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testRetrieveRequestComments_GivenValidPenReqID_ShouldReturnStatusOk() throws Exception {
    StudentProfileEntity entity = requestRepository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
    String penReqId = entity.getStudentRequestID().toString();
    this.mockMvc.perform(get("/" + penReqId + "/comments")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE"))))
            .andDo(print()).andExpect(status().isOk());
  }

  @Test
  public void testCreateRequestComments_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    StudentProfileEntity entity = requestRepository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
    String penReqId = entity.getStudentRequestID().toString();
    this.mockMvc.perform(post("/" + penReqId + "/comments")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(dummyRequestCommentsJsonWithValidPenReqID(penReqId))).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  public void testCreateRequestComments_GivenInvalidPenReqId_ShouldReturnStatusNotFound() throws Exception {
    String penReqId = UUID.randomUUID().toString();
    this.mockMvc.perform(post("/" + penReqId + "/comments")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
            .contentType(MediaType.APPLICATION_JSON)
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
