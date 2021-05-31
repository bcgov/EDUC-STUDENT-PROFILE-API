package ca.bc.gov.educ.api.student.profile.repository.v1;

import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileMacroTypeCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentProfileMacroTypeCodeRepository extends JpaRepository<StudentProfileMacroTypeCodeEntity, String> {
}
