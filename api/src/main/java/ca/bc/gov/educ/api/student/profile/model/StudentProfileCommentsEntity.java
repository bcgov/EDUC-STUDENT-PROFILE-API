package ca.bc.gov.educ.api.student.profile.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_profile_request_comment")
@Getter
@Setter
public class StudentProfileCommentsEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "student_profile_request_comment_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  UUID studentProfileCommentID;
  @Column(name = "student_profile_request_id")
  UUID requestID;
  @Column(name = "staff_member_idir_guid")
  String staffMemberIDIRGUID;

  @Column(name = "staff_member_name")
  String staffMemberName;

  @Column(name = "comment_content")
  String commentContent;

  @Column(name = "comment_timestamp", columnDefinition = "TIMESTAMP")
  LocalDateTime commentTimestamp;

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

  @ManyToOne(cascade = CascadeType.ALL, optional = false, targetEntity = StudentProfileEntity.class)
  @JoinColumn(name = "student_profile_request_id", updatable = false, insertable = false)
  private StudentProfileEntity studentProfileEntity;

}
