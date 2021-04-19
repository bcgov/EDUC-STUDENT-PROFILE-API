package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.StudentProfileStatusCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Student profile status code table repository.
 */
@Repository
public interface StudentProfileStatusCodeTableRepository extends JpaRepository<StudentProfileStatusCodeEntity, Long> {
}
