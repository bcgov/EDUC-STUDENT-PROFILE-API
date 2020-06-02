package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentProfileRepository extends CrudRepository<StudentProfileEntity, UUID>, StudentProfileRepositoryCustom, JpaSpecificationExecutor<StudentProfileEntity> {
    List<StudentProfileEntity> findAll();
}
