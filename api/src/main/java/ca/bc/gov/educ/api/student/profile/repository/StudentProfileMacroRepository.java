package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.StudentProfileMacroEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentProfileMacroRepository extends CrudRepository<StudentProfileMacroEntity, UUID> {
  List<StudentProfileMacroEntity> findAll();

  List<StudentProfileMacroEntity> findAllByMacroTypeCode(String macroTypeCode);
}
