package ca.bc.gov.educ.api.student.profile.repository;


import ca.bc.gov.educ.api.student.profile.model.StudentProfileCommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileCommentRepository extends JpaRepository<StudentProfileCommentsEntity, UUID> {
  Optional<StudentProfileCommentsEntity> findByCommentContentAndCommentTimestamp(String commentContent, LocalDateTime commentTimestamp);
}
