package ca.bc.gov.educ.api.student.profile.model.v1;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "student_profile_request_macro")
public class StudentProfileMacroEntity {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @org.hibernate.annotations.Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "student_profile_request_macro_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  UUID macroId;

  @Column(name = "macro_code")
  private String macroCode;

  @Column(name = "macro_text")
  private String macroText;

  @Column(name = "macro_type_code")
  private String macroTypeCode;

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
