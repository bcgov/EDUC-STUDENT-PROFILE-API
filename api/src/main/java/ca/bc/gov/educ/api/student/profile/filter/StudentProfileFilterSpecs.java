package ca.bc.gov.educ.api.student.profile.filter;

import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
public class StudentProfileFilterSpecs {

  private final FilterSpecifications<StudentProfileEntity, ChronoLocalDate> dateFilterSpecifications;
  private final FilterSpecifications<StudentProfileEntity, ChronoLocalDateTime<?>> dateTimeFilterSpecifications;
  private final FilterSpecifications<StudentProfileEntity, Integer> integerFilterSpecifications;
  private final FilterSpecifications<StudentProfileEntity, String> stringFilterSpecifications;
  private final FilterSpecifications<StudentProfileEntity, Long> longFilterSpecifications;
  private final FilterSpecifications<StudentProfileEntity, UUID> uuidFilterSpecifications;
  private final Converters converters;

  public StudentProfileFilterSpecs(FilterSpecifications<StudentProfileEntity, ChronoLocalDate> dateFilterSpecifications, FilterSpecifications<StudentProfileEntity, ChronoLocalDateTime<?>> dateTimeFilterSpecifications, FilterSpecifications<StudentProfileEntity, Integer> integerFilterSpecifications, FilterSpecifications<StudentProfileEntity, String> stringFilterSpecifications, FilterSpecifications<StudentProfileEntity, Long> longFilterSpecifications, FilterSpecifications<StudentProfileEntity, UUID> uuidFilterSpecifications, Converters converters) {
    this.dateFilterSpecifications = dateFilterSpecifications;
    this.dateTimeFilterSpecifications = dateTimeFilterSpecifications;
    this.integerFilterSpecifications = integerFilterSpecifications;
    this.stringFilterSpecifications = stringFilterSpecifications;
    this.longFilterSpecifications = longFilterSpecifications;
    this.uuidFilterSpecifications = uuidFilterSpecifications;
    this.converters = converters;
  }

  public Specification<StudentProfileEntity> getDateTypeSpecification(String fieldName, String filterValue, FilterOperation filterOperation) {
    return getSpecification(fieldName, filterValue, filterOperation, converters.getFunction(ChronoLocalDate.class), dateFilterSpecifications);
  }

  public Specification<StudentProfileEntity> getDateTimeTypeSpecification(String fieldName, String filterValue, FilterOperation filterOperation) {
    return getSpecification(fieldName, filterValue, filterOperation, converters.getFunction(ChronoLocalDateTime.class), dateTimeFilterSpecifications);
  }

  public Specification<StudentProfileEntity> getIntegerTypeSpecification(String fieldName, String filterValue, FilterOperation filterOperation) {
    return getSpecification(fieldName, filterValue, filterOperation, converters.getFunction(Integer.class), integerFilterSpecifications);
  }

  public Specification<StudentProfileEntity> getLongTypeSpecification(String fieldName, String filterValue, FilterOperation filterOperation) {
    return getSpecification(fieldName, filterValue, filterOperation, converters.getFunction(Long.class), longFilterSpecifications);
  }

  public Specification<StudentProfileEntity> getStringTypeSpecification(String fieldName, String filterValue, FilterOperation filterOperation) {
    return getSpecification(fieldName, filterValue, filterOperation, converters.getFunction(String.class), stringFilterSpecifications);
  }
  public Specification<StudentProfileEntity> getUUIDTypeSpecification(String fieldName, String filterValue, FilterOperation filterOperation) {
    return getSpecification(fieldName, filterValue, filterOperation, converters.getFunction(UUID.class), uuidFilterSpecifications);
  }

  private <T extends Comparable<T>> Specification<StudentProfileEntity> getSpecification(String fieldName,
                                                                                     String filterValue,
                                                                                     FilterOperation filterOperation,
                                                                                     Function<String, T> converter,
                                                                                     FilterSpecifications<StudentProfileEntity, T> specifications) {
    FilterCriteria<T> criteria = new FilterCriteria<>(fieldName, filterValue, filterOperation, converter);
    return specifications.getSpecification(criteria.getOperation()).apply(criteria);
  }
}
