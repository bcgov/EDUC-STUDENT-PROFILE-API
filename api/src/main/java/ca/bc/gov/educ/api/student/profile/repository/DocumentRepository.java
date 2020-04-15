package ca.bc.gov.educ.api.student.profile.repository;


import ca.bc.gov.educ.api.student.profile.model.DocumentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends CrudRepository<DocumentEntity, UUID> {
  List<DocumentEntity> findByStudentProfileEntity(UUID studentProfileID);
}