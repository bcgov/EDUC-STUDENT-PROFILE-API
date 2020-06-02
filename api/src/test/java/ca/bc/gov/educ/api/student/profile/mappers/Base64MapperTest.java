package ca.bc.gov.educ.api.student.profile.mappers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class Base64MapperTest {
  private final Base64Mapper mapper = new Base64Mapper();

  @Test
  public void testMap_GivenNullString_ReturnsBlankByteArray() {
    byte[] bytes = mapper.map((String) null);
    assertEquals(0, bytes.length);
  }

  @Test
  public void testMap_GivenNullByteArray_ReturnsNullString() {
    String result = mapper.map((byte[]) null);
    assertNull(result);
  }
}
