package ca.bc.gov.educ.api.student.profile.repository.v1;


import ca.bc.gov.educ.api.student.profile.model.v1.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
  List<DocumentEntity> findByRequestStudentRequestID(UUID studentProfileID);

  List<DocumentEntity> findAllByRequestStudentRequestStatusCodeInAndFileSizeGreaterThanAndDocumentDataIsNotNull(List<String> statusCodes, int fileSize);
}
