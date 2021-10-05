package ca.bc.gov.educ.api.student.profile.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * place-holder struct to return all the stats with a single class with fields getting populated as per the query.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentProfileStats {
  /**
   * contains number of completed UMP requests in last 12 months from the current month.
   * ex:- if current month is JANUARY 2021, it will start from JANUARY 2020.
   * JAN 20
   * FEB 30 ......
   */
  Map<String, Long> completionsInLastTwelveMonth;
  /**
   * contains number of completed UMP requests in last week from current day.
   * ex:- if current day is Wednesday -> it will show from Last Wednesday to Tuesday.
   * WED 20
   * THURS 30 ......
   */
  Map<String, Long> completionsInLastWeek;

  /**
   * Numbers for different statuses in last 12 month.
   * COMPLETED, 500
   * REJECTED, 20
   * DRAFT, 20
   * RETURNED, 50
   * ABANDONED, 20
   */
  Map<String, Long> allStatsLastTwelveMonth;

  /**
   * Numbers for different statuses in last 6 month.
   * COMPLETED, 500
   * REJECTED, 20
   * DRAFT, 20
   * RETURNED, 50
   * ABANDONED, 20
   */
  Map<String, Long> allStatsLastSixMonth;

  /**
   * Numbers for different statuses in last 1 month.
   * COMPLETED, 500
   * REJECTED, 20
   * DRAFT, 20
   * RETURNED, 50
   * ABANDONED, 20
   */
  Map<String, Long> allStatsLastOneMonth;

  /**
   * Numbers for different statuses in last 1 week.
   * COMPLETED, 500
   * REJECTED, 20
   * DRAFT, 20
   * RETURNED, 50
   * ABANDONED, 20
   */
  Map<String, Long> allStatsLastOneWeek;

  /**
   * The Average time to complete request.
   */
  Double averageTimeToCompleteRequest;

  /**
   * The Percent completed UMP to last month.
   */
  Double percentCompletedUmpToLastMonth;
  /**
   * The Percent rejected UMP to last month.
   */
  Double percentRejectedUmpToLastMonth;
  /**
   * The Percent abandoned UMP to last month.
   */
  Double percentAbandonedUmpToLastMonth;
  /**
   * The Percent UMP completed with documents to last month.
   */
  Double percentUmpCompletedWithDocumentsToLastMonth;
  /**
   * The UMP completed in current month.
   */
  Long umpCompletedInCurrentMonth;
  /**
   * The UMP abandoned in current month.
   */
  Long umpAbandonedInCurrentMonth;
  /**
   * The UMP rejected in current month.
   */
  Long umpRejectedInCurrentMonth;

  /**
   * The UMP completed with docs in current month.
   */
  Long umpCompletedWithDocsInCurrentMonth;
}
