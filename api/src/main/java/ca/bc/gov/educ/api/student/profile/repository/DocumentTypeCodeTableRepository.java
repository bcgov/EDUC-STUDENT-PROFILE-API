package ca.bc.gov.educ.api.student.profile.repository;


import ca.bc.gov.educ.api.student.profile.model.DocumentTypeCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Document Type Code Table Repository
 */
@Repository
public interface DocumentTypeCodeTableRepository extends JpaRepository<DocumentTypeCodeEntity, String> {
}
