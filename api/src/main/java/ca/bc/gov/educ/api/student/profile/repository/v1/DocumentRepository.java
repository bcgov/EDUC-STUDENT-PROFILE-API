package ca.bc.gov.educ.api.student.profile.repository.v1;


import ca.bc.gov.educ.api.student.profile.model.v1.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
  List<DocumentEntity> findByRequestStudentRequestID(UUID studentProfileID);

  List<DocumentEntity> findAllByRequestStudentRequestStatusCodeInAndFileSizeGreaterThanAndDocumentDataIsNotNull(List<String> statusCodes, int fileSize);

  @Modifying
  @Query(value = "delete from student_profile_request_document where student_profile_request_id is null and create_date <= :createDate", nativeQuery = true)
  void deleteOrphanRecordsByCreateDateLessThanEqual(LocalDateTime createDate);

}
