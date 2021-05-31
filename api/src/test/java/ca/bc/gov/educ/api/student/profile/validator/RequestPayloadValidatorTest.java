package ca.bc.gov.educ.api.student.profile.validator;

import ca.bc.gov.educ.api.student.profile.BaseProfileRequestAPITest;
import ca.bc.gov.educ.api.student.profile.model.v1.GenderCodeEntity;
import ca.bc.gov.educ.api.student.profile.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.repository.v1.*;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileService;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RequestPayloadValidatorTest extends BaseProfileRequestAPITest {
  private boolean isCreateOperation = false;
  @Mock
  StudentProfileRepository repository;
  @Mock
  StudentProfileStatusCodeTableRepository statusCodeTableRepo;
  @Mock
  GenderCodeTableRepository genderCodeTableRepo;
  @Mock
  StudentProfileService service;
  @Autowired
  ApplicationProperties properties;
  @InjectMocks
  StudentProfilePayloadValidator requestPayloadValidator;

  @Mock
  private StudentProfileCommentRepository penRequestCommentRepository;
  @Mock
  private DocumentRepository documentRepository;

  @Before
  public void before() {
    service = new StudentProfileService(repository, penRequestCommentRepository, documentRepository, statusCodeTableRepo, genderCodeTableRepo);
    requestPayloadValidator = new StudentProfilePayloadValidator(service,properties);
  }

  @Test
  public void testValidateGenderCode_WhenGenderCodeDoesNotExistInDB_ShouldAddAnErrorTOTheReturnedList() {
    isCreateOperation = true;
    List<FieldError> errorList = new ArrayList<>();
    when(service.getGenderCodesList()).thenReturn(new ArrayList<>());
    StudentProfile request = getRequestEntityFromJsonString();
    requestPayloadValidator.validateGenderCode(request, errorList);
    assertEquals(1, errorList.size());
    assertEquals("Invalid Gender Code.",errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidateGenderCode_WhenGenderCodeExistInDBAndIsNotEffective_ShouldAddAnErrorTOTheReturnedList() {
    isCreateOperation = true;
    List<FieldError> errorList = new ArrayList<>();
    List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
    GenderCodeEntity entity = createGenderCodeData();
    entity.setEffectiveDate(LocalDateTime.MAX);
    genderCodeEntities.add(entity);
    when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
    StudentProfile request = getRequestEntityFromJsonString();
    requestPayloadValidator.validateGenderCode(request, errorList);
    assertEquals(1, errorList.size());
    assertEquals("Gender Code provided is not yet effective.",errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidateGenderCode_WhenGenderCodeExistInDBAndIsExpired_ShouldAddAnErrorTOTheReturnedList() {
    isCreateOperation = true;
    List<FieldError> errorList = new ArrayList<>();
    List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
    GenderCodeEntity entity = createGenderCodeData();
    entity.setExpiryDate(LocalDateTime.MIN);
    genderCodeEntities.add(entity);
    when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
    StudentProfile request = getRequestEntityFromJsonString();
    requestPayloadValidator.validateGenderCode(request, errorList);
    assertEquals(1, errorList.size());
    assertEquals("Gender Code provided has expired.",errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidatePayload_GivenRequestIDInCreate_ShouldAddAnErrorTOTheReturnedList() {
    isCreateOperation = true;
    List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
    genderCodeEntities.add(createGenderCodeData());
    when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
    StudentProfile request = getRequestEntityFromJsonString();
    request.setStudentRequestID(UUID.randomUUID().toString());
    List<FieldError> errorList = requestPayloadValidator.validatePayload(request,true);
    assertEquals(1, errorList.size());
    assertEquals("requestID should be null for post operation.",errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidatePayload_GivenInitialSubmitDateInCreate_ShouldAddAnErrorTOTheReturnedList() {
    isCreateOperation = true;
    List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
    genderCodeEntities.add(createGenderCodeData());
    when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
    StudentProfile request = getRequestEntityFromJsonString();
    request.setInitialSubmitDate(LocalDateTime.now().toString());
    List<FieldError> errorList = requestPayloadValidator.validatePayload(request,true);
    assertEquals(1, errorList.size());
    assertEquals("initialSubmitDate should be null for post operation.",errorList.get(0).getDefaultMessage());
  }
//  @Test
//  public void testValidatePayload_WhenBCSCAutoMatchIsInvalid_ShouldAddAnErrorTOTheReturnedList() {
//    isCreateOperation = true;
//    List<GenderCodeEntity> genderCodeEntities = new ArrayList<>();
//    genderCodeEntities.add(createGenderCodeData());
//    when(service.getGenderCodesList()).thenReturn(genderCodeEntities);
//    StudentProfile request = getRequestEntityFromJsonString();
//    request.setBcscAutoMatchOutcome("junk");
//    request.setBcscAutoMatchOutcome("junk");
//    List<FieldError> errorList = requestPayloadValidator.validatePayload(request,true);
//    assertEquals(1, errorList.size());
//    assertEquals("Invalid bcscAutoMatchOutcome. It should be one of :: [RIGHTPEN, WRONGPEN, NOMATCH, MANYMATCHES, ONEMATCH]",errorList.get(0).getDefaultMessage());
//  }
  private GenderCodeEntity createGenderCodeData() {
    return GenderCodeEntity.builder().genderCode("M").description("Male")
            .effectiveDate(LocalDateTime.now()).expiryDate(LocalDateTime.MAX).displayOrder(1).label("label").createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now()).createUser("TEST").updateUser("TEST").build();
  }

  protected String dummyRequestJson() {
    return "{\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\"}";
  }

  protected StudentProfile getRequestEntityFromJsonString() {
    try {
      return new ObjectMapper().readValue(dummyRequestJson(), StudentProfile.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
