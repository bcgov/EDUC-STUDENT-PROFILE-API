package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.GenderCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Gender Code Table Repository
 *
 * @author Marco Villeneuve
 *
 */
@Repository
public interface GenderCodeTableRepository extends JpaRepository<GenderCodeEntity, Long> {
}
