package ca.bc.gov.educ.api.student.profile.utils;

import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonUtilTest {

  @Test
  public void getJsonStringFromObject() throws JsonProcessingException {
    assertThat(JsonUtil.getJsonStringFromObject(new StudentProfile())).isNotNull();
  }

  @Test
  public void getJsonObjectFromString() throws JsonProcessingException {
    assertThat(JsonUtil.getJsonObjectFromString(StudentProfile.class, JsonUtil.getJsonStringFromObject(new StudentProfile()))).isNotNull();
  }
}
