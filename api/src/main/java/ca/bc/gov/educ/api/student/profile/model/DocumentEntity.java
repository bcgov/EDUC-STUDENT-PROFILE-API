package ca.bc.gov.educ.api.student.profile.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "student_profile_request_document")
@DynamicUpdate
public class DocumentEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
          name = "UUID",
          strategy = "org.hibernate.id.UUIDGenerator",
          parameters = {
                  @Parameter(
                          name = "uuid_gen_strategy_class",
                          value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                  )
          }
  )
  @Column(name = "student_profile_request_document_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  UUID documentID;

  @ManyToOne
  @JoinColumn(name = "student_profile_request_id", updatable = false, columnDefinition = "BINARY(16)")
  StudentProfileEntity request;

  @Column(name = "student_profile_request_document_type_code")
  String documentTypeCode;

  @Column(name = "file_name")
  String fileName;

  @Column(name = "file_extension")
  String fileExtension;

  @Column(name = "file_size")
  Integer fileSize;

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

  @Basic(fetch = FetchType.LAZY)
  @Lob
  @Column(name = "document_data", updatable = false)
  byte[] documentData;
}
