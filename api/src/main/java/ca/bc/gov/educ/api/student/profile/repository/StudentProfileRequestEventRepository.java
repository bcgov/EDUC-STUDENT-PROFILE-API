package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.StudentProfileRequestEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileRequestEventRepository extends CrudRepository<StudentProfileRequestEvent, UUID> {

  Optional<StudentProfileRequestEvent> findBySagaIdAndEventType(UUID sagaId, String eventType);

  List<StudentProfileRequestEvent> findByEventStatus(String toString);
}
