package ca.bc.gov.educ.api.student.profile.model.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_profile_request_gender_code")
public class GenderCodeEntity {

	@Id
	@Column(name = "gender_code", unique = true, updatable = false)
	String genderCode;

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

	@Column(name = "update_user", updatable = false)
	String updateUser;

	@PastOrPresent
	@Column(name = "update_date", updatable = false)
	LocalDateTime updateDate;

}
