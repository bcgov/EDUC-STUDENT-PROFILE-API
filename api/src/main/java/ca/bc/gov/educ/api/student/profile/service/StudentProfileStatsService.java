package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.constants.StudentProfileStatusCodes;
import ca.bc.gov.educ.api.student.profile.constants.StatsType;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileStats;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class StudentProfileStatsService {
  private final StudentProfileRepository studentProfileRepository;

  public StudentProfileStatsService(StudentProfileRepository studentProfileRepository) {
    this.studentProfileRepository = studentProfileRepository;
  }

  public StudentProfileStats getStats(final StatsType statsType) {
    Pair<Long, Double> currentMonthResultAndPercentile;
    var currentDateTime = LocalDateTime.now();
    val baseDateTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    switch (statsType) {
      case COMPLETIONS_LAST_WEEK:
        return this.getRequestsCompletedLastWeek();
      case AVERAGE_COMPLETION_TIME:
        return this.getAverageUMPCompletionTime();
      case COMPLETIONS_LAST_13_MONTH:
        return this.getRequestsCompletedLastMonths();
      case PERCENT_UMP_REJECTED_TO_LAST_MONTH:
        currentMonthResultAndPercentile = this.getMonthlyPercentUMPBasedOnStatus(StudentProfileStatusCodes.REJECTED.toString());
        return StudentProfileStats.builder().umpRejectedInCurrentMonth(currentMonthResultAndPercentile.getLeft()).percentRejectedUmpToLastMonth(currentMonthResultAndPercentile.getRight()).build();
      case PERCENT_UMP_ABANDONED_TO_LAST_MONTH:
        currentMonthResultAndPercentile = this.getMonthlyPercentUMPBasedOnStatus(StudentProfileStatusCodes.ABANDONED.toString());
        return StudentProfileStats.builder().umpAbandonedInCurrentMonth(currentMonthResultAndPercentile.getLeft()).percentAbandonedUmpToLastMonth(currentMonthResultAndPercentile.getRight()).build();
      case PERCENT_UMP_COMPLETED_WITH_DOCUMENTS_TO_LAST_MONTH:
        currentMonthResultAndPercentile = this.getMonthlyPercentUMPWithDocsBasedOnStatus(StudentProfileStatusCodes.COMPLETED.toString());
        return StudentProfileStats.builder().umpCompletedWithDocsInCurrentMonth(currentMonthResultAndPercentile.getLeft()).percentUmpCompletedWithDocumentsToLastMonth(currentMonthResultAndPercentile.getRight()).build();
      case PERCENT_UMP_COMPLETION_TO_LAST_MONTH:
        currentMonthResultAndPercentile = this.getMonthlyPercentUMPBasedOnStatus(StudentProfileStatusCodes.COMPLETED.toString());
        return StudentProfileStats.builder().umpCompletedInCurrentMonth(currentMonthResultAndPercentile.getLeft()).percentCompletedUmpToLastMonth(currentMonthResultAndPercentile.getRight()).build();
      case ALL_STATUSES_LAST_12_MONTH:
        return StudentProfileStats.builder().allStatsLastTwelveMonth(this.getAllStatusesBetweenDates(baseDateTime.withDayOfMonth(1).minusMonths(11), currentDateTime)).build();
      case ALL_STATUSES_LAST_6_MONTH:
        return StudentProfileStats.builder().allStatsLastSixMonth(this.getAllStatusesBetweenDates(baseDateTime.withDayOfMonth(1).minusMonths(5), currentDateTime)).build();
      case ALL_STATUSES_LAST_1_MONTH:
        return StudentProfileStats.builder().allStatsLastOneMonth(this.getAllStatusesBetweenDates(baseDateTime.minusDays(1).minusMonths(1), currentDateTime)).build();
      case ALL_STATUSES_LAST_1_WEEK:
        return StudentProfileStats.builder().allStatsLastOneWeek(this.getAllStatusesBetweenDates(baseDateTime.minusDays(6), currentDateTime)).build();
      default:
        break;
    }
    return new StudentProfileStats();
  }

  private Pair<Long, Double> getMonthlyPercentUMPBasedOnStatus(final String... statusCode) {
    val startDatePreviousMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(59);
    val endDatePreviousMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999).minusDays(30);
    val startDateCurrentMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(29);
    val endDateCurrentMonth = LocalDateTime.now();
    val previousMonthResult = this.studentProfileRepository.countByStudentRequestStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDatePreviousMonth, endDatePreviousMonth);
    val currentMonthResult = this.studentProfileRepository.countByStudentRequestStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDateCurrentMonth, endDateCurrentMonth);
    return Pair.of(currentMonthResult, findPercentage(previousMonthResult, currentMonthResult));
  }

  private Pair<Long, Double> getMonthlyPercentUMPWithDocsBasedOnStatus(final String... statusCode) {
    val startDatePreviousMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(59);
    val endDatePreviousMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999).minusDays(30);
    val startDateCurrentMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(29);
    val endDateCurrentMonth = LocalDateTime.now();
    val previousMonthResult = this.studentProfileRepository.findNumberOfStudentRequestsWithDocumentsStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDatePreviousMonth, endDatePreviousMonth);
    val currentMonthResult = this.studentProfileRepository.findNumberOfStudentRequestsWithDocumentsStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDateCurrentMonth, endDateCurrentMonth);
    return Pair.of(currentMonthResult, findPercentage(previousMonthResult, currentMonthResult));
  }

  private double findPercentage(long previousMonthResult, long currentMonthResult) {
    final double percentVal;
    if (previousMonthResult == 0 && currentMonthResult != 0) {
      percentVal = currentMonthResult;
    } else if (currentMonthResult == 0 && previousMonthResult != 0) {
      percentVal = -previousMonthResult;
    } else if (currentMonthResult == 0) {
      percentVal = 0.0;
    } else {
      double increase = (double) (currentMonthResult - previousMonthResult) / previousMonthResult;
      percentVal = increase * 100;
    }
    return percentVal;
  }

  private Map<String, Long> getAllStatusesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
    Map<String, Long> allStatusMap = new LinkedHashMap<>();
    for (val status : StudentProfileStatusCodes.values()) {
      val results = this.studentProfileRepository.countByStudentRequestStatusCodeInAndStatusUpdateDateBetween(List.of(status.toString()), startDate, endDate);
      allStatusMap.put(status.toString(), results);
    }
    return allStatusMap;
  }

  private StudentProfileStats getRequestsCompletedLastMonths() {
    LocalDateTime currentDate = LocalDateTime.now();
    Map<String, Long> requestsCompletionsInLastMonths = new LinkedHashMap<>();
    for (int i = 12; i >= 0; i--) {
      LocalDateTime startDate = currentDate.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
      LocalDateTime endDate = currentDate.minusMonths(i).withDayOfMonth(currentDate.minusMonths(i).toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
      val umpNumbers = this.studentProfileRepository.countByStudentRequestStatusCodeInAndStatusUpdateDateBetween(Arrays.asList("COMPLETED"), startDate, endDate);
      val monthName = (i == 0) ? "CURRENT" : startDate.getMonth().toString();
      requestsCompletionsInLastMonths.put(monthName, umpNumbers);
    }
    return StudentProfileStats.builder().completionsInLastMonths(requestsCompletionsInLastMonths).build();
  }

  private StudentProfileStats getAverageUMPCompletionTime() {
    val umpStat = this.studentProfileRepository.findCompletionProcessAverageTime();
    return StudentProfileStats.builder().averageTimeToCompleteRequest(umpStat.getAverageCompletionTime()).build();
  }

  private StudentProfileStats getRequestsCompletedLastWeek() {
    Map<String, Long> requestsCompletionsInLastWeek = new LinkedHashMap<>();
    LocalDateTime currentDate = LocalDateTime.now();
    for (int i = 6; i >= 0; i--) {
      LocalDateTime startDate = currentDate.minusDays(i).withHour(0).withMinute(0).withSecond(0).withNano(0);
      LocalDateTime endDate = currentDate.minusDays(i).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
      val umpNumbers = this.studentProfileRepository.countByStudentRequestStatusCodeInAndStatusUpdateDateBetween(Arrays.asList("COMPLETED"), startDate, endDate);
      requestsCompletionsInLastWeek.put(startDate.getDayOfWeek().toString(), umpNumbers);
    }
    return StudentProfileStats.builder().completionsInLastWeek(requestsCompletionsInLastWeek).build();
  }
}
