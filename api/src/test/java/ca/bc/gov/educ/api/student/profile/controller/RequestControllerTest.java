package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.constants.v1.URL;
import ca.bc.gov.educ.api.student.profile.controller.v1.StudentProfileController;
import ca.bc.gov.educ.api.student.profile.filter.FilterOperation;
import ca.bc.gov.educ.api.student.profile.mappers.v1.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.model.v1.*;
import ca.bc.gov.educ.api.student.profile.repository.v1.DocumentRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.GenderCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileStatusCodeTableRepository;
import ca.bc.gov.educ.api.student.profile.struct.SearchCriteria;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import ca.bc.gov.educ.api.student.profile.struct.ValueType;
import ca.bc.gov.educ.api.student.profile.support.DocumentBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



public class RequestControllerTest extends BaseReqControllerTest {

  private static final StudentProfileEntityMapper mapper = StudentProfileEntityMapper.mapper;

  @Autowired
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
    MockitoAnnotations.openMocks(this);
    profileRequestAPITestUtils.saveGenderCode(createGenderCodeData());
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
  public void testRetrieveRequest_GivenRandomID_ShouldThrowEntityNotFoundException() throws Exception {
    this.mockMvc.perform(get(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_ID, UUID.randomUUID())
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE"))))
            .andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testRetrieveRequest_GivenValidID_ShouldReturnOkStatus() throws Exception {
    StudentProfileEntity entity = repository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
    this.mockMvc.perform(get(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_ID, entity.getStudentRequestID())
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE"))))
            .andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.studentRequestID").value(entity.getStudentRequestID().toString()));
  }

//  @Test
//  @WithMockOAuth2Scope(scope = "READ_PEN_REQUEST")
//  public void testFindRequest_GivenOnlyPenInQueryParam_ShouldReturnOkStatusAndEntities() throws Exception {
//    StudentProfileEntity entity = repository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
//    this.mockMvc.perform(get("?pen" + entity.getPen())).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$[0].pen").value(entity.getPen()));
//  }

  @Test
  public void testRetrieveRequest_GivenRandomDigitalIdAndStatusCode_ShouldReturnOkStatus() throws Exception {
    this.mockMvc.perform(get(URL.BASE_URL).queryParam("digitalID", String.valueOf(UUID.randomUUID()))
      .queryParam("status", "INT")
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE"))))
      .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  public void testCreateRequest_GivenValidPayload_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJson())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  public void testCreateRequest_GivenInitialSubmitDateInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithInitialSubmitDate())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateRequest_GivenPenReqIdInPayload_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithInvalidReqID())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateRequest_LowercaseEmailVerifiedFlag_ShouldReturnStatusBadRequest() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithInvalidEmailVerifiedFlag())).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateRequest_GivenInvalidPenReqIDInPayload_ShouldReturnStatusNotFound() throws Exception {
    this.mockMvc.perform(put(URL.BASE_URL)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithInvalidReqID())).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testUpdateRequest_GivenValidPenReqIDInPayload_ShouldReturnStatusOk() throws Exception {
    StudentProfileEntity entity = repository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
    String penReqId = entity.getStudentRequestID().toString();
    this.mockMvc.perform(put(URL.BASE_URL)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithValidReqID(penReqId))).andDo(print()).andExpect(status().isOk());
  }

  @Test
  public void testReadRequestStatus_Always_ShouldReturnStatusOkAndAllDataFromDB() throws Exception {
    statusCodeTableRepo.save(createPenReqStatus());
    this.mockMvc.perform(get(URL.BASE_URL + URL.STATUSES)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATUSES"))))
            .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
  }


//  @Test
//  public void testHealth_GivenServerIsRunning_ShouldReturnOK() throws Exception {
//    this.mockMvc.perform(get("/health")).andDo(print()).andExpect(status().isOk())
//            .andExpect(content().string(containsString("OK")));
//  }

  @Test
  public void testDeleteRequest_GivenInvalidId_ShouldReturn404() throws Exception {
    this.mockMvc.perform(delete(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_ID, UUID.randomUUID().toString())
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "DELETE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testDeleteRequest_GivenValidId_ShouldReturn204() throws Exception {
    StudentProfileEntity entity = repository.save(mapper.toModel(getStudentProfileEntityFromJsonString()));
    String reqId = entity.getStudentRequestID().toString();
    this.mockMvc.perform(delete(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_ID, reqId)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "DELETE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  public void testDeleteRequest_GivenValidIdWithAssociations_ShouldReturn204() throws Exception {
    StudentProfileEntity requestEntity = mapper.toModel(getStudentProfileEntityFromJsonString());
    requestEntity.setStudentProfileComments(createComments(requestEntity));
    StudentProfileEntity entity = repository.save(requestEntity);
    DocumentEntity document = new DocumentBuilder()
            .withoutDocumentID()
            //.withoutCreateAndUpdateUser()
            .withRequest(entity)
            .withTypeCode("CAPASSPORT")
            .build();
    this.documentRepository.save(document);
    String reqId = entity.getStudentRequestID().toString();
    this.mockMvc.perform(delete(URL.BASE_URL + URL.STUDENT_PROFILE_REQUEST_ID, reqId)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "DELETE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNoContent());
  }

  private Set<StudentProfileCommentsEntity> createComments(StudentProfileEntity studentProfileEntity) {
    Set<StudentProfileCommentsEntity> commentsEntitySet = new HashSet<>();
    StudentProfileCommentsEntity commentsEntity = new StudentProfileCommentsEntity();
    commentsEntity.setStudentProfileEntity(studentProfileEntity);
    commentsEntity.setCommentContent("hi");
    commentsEntity.setCommentTimestamp(LocalDateTime.now());
    commentsEntitySet.add(commentsEntity);
    return commentsEntitySet;
  }

  @Test
  public void testReadPenRequestPaginated_Always_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<List<StudentProfile>>() {
    });
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
      .perform(get(URL.BASE_URL + URL.PAGINATED).queryParam("pageSize", String.valueOf(2))
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE")))
        .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  public void testReadPenRequestPaginated_whenNoDataInDB_ShouldReturnStatusOk() throws Exception {
    MvcResult result = mockMvc
      .perform(get(URL.BASE_URL + URL.PAGINATED)
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE")))
        .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(0)));
  }
  @Test
  public void testReadPenRequestPaginatedWithSorting_Always_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<List<StudentProfile>>() {
    });
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    Map<String, String> sortMap = new HashMap<>();
    sortMap.put("legalLastName", "ASC");
    sortMap.put("legalFirstName", "DESC");
    String sort = new ObjectMapper().writeValueAsString(sortMap);
    MvcResult result = mockMvc
      .perform(get(URL.BASE_URL + URL.PAGINATED)
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE")))
        .param("pageNumber", "1").param("pageSize", "5").param("sort", sort)
        .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(5)));
  }

  @Test
  public void testReadPenRequestPaginated_GivenFirstNameFilter_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<List<StudentProfile>>() {
    });
    SearchCriteria criteria = SearchCriteria.builder().key("legalFirstName").operation(FilterOperation.EQUAL).value("Katina").valueType(ValueType.STRING).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
      .perform(get(URL.BASE_URL + URL.PAGINATED)
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE")))
        .param("searchCriteriaList", criteriaJSON)
        .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  public void testReadPenRequestPaginated_GivenLastNameFilter_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<List<StudentProfile>>() {
    });
    SearchCriteria criteria = SearchCriteria.builder().key("legalLastName").operation(FilterOperation.EQUAL).value("Medling").valueType(ValueType.STRING).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
      .perform(get(URL.BASE_URL + URL.PAGINATED)
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE")))
        .param("searchCriteriaList", criteriaJSON)
        .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  public void testReadPenRequestPaginated_GivenSubmitDateBetween_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<List<StudentProfile>>() {
    });
    String fromDate = "2020-04-01T00:00:01";
    String toDate = "2020-04-15T00:00:01";
    SearchCriteria criteria = SearchCriteria.builder().key("initialSubmitDate").operation(FilterOperation.BETWEEN).value(fromDate + "," + toDate).valueType(ValueType.DATE_TIME).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
      .perform(get(URL.BASE_URL + URL.PAGINATED)
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE")))
        .param("searchCriteriaList", criteriaJSON)
        .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  public void testReadPenRequestPaginated_GivenFirstAndLast_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<List<StudentProfile>>() {
    });
    String fromDate = "2020-04-01T00:00:01";
    String toDate = "2020-04-15T00:00:01";
    SearchCriteria criteria = SearchCriteria.builder().key("initialSubmitDate").operation(FilterOperation.BETWEEN).value(fromDate + "," + toDate).valueType(ValueType.DATE_TIME).build();
    SearchCriteria criteriaFirstName = SearchCriteria.builder().key("legalFirstName").operation(FilterOperation.CONTAINS).value("a").valueType(ValueType.STRING).build();
    SearchCriteria criteriaLastName = SearchCriteria.builder().key("legalLastName").operation(FilterOperation.CONTAINS).value("o").valueType(ValueType.STRING).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    criteriaList.add(criteriaFirstName);
    criteriaList.add(criteriaLastName);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
      .perform(get(URL.BASE_URL + URL.PAGINATED)
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE")))
        .param("searchCriteriaList", criteriaJSON)
        .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  public void testReadPenRequestPaginated_LegalLastNameFilterIgnoreCase_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<List<StudentProfile>>() {
    });
    SearchCriteria criteria = SearchCriteria.builder().key("legalLastName").operation(FilterOperation.CONTAINS_IGNORE_CASE).value("j").valueType(ValueType.STRING).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
      .perform(get(URL.BASE_URL + URL.PAGINATED)
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE")))
        .param("searchCriteriaList", criteriaJSON)
        .contentType(APPLICATION_JSON))
            .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }
  @Test
  public void testReadPenRequestPaginated_digitalIdFilter_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
        Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    SearchCriteria criteria = SearchCriteria.builder().key("digitalID").operation(FilterOperation.EQUAL).value("fdf94a22-51e3-4816-8665-9f8571af1be4").valueType(ValueType.UUID).build();
    List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    ObjectMapper objectMapper = new ObjectMapper();
    String criteriaJSON = objectMapper.writeValueAsString(criteriaList);
    System.out.println(criteriaJSON);
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));
    MvcResult result = mockMvc
      .perform(get(URL.BASE_URL + URL.PAGINATED)
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE")))
        .param("searchCriteriaList", criteriaJSON)
        .contentType(APPLICATION_JSON))
        .andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)));
  }
  private StudentProfileStatusCodeEntity createPenReqStatus() {
    StudentProfileStatusCodeEntity entity = new StudentProfileStatusCodeEntity();
    entity.setStudentRequestStatusCode("INITREV");
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
