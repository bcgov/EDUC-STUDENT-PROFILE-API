package ca.bc.gov.educ.api.student.profile.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
  public static final ObjectMapper mapper = new ObjectMapper();
  private JsonUtil(){
  }
  public static String getJsonStringFromObject(Object payload) throws JsonProcessingException {
    return mapper.writeValueAsString(payload);
  }

  public static <T> T getJsonObjectFromString(Class<T> clazz,  String payload) throws JsonProcessingException {
    return mapper.readValue(payload, clazz);
  }
}
