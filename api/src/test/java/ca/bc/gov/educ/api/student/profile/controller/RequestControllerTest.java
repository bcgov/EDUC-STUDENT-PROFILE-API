package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.constants.StudentProfileStatusCodes;
import ca.bc.gov.educ.api.student.profile.constants.v1.URL;
import ca.bc.gov.educ.api.student.profile.controller.v1.StudentProfileController;
import ca.bc.gov.educ.api.student.profile.filter.FilterOperation;
import ca.bc.gov.educ.api.student.profile.mappers.v1.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.model.v1.*;
import ca.bc.gov.educ.api.student.profile.repository.v1.*;
import ca.bc.gov.educ.api.student.profile.struct.SearchCriteria;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import ca.bc.gov.educ.api.student.profile.struct.ValueType;
import ca.bc.gov.educ.api.student.profile.support.DocumentBuilder;
import ca.bc.gov.educ.api.student.profile.support.DocumentTypeCodeBuilder;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.closeTo;
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
  StudentProfileCommentRepository studentProfileCommentRepository;

  @Autowired
  DocumentRepository documentRepository;

  @Autowired
  StudentProfileStatusCodeTableRepository statusCodeTableRepo;

  @Autowired
  private DocumentTypeCodeTableRepository documentTypeCodeRepository;

  private UUID documentID;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    profileRequestAPITestUtils.saveGenderCode(createGenderCodeData());
    DocumentTypeCodeBuilder.setUpDocumentTypeCodes(documentTypeCodeRepository);
    DocumentEntity document = new DocumentBuilder()
      .withoutDocumentID()
      .withRequest(null)
      .withTypeCode("CAPASSPORT")
      .build();
    document = this.documentRepository.save(document);
    this.documentID = document.getDocumentID();
  }

  @After
  public void after() {
    documentRepository.deleteAll();
    repository.deleteAll();
    genderRepo.deleteAll();
    documentRepository.deleteAll();
    documentTypeCodeRepository.deleteAll();
  }

  private GenderCodeEntity createGenderCodeData() {
    return GenderCodeEntity.builder().genderCode("M").description("Male")
      .effectiveDate(LocalDateTime.now().minusYears(10)).expiryDate(LocalDateTime.MAX).displayOrder(1).label("label").createDate(LocalDateTime.now())
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
  public void testCreateRequest_GivenValidPayload2_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithRecordedEmail())).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  public void testCreateRequest_GivenValidPayload3_ShouldReturnStatusCreated() throws Exception {
    this.mockMvc.perform(post(URL.BASE_URL)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRITE_STUDENT_PROFILE")))
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(dummyStudentProfileJsonWithDocumentIDs(List.of(this.documentID.toString())))).andDo(print()).andExpect(status().isCreated());
    var document =  documentRepository.findById(this.documentID);
    assertThat(document.get().getRequest()).isNotNull();
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
    StudentProfileEntity entity = repository.save(requestEntity);
    entity.setStudentProfileComments(createComments(entity));
    repository.save(entity);
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
    commentsEntity.setStudentRequestID(studentProfileEntity.getStudentRequestID());
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

  @Test
  public void testGetStats_COMPLETIONS_LAST_WEEK_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    var updateDate = LocalDateTime.now().minusDays(6);
    var dayName1 = updateDate.getDayOfWeek().name();
    entities.get(0).setStatusUpdateDate(updateDate.toString());
    entities.get(1).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    updateDate = LocalDateTime.now();
    var dayName2 = updateDate.getDayOfWeek().name();
    entities.get(1).setStatusUpdateDate(updateDate.toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
        .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
        .param("statsType", "COMPLETIONS_LAST_WEEK")
        .contentType(APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.completionsInLastWeek." + dayName1, is(1)))
        .andExpect(jsonPath("$.completionsInLastWeek." + dayName2, is(1)));
  }

  @Test
  public void testGetStats_COMPLETIONS_LAST_12_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    var updateDate = LocalDateTime.now();
    var month1 = updateDate.getMonth().toString();
    entities.get(0).setStatusUpdateDate(updateDate.toString());
    entities.get(1).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    updateDate = LocalDateTime.now().withDayOfMonth(1).minusMonths(11);
    var month2 = updateDate.getMonth().toString();
    entities.get(1).setStatusUpdateDate(updateDate.toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
      .param("statsType", "COMPLETIONS_LAST_13_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.completionsInLastTwelveMonth." + month1, is(1)))
      .andExpect(jsonPath("$.completionsInLastTwelveMonth." + month2, is(1)));
  }

  @Test
  public void testGetStats_PERCENT_UMP_REJECTED_TO_LAST_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.REJECTED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(30).toString());
    entities.get(1).setStudentRequestStatusCode(StudentProfileStatusCodes.REJECTED.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setStudentRequestStatusCode(StudentProfileStatusCodes.REJECTED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
      .param("statsType", "PERCENT_UMP_REJECTED_TO_LAST_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.percentRejectedUmpToLastMonth", closeTo(100, 0.001)))
      .andExpect(jsonPath("$.umpRejectedInCurrentMonth", is(2)));
  }

  @Test
  public void testGetStats_PERCENT_UMP_ABANDONED_TO_LAST_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.ABANDONED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(30).toString());
    entities.get(1).setStudentRequestStatusCode(StudentProfileStatusCodes.ABANDONED.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setStudentRequestStatusCode(StudentProfileStatusCodes.ABANDONED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
      .param("statsType", "PERCENT_UMP_ABANDONED_TO_LAST_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.percentAbandonedUmpToLastMonth", closeTo(100, 0.001)))
      .andExpect(jsonPath("$.umpAbandonedInCurrentMonth", is(2)));
  }

  @Test
  public void testGetStats_PERCENT_UMP_COMPLETION_TO_LAST_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(30).toString());
    entities.get(1).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
      .param("statsType", "PERCENT_UMP_COMPLETION_TO_LAST_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.percentCompletedUmpToLastMonth", closeTo(100, 0.001)))
      .andExpect(jsonPath("$.umpCompletedInCurrentMonth", is(2)));
  }

  @Test
  public void testGetStats_PERCENT_UMP_COMPLETED_WITH_DOCUMENTS_TO_LAST_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().toString());
    var studentProfiles = repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    DocumentEntity document = new DocumentBuilder()
      .withoutDocumentID()
      //.withoutCreateAndUpdateUser()
      .withRequest(studentProfiles.get(0))
      .withTypeCode("CAPASSPORT")
      .build();
    this.documentRepository.save(document);

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
      .param("statsType", "PERCENT_UMP_COMPLETED_WITH_DOCUMENTS_TO_LAST_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.percentUmpCompletedWithDocumentsToLastMonth", closeTo(1, 0.001)))
      .andExpect(jsonPath("$.umpCompletedWithDocsInCurrentMonth", is(1)));
  }

  @Test
  public void testGetStats_ALL_STATUSES_LAST_12_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusMonths(11).toString());
    entities.get(1).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setStudentRequestStatusCode(StudentProfileStatusCodes.RETURNED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().minusMonths(3).toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
      .param("statsType", "ALL_STATUSES_LAST_12_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.allStatsLastTwelveMonth.COMPLETED", is(2)))
      .andExpect(jsonPath("$.allStatsLastTwelveMonth.RETURNED", is(1)));
  }

  @Test
  public void testGetStats_ALL_STATUSES_LAST_6_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusMonths(5).toString());
    entities.get(1).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setStudentRequestStatusCode(StudentProfileStatusCodes.RETURNED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().minusMonths(3).toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
      .param("statsType", "ALL_STATUSES_LAST_6_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.allStatsLastSixMonth.COMPLETED", is(2)))
      .andExpect(jsonPath("$.allStatsLastSixMonth.RETURNED", is(1)));
  }

  @Test
  public void testGetStats_ALL_STATUSES_LAST_1_MONTH_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(1).minusMonths(1).toString());
    entities.get(1).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setStudentRequestStatusCode(StudentProfileStatusCodes.RETURNED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().minusMonths(1).toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
      .param("statsType", "ALL_STATUSES_LAST_1_MONTH")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.allStatsLastOneMonth.COMPLETED", is(2)))
      .andExpect(jsonPath("$.allStatsLastOneMonth.RETURNED", is(1)));
  }

  @Test
  public void testGetStats_ALL_STATUSES_LAST_1_WEEK_ShouldReturnStatusOk() throws Exception {
    final File file = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource("mock_student_profiles.json")).getFile()
    );
    List<StudentProfile> entities = new ObjectMapper().readValue(file, new TypeReference<>() {
    });
    entities.get(0).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(0).setStatusUpdateDate(LocalDateTime.now().minusDays(6).toString());
    entities.get(1).setStudentRequestStatusCode(StudentProfileStatusCodes.COMPLETED.toString());
    entities.get(1).setStatusUpdateDate(LocalDateTime.now().toString());
    entities.get(2).setStudentRequestStatusCode(StudentProfileStatusCodes.RETURNED.toString());
    entities.get(2).setStatusUpdateDate(LocalDateTime.now().minusDays(2).toString());
    repository.saveAll(entities.stream().map(mapper::toModel).collect(Collectors.toList()));

    this.mockMvc.perform(get(URL.BASE_URL + URL.STATS)
      .with(jwt().jwt((jwt) -> jwt.claim("scope", "READ_STUDENT_PROFILE_STATS")))
      .param("statsType", "ALL_STATUSES_LAST_1_WEEK")
      .contentType(APPLICATION_JSON))
      .andDo(print()).andExpect(status().isOk())
      .andExpect(jsonPath("$.allStatsLastOneWeek.COMPLETED", is(2)))
      .andExpect(jsonPath("$.allStatsLastOneWeek.RETURNED", is(1)));
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
