package ca.bc.gov.educ.api.student.profile.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "student_profile_request_status_code")
public class StudentProfileStatusCodeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "student_profile_request_status_code", unique = true, updatable = false)
  String requestStatusCode;

  @NotNull(message = "label cannot be null")
  @Column(name = "label")
  String label;

  @NotNull(message = "description cannot be null")
  @Column(name = "description")
  String description;

  @NotNull(message = "displayOrder cannot be null")
  @Column(name = "display_order")
  Integer displayOrder;

  @NotNull(message = "effectiveDate cannot be null")
  @Column(name = "effective_date")
  LocalDateTime effectiveDate;

  @NotNull(message = "expiryDate cannot be null")
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
