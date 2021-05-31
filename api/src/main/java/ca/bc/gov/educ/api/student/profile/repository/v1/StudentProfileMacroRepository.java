package ca.bc.gov.educ.api.student.profile.repository.v1;

import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileMacroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentProfileMacroRepository extends JpaRepository<StudentProfileMacroEntity, UUID> {
  List<StudentProfileMacroEntity> findAllByMacroTypeCode(String macroTypeCode);
}
