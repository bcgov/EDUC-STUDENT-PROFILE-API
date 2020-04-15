package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.StudentProfileEventEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileEventRepository extends CrudRepository<StudentProfileEventEntity, UUID> {
  Optional<StudentProfileEventEntity> findBySagaIdAndEventType(UUID sagaId, String eventType);

  List<StudentProfileEventEntity> findByEventStatus(String toString);
}
