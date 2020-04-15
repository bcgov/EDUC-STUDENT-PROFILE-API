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
@Table(name = "STUDENT_PROFILE_COMMENT")
@Getter
@Setter
public class StudentProfileCommentsEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
          @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "STDUENT_PROFILE_COMMENT_ID", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  UUID studentProfileCommentID;
  @Column(name = "STUDEN_PROFILE_ID")
  UUID studentProfileID;
  @Column(name = "STAFF_MEMBER_IDIR_GUID")
  String staffMemberIDIRGUID;

  @Column(name = "STAFF_MEMBER_NAME")
  String staffMemberName;

  @Column(name = "COMMENT_CONTENT")
  String commentContent;

  @Column(name = "COMMENT_TIMESTAMP", columnDefinition = "TIMESTAMP")
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
  @JoinColumn(name = "STUDENT_PROFILE_ID", updatable = false, insertable = false)
  private StudentProfileEntity studentProfileEntity;

}
