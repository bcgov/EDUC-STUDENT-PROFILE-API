package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.StudentProfileStatusCodeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Pen Request Status Code Table Repository
 *
 * @author Marco Villeneuve
 */
@Repository
public interface StudentProfileStatusCodeTableRepository extends CrudRepository<StudentProfileStatusCodeEntity, Long> {
}
