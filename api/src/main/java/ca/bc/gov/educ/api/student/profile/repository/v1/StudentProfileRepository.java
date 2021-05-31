package ca.bc.gov.educ.api.student.profile.repository.v1;

import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfileEntity, UUID>, StudentProfileRepositoryCustom, JpaSpecificationExecutor<StudentProfileEntity> {
}
