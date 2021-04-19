package ca.bc.gov.educ.api.student.profile.health;

import ca.bc.gov.educ.api.student.profile.BaseProfileRequestAPITest;
import io.nats.client.Connection;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class StudentProfileAPICustomHealthCheckTest extends BaseProfileRequestAPITest {

  @Autowired
  Connection natsConnection;

  @Autowired
  private StudentProfileAPICustomHealthCheck studentProfileAPICustomHealthCheck;

  @Test
  public void testGetHealth_givenClosedNatsConnection_shouldReturnStatusDown() {
    when(natsConnection.getStatus()).thenReturn(Connection.Status.CLOSED);
    assertThat(studentProfileAPICustomHealthCheck.getHealth(true)).isNotNull();
    assertThat(studentProfileAPICustomHealthCheck.getHealth(true).getStatus()).isEqualTo(Status.DOWN);
  }

  @Test
  public void testGetHealth_givenOpenNatsConnection_shouldReturnStatusUp() {
    when(natsConnection.getStatus()).thenReturn(Connection.Status.CONNECTED);
    assertThat(studentProfileAPICustomHealthCheck.getHealth(true)).isNotNull();
    assertThat(studentProfileAPICustomHealthCheck.getHealth(true).getStatus()).isEqualTo(Status.UP);
  }


  @Test
  public void testHealth_givenClosedNatsConnection_shouldReturnStatusDown() {
    when(natsConnection.getStatus()).thenReturn(Connection.Status.CLOSED);
    assertThat(studentProfileAPICustomHealthCheck.health()).isNotNull();
    assertThat(studentProfileAPICustomHealthCheck.health().getStatus()).isEqualTo(Status.DOWN);
  }

  @Test
  public void testHealth_givenOpenNatsConnection_shouldReturnStatusUp() {
    when(natsConnection.getStatus()).thenReturn(Connection.Status.CONNECTED);
    assertThat(studentProfileAPICustomHealthCheck.health()).isNotNull();
    assertThat(studentProfileAPICustomHealthCheck.health().getStatus()).isEqualTo(Status.UP);
  }
}
