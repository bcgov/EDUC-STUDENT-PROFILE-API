package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.GenderCodeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Gender Code Table Repository
 *
 * @author Marco Villeneuve
 * 
 */
@Repository
public interface GenderCodeTableRepository extends CrudRepository<GenderCodeEntity, Long> {
    List<GenderCodeEntity> findAll();
}
