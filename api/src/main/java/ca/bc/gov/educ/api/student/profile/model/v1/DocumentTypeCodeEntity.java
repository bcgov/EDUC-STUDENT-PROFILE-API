package ca.bc.gov.educ.api.student.profile.model.v1;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "student_profile_request_document_type_code")
public class DocumentTypeCodeEntity {

    @Id
    @Column(name = "student_profile_request_document_type_code", unique = true, updatable = false)
    String documentTypeCode;

  @Column(name = "label")
    String label;

  @Column(name = "description")
    String description;

    @Column(name = "display_order")
    Integer displayOrder;

    @Column(name = "effective_date")
    LocalDateTime effectiveDate;

  @Column(name = "expiry_date")
    LocalDateTime expiryDate;

    @Column(name = "create_user", updatable = false)
    String createUser;

    @PastOrPresent
    @Column(name = "create_date", updatable = false)
    LocalDateTime createDate;

    @Column(name = "update_user")
    String updateUser;

    @PastOrPresent
    @Column(name = "update_date")
    LocalDateTime updateDate;
}
