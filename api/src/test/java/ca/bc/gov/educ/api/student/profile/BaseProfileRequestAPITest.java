package ca.bc.gov.educ.api.student.profile;

import ca.bc.gov.educ.api.student.profile.utils.ProfileRequestAPITestUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {StudentProfileApiResourceApplication.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseProfileRequestAPITest {

  @Autowired
  protected ProfileRequestAPITestUtils profileRequestAPITestUtils;

  @Before
  public void before() {
    this.profileRequestAPITestUtils.cleanDB();
  }
}
