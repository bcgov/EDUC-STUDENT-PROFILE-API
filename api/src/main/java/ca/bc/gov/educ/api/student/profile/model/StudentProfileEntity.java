package ca.bc.gov.educ.api.student.profile.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "STUDENT_PROFILE_REQUEST")
@DynamicUpdate
public class StudentProfileEntity {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "STUDENT_PROFILE_REQUEST_ID", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  UUID requestID;

  @NotNull(message = "digitalID cannot be null")
  @Column(name = "DIGITAL_IDENTITY_ID", updatable = false, columnDefinition = "BINARY(16)")
  UUID digitalID;

  @Column(name = "STUDENT_PROFILE_REQUEST_STATUS_CODE")
  String requestStatusCode;

  @Column(name = "LEGAL_FIRST_NAME")
  String legalFirstName;

  @Column(name = "LEGAL_MIDDLE_NAMES")
  String legalMiddleNames;

  @NotNull(message = "legalLastName cannot be null")
  @Column(name = "LEGAL_LAST_NAME")
  String legalLastName;

  @Column(name = "DOB")
  LocalDate dob;

  @Column(name = "GENDER_CODE")
  String genderCode;

  @Column(name = "EMAIL")
  String email;

  @NotNull(message = "recordedPen cannot be null")
  @Column(name = "RECORDED_PEN")
  String recordedPen;

  @Column(name = "RECORDED_LEGAL_FIRST_NAME")
  String recordedLegalFirstName;

  @Column(name = "RECORDED_LEGAL_MIDDLE_NAMES")
  String recordedLegalMiddleNames;

  @NotNull(message = "recordedLegalLastName cannot be null")
  @Column(name = "RECORDED_LEGAL_LAST_NAME")
  String recordedLegalLastName;

  @Column(name = "RECORDED_DOB")
  LocalDate recordedDob;

  @Column(name = "RECORDED_GENDER_CODE")
  String recordedGenderCode;

  @Column(name = "RECORDED_EMAIL")
  String recordedEmail;

  @Column(name = "email_verified")
  String emailVerified;
  
  @Column(name = "reviewer")
  String reviewer;

  @Column(name = "failure_reason")
  String failureReason;

  @PastOrPresent
  @Column(name = "initial_submit_date")
  LocalDateTime initialSubmitDate;

  @PastOrPresent
  @Column(name = "status_update_date")
  LocalDateTime statusUpdateDate;

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
  
  @Column(name = "complete_comment")
  String completeComment;

  @OneToMany(mappedBy = "studentProfileEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = StudentProfileCommentsEntity.class)
  private Set<StudentProfileCommentsEntity> studentProfileComments;
}
