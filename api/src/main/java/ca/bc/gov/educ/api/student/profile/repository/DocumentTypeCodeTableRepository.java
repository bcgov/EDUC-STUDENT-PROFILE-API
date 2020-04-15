package ca.bc.gov.educ.api.student.profile.repository;


import ca.bc.gov.educ.api.student.profile.model.DocumentTypeCodeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Document Type Code Table Repository
 */
@Repository
public interface DocumentTypeCodeTableRepository extends CrudRepository<DocumentTypeCodeEntity, String> {
  List<DocumentTypeCodeEntity> findAll();
}
