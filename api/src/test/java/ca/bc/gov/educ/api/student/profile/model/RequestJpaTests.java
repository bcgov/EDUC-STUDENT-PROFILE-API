package ca.bc.gov.educ.api.student.profile.model;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.student.profile.repository.StudentProfileRepository;
import ca.bc.gov.educ.api.student.profile.support.RequestBuilder;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RequestJpaTests {
    @Autowired
    private StudentProfileRepository repository;

    private StudentProfileEntity request;

    @Before
    public void setUp() {
        this.request = new RequestBuilder()
                            .withoutRequestID().build();
    }

    @Test
    public void saveDocumentTest() {
        StudentProfileEntity savedRequest = this.repository.save(this.request);
        assertThat(savedRequest.getStudentRequestID()).isNotNull();
        assertThat(savedRequest.getInitialSubmitDate()).isNull();

        assertThat(this.repository.findById(savedRequest.getStudentRequestID()).isPresent()).isTrue();
    }
   
}
