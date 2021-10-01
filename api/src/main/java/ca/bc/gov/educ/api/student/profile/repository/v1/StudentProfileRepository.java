package ca.bc.gov.educ.api.student.profile.repository.v1;

import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.model.v1.UmpStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfileEntity, UUID>, StudentProfileRepositoryCustom, JpaSpecificationExecutor<StudentProfileEntity> {
  @Query(value = "select student_profile_request_status_code as status, status_update_date as statusupdatedate from student_profile_request where  student_profile_request.status_update_date between :fromDate and :toDate and student_profile_request_status_code in :statuses order by status_update_date", nativeQuery = true)
  List<UmpStats> findStatusAndStatusUpdateDatesBetweenForStatuses(LocalDate fromDate, LocalDate toDate, List<String> statuses);

  @Query(value = "select avg(STATUS_UPDATE_DATE-INITIAL_SUBMIT_DATE) as averageCompletionTime from student_profile_request WHERE STUDENT_PROFILE_REQUEST_STATUS_CODE = 'COMPLETED'", nativeQuery = true)
  UmpStats findCompletionProcessAverageTime();

  @Query(value = "SELECT COUNT (SPR_DISTINCT_ID) as col_0_0_ FROM(SELECT  DISTINCT (SPRDOC.STUDENT_PROFILE_REQUEST_ID) AS SPR_DISTINCT_ID FROM STUDENT_PROFILE_REQUEST_DOCUMENT SPRDOC INNER JOIN STUDENT_PROFILE_REQUEST SPR ON SPRDOC.STUDENT_PROFILE_REQUEST_ID = SPR.STUDENT_PROFILE_REQUEST_ID WHERE SPR.student_profile_request_status_code in :statuses and SPR.status_update_date between :startDate and :endDate)", nativeQuery = true)
  long findNumberOfStudentRequestsWithDocumentsStatusCodeInAndStatusUpdateDateBetween(List<String> statuses, LocalDateTime startDate, LocalDateTime endDate);

  long countByStudentRequestStatusCodeInAndStatusUpdateDateBetween(List<String> statuses, LocalDateTime startDate, LocalDateTime endDate);
}
