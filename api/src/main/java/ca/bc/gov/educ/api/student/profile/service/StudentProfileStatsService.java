package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.constants.StudentProfileStatusCodes;
import ca.bc.gov.educ.api.student.profile.constants.StatsType;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileStats;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

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
      case COMPLETIONS_LAST_12_MONTH:
        return this.getRequestsCompletedLastYear();
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
    val dayOfMonth = LocalDateTime.now().getDayOfMonth();

    val startDatePreviousMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1).minusMonths(1);
    val endDatePreviousMonthLength = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).minusMonths(1).getMonth().length(LocalDate.now().minusMonths(1).isLeapYear());
    val endDatePreviousMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).withDayOfMonth(Math.min(dayOfMonth, endDatePreviousMonthLength)).minusMonths(1);
    val startDateCurrentMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1);
    val endDateCurrentMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).withDayOfMonth(dayOfMonth);

    val previousMonthResult = this.studentProfileRepository.countByStudentRequestStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDatePreviousMonth, endDatePreviousMonth);

    val currentMonthResult = this.studentProfileRepository.countByStudentRequestStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDateCurrentMonth, endDateCurrentMonth);
    return Pair.of(currentMonthResult, findPercentage(previousMonthResult, currentMonthResult));
  }

  private Pair<Long, Double> getMonthlyPercentUMPWithDocsBasedOnStatus(final String... statusCode) {
    val dayOfMonth = LocalDateTime.now().getDayOfMonth();

    val startDatePreviousMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1).minusMonths(1);
    val endDatePreviousMonthLength = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).minusMonths(1).getMonth().length(LocalDate.now().minusMonths(1).isLeapYear());
    val endDatePreviousMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).withDayOfMonth(Math.min(dayOfMonth, endDatePreviousMonthLength)).minusMonths(1);
    val startDateCurrentMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1);
    val endDateCurrentMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).withDayOfMonth(dayOfMonth);
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

  private StudentProfileStats getRequestsCompletedLastYear() {
    LocalDateTime currentDate = LocalDateTime.now();
    LocalDateTime fromDate = currentDate.withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1).minusMonths(11);
    val umpStats = this.studentProfileRepository.findStatusAndStatusUpdateDatesBetweenForStatuses(fromDate, currentDate, Collections.singletonList("COMPLETED"));
    Map<String, Integer> penReqCompletionsInLast12Months = new HashMap<>();
    for (val umpStat : umpStats) {
      val month = umpStat.getStatusUpdateDate().getMonth().toString();
      if (penReqCompletionsInLast12Months.containsKey(month)) {
        val currentCount = penReqCompletionsInLast12Months.get(month);
        penReqCompletionsInLast12Months.put(month, currentCount + 1);
      } else {
        penReqCompletionsInLast12Months.put(month, 1);
      }
    }
    Arrays.stream(Month.values()).forEach(el -> {
      if (!penReqCompletionsInLast12Months.containsKey(el.toString())) {
        penReqCompletionsInLast12Months.put(el.toString(), 0);
      }
    });
    val sortedKeys = new ArrayList<>(penReqCompletionsInLast12Months.keySet()).stream().sorted(this::monthComparator).collect(Collectors.toList());
    Map<String, Integer> sortedMap = createSortedMap(penReqCompletionsInLast12Months, sortedKeys);
    return StudentProfileStats.builder().completionsInLastTwelveMonth(sortedMap).build();
  }

  private Map<String, Integer> createSortedMap(Map<String, Integer> unsortedMap, List<String> sortedKeys) {
    Map<String, Integer> sortedMap = new LinkedHashMap<>();
    for (val key : sortedKeys) {
      sortedMap.put(key, unsortedMap.get(key));
    }
    return sortedMap;
  }

  private int monthComparator(String month1, String month2) {
    return Month.valueOf(month1).getValue() - Month.valueOf(month2).getValue();
  }

  private int dayComparator(String day1, String day2) {
    return DayOfWeek.valueOf(day1).getValue() - DayOfWeek.valueOf(day2).getValue();
  }

  private StudentProfileStats getAverageUMPCompletionTime() {
    val umpStat = this.studentProfileRepository.findCompletionProcessAverageTime();
    return StudentProfileStats.builder().averageTimeToCompleteRequest(umpStat.getAverageCompletionTime()).build();
  }

  private StudentProfileStats getRequestsCompletedLastWeek() {
    LocalDateTime currentDate = LocalDateTime.now();
    LocalDateTime fromDate = currentDate.withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(6);
    val umpStats = this.studentProfileRepository.findStatusAndStatusUpdateDatesBetweenForStatuses(fromDate, currentDate, Collections.singletonList("COMPLETED"));
    Map<String, Integer> penReqCompletionsInLastWeek = new HashMap<>();
    for (val umpStat : umpStats) {
      val day = umpStat.getStatusUpdateDate().getDayOfWeek().toString();
      if (penReqCompletionsInLastWeek.containsKey(day)) {
        val currentCount = penReqCompletionsInLastWeek.get(day);
        penReqCompletionsInLastWeek.put(day, currentCount + 1);
      } else {
        penReqCompletionsInLastWeek.put(day, 1);
      }
    }
    Arrays.stream(DayOfWeek.values()).forEach(el -> {
      if (!penReqCompletionsInLastWeek.containsKey(el.toString())) {
        penReqCompletionsInLastWeek.put(el.toString(), 0);
      }
    });
    val sortedKeys = new ArrayList<>(penReqCompletionsInLastWeek.keySet()).stream().sorted(this::dayComparator).collect(Collectors.toList());
    Map<String, Integer> sortedMap = createSortedMap(penReqCompletionsInLastWeek, sortedKeys);
    return StudentProfileStats.builder().completionsInLastWeek(sortedMap).build();
  }
}
