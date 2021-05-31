package ca.bc.gov.educ.api.student.profile.validator;

import ca.bc.gov.educ.api.student.profile.BaseProfileRequestAPITest;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileMacroTypeCodeEntity;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileMacroRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileMacroTypeCodeRepository;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileMacroService;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileMacro;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMacroPayloadValidatorTest extends BaseProfileRequestAPITest {

  @Autowired
  StudentProfileMacroTypeCodeRepository macroTypeCodeRepository;

  @Mock
  StudentProfileMacroRepository macroRepository;

  @Autowired
  StudentProfileMacroService macroService;
  @InjectMocks
  StudentProfileMacroPayloadValidator macroPayloadValidator;

  @Before
  public void before() {
    macroTypeCodeRepository.deleteAll();
    macroService = new StudentProfileMacroService(macroRepository, macroTypeCodeRepository);
    macroPayloadValidator = new StudentProfileMacroPayloadValidator(macroService);
  }

  @Test
  public void testValidatePayload_WhenMacroIdGivenForPost_ShouldAddAnErrorTOTheReturnedList() {
    val errorList = macroPayloadValidator.validatePayload(getRequestMacroEntityFromJsonString(), true);
    assertEquals(2, errorList.size());
    assertEquals("macroId should be null for post operation.", errorList.get(0).getDefaultMessage());
  }
  @Test
  public void testValidatePayload_WhenMacroTypeCodeIsInvalid_ShouldAddAnErrorTOTheReturnedList() {
    val entity = getRequestMacroEntityFromJsonString();
    entity.setMacroId(null);
    val errorList = macroPayloadValidator.validatePayload(entity, true);
    assertEquals(1, errorList.size());
    assertEquals("macroTypeCode Invalid.", errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidatePayload_WhenMacroTypeCodeIsNotEffective_ShouldAddAnErrorTOTheReturnedList() {
    val macroTypeCode = createPenReqMacroTypeCode();
    macroTypeCode.setEffectiveDate(LocalDate.MAX);
    macroTypeCodeRepository.save(macroTypeCode);
    val entity = getRequestMacroEntityFromJsonString();
    val errorList = macroPayloadValidator.validatePayload(entity, false);
    assertEquals(1, errorList.size());
    assertEquals("macroTypeCode is not yet effective.", errorList.get(0).getDefaultMessage());
  }
  @Test
  public void testValidatePayload_WhenMacroTypeCodeIsExpired_ShouldAddAnErrorTOTheReturnedList() {
    StudentProfileMacroTypeCodeEntity macroTypeCode = createPenReqMacroTypeCode();
    macroTypeCode.setEffectiveDate(LocalDate.now());
    macroTypeCode.setExpiryDate(LocalDate.now().minusDays(1));
    macroTypeCodeRepository.save(macroTypeCode);
    val entity = getRequestMacroEntityFromJsonString();
    val errorList = macroPayloadValidator.validatePayload(entity, false);
    assertEquals(1, errorList.size());
    assertEquals("macroTypeCode is expired.", errorList.get(0).getDefaultMessage());
  }
  private StudentProfileMacroTypeCodeEntity createPenReqMacroTypeCode() {
    return StudentProfileMacroTypeCodeEntity.builder()
            .createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now())
            .createUser("TEST")
            .updateUser("TEST")
            .description("TEST")
            .displayOrder(1)
            .effectiveDate(LocalDate.MIN)
            .expiryDate(LocalDate.MAX)
            .label("TEST")
            .macroTypeCode("REJECT")
            .build();
  }

  protected String dummyRequestMacroJson() {
    return " {\n" +
            "    \"createUser\": \"om\",\n" +
            "    \"updateUser\": \"om\",\n" +
            "    \"macroId\": \"7f000101-7151-1d84-8171-5187006c0000\",\n" +
            "    \"macroCode\": \"hi\",\n" +
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
