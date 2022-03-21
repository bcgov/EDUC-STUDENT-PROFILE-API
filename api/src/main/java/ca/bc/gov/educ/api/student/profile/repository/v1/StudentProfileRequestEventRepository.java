package ca.bc.gov.educ.api.student.profile.repository.v1;

import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileRequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileRequestEventRepository extends JpaRepository<StudentProfileRequestEvent, UUID> {
  Optional<StudentProfileRequestEvent> findBySagaIdAndEventType(UUID sagaId, String eventType);

  @Transactional
  @Modifying
  @Query("delete from StudentProfileRequestEvent where createDate <= :createDate")
  void deleteByCreateDateBefore(LocalDateTime createDate);
}
