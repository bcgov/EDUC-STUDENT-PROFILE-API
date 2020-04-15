package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.StudentProfileMacroTypeCodeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentProfileMacroTypeCodeRepository extends CrudRepository<StudentProfileMacroTypeCodeEntity, String> {
  List<StudentProfileMacroTypeCodeEntity> findAll();
}
