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
@Table(name = "student_profile")
@DynamicUpdate
public class StudentProfileEntity {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "student_profile_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  UUID studentProfileID;

  @NotNull(message = "digitalID cannot be null")
  @Column(name = "digital_identity_id", updatable = false, columnDefinition = "BINARY(16)")
  UUID digitalID;

  @Column(name = "student_profile_status_code")
  String studentProfileStatusCode;

  @Column(name = "legal_first_name")
  String legalFirstName;

  @Column(name = "legal_middle_names")
  String legalMiddleNames;

  @NotNull(message = "legalLastName cannot be null")
  @Column(name = "legal_last_name")
  String legalLastName;

  @Column(name = "dob")
  LocalDate dob;

  @Column(name = "gender_code")
  String genderCode;

  @Column(name = "usual_first_name")
  String usualFirstName;

  @Column(name = "usual_middle_names")
  String usualMiddleName;

  @Column(name = "usual_last_name")
  String usualLastName;

  @Column(name = "email")
  String email;

  @Column(name = "email_verified")
  String emailVerified;
  
  @Column(name = "maiden_name")
  String maidenName;

  @Column(name = "past_names")
  String pastNames;

  @Column(name = "last_bc_school")
  String lastBCSchool;

  @Column(name = "last_bc_school_student_number")
  String lastBCSchoolStudentNumber;

  @Column(name = "current_school")
  String currentSchool;

  @Column(name = "reviewer")
  String reviewer;

  @Column(name = "failure_reason")
  String failureReason;

  @PastOrPresent
  @Column(name = "INITIAL_SUBMIT_DATE")
  LocalDateTime initialSubmitDate;

  @PastOrPresent
  @Column(name = "STATUS_UPDATE_DATE")
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

  @Column(name = "BCSC_AUTO_MATCH_OUTCOME")
  String bcscAutoMatchOutcome;

  @Column(name = "BCSC_AUTO_MATCH_DETAIL")
  String bcscAutoMatchDetails;
  
  @Column(name = "PEN")
  String pen;
  @OneToMany(mappedBy = "studentProfileEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = StudentProfileCommentsEntity.class)
  private Set<StudentProfileCommentsEntity> studentProfileComments;
}
