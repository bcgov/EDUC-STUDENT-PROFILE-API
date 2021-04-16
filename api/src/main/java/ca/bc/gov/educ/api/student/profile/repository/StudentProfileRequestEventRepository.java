package ca.bc.gov.educ.api.student.profile.repository;

import ca.bc.gov.educ.api.student.profile.model.StudentProfileRequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileRequestEventRepository extends JpaRepository<StudentProfileRequestEvent, UUID> {
  Optional<StudentProfileRequestEvent> findBySagaIdAndEventType(UUID sagaId, String eventType);
}
