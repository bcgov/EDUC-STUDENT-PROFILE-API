package ca.bc.gov.educ.api.student.profile.mappers;

import java.util.Base64;

public class Base64Mapper {

  public byte[] map(String value) {
    if (value == null) {
      return "".getBytes();
    }
    return Base64.getDecoder().decode(value);
  }

  public String map(byte[] value) {
    if (value == null) {
      return null;
    }
    return new String(Base64.getEncoder().encode(value));
  }
}
