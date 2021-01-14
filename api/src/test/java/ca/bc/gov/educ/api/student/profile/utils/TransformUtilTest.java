package ca.bc.gov.educ.api.student.profile.utils;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Data
class TestParentClass {
  @UpperCase
  String filedA;

  String filedB;
}

@Data
class TestChildClass extends TestParentClass {
  @UpperCase
  String filedE;
}

@RunWith(SpringRunner.class)
public class TransformUtilTest {
  @Test
  public void testIsUppercaseField_WhenFieldInParentClass_ShouldReturnTrue()  {
    assertTrue(TransformUtil.isUppercaseField(TestChildClass.class, "filedA"));
  }

  @Test
  public void testIsUppercaseField_WhenFieldInClass_ShouldReturnTrue()  {
    assertTrue(TransformUtil.isUppercaseField(TestChildClass.class, "filedE"));
  }

  @Test
  public void testIsUppercaseField_WhenFieldNotExists_ShouldReturnFalse()  {
    assertFalse(TransformUtil.isUppercaseField(TestChildClass.class, "filedC"));
  }

  @Test
  public void testIsUppercaseField_WhenFieldIsNotUppercased_ShouldReturnFalse()  {
    assertFalse(TransformUtil.isUppercaseField(TestChildClass.class, "filedB"));
  }
}
