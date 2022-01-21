package ca.bc.gov.educ.api.student.profile.controller.v1;

import ca.bc.gov.educ.api.student.profile.constants.StatsType;
import ca.bc.gov.educ.api.student.profile.controller.BaseController;
import ca.bc.gov.educ.api.student.profile.endpoint.v1.StudentProfileEndpoint;
import ca.bc.gov.educ.api.student.profile.exception.InvalidParameterException;
import ca.bc.gov.educ.api.student.profile.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.student.profile.exception.StudentProfileRuntimeException;
import ca.bc.gov.educ.api.student.profile.exception.errors.ApiError;
import ca.bc.gov.educ.api.student.profile.filter.FilterOperation;
import ca.bc.gov.educ.api.student.profile.filter.StudentProfileFilterSpecs;
import ca.bc.gov.educ.api.student.profile.mappers.v1.StudentProfileEntityMapper;
import ca.bc.gov.educ.api.student.profile.mappers.v1.StudentProfileGenderCodeMapper;
import ca.bc.gov.educ.api.student.profile.mappers.v1.StudentProfileStatusCodeMapper;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileService;
import ca.bc.gov.educ.api.student.profile.service.StudentProfileStatsService;
import ca.bc.gov.educ.api.student.profile.struct.*;
import ca.bc.gov.educ.api.student.profile.utils.JsonUtil;
import ca.bc.gov.educ.api.student.profile.utils.UUIDUtil;
import ca.bc.gov.educ.api.student.profile.validator.StudentProfilePayloadValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@Slf4j
public class StudentProfileController extends BaseController implements StudentProfileEndpoint {

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfilePayloadValidator payloadValidator;
  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileService service;
  private static final StudentProfileEntityMapper mapper = StudentProfileEntityMapper.mapper;
  private static final StudentProfileStatusCodeMapper statusCodeMapper = StudentProfileStatusCodeMapper.mapper;
  private static final StudentProfileGenderCodeMapper genderCodeMapper = StudentProfileGenderCodeMapper.mapper;
  private final StudentProfileFilterSpecs studentProfileFilterSpecs;
  private final StudentProfileStatsService studentProfileStatsService;

  @Autowired
  StudentProfileController(final StudentProfileService studentProfile, final StudentProfilePayloadValidator payloadValidator, final StudentProfileFilterSpecs studentProfileFilterSpecs, final StudentProfileStatsService studentProfileStatsService) {
    this.service = studentProfile;
    this.payloadValidator = payloadValidator;
    this.studentProfileFilterSpecs = studentProfileFilterSpecs;
    this.studentProfileStatsService = studentProfileStatsService;
  }

  public StudentProfile retrieveStudentProfile(String studentProfileRequestId) {
    return mapper.toStructure(getService().retrieveStudentProfile(UUIDUtil.fromString(studentProfileRequestId)));
  }

  @Override
  public Iterable<StudentProfile> findStudentProfiles(final String digitalID, final String status, final String pen) {
    return getService().findStudentProfiles(UUIDUtil.fromString(digitalID), status, pen).stream().map(mapper::toStructure).collect(Collectors.toList());
  }

  public StudentProfile createStudentProfile(StudentProfileCreate studentProfile) {
    validatePayload(studentProfile, true);
    setAuditColumns(studentProfile);
    return mapper.toStructure(getService().createStudentProfile(mapper.toModel(studentProfile), studentProfile.getDocumentIDs()));
  }

  public StudentProfile updateStudentProfile(StudentProfile studentProfile) {
    validatePayload(studentProfile, false);
    setAuditColumns(studentProfile);
    return mapper.toStructure(getService().updateStudentProfile(mapper.toModel(studentProfile)));
  }

  public List<StudentProfileStatusCode> getStudentProfileStatusCodes() {
    val studentProfileStatusCodes = new ArrayList<StudentProfileStatusCode>();
    getService().getStudentProfileStatusCodesList().forEach(element -> studentProfileStatusCodes.add(statusCodeMapper.toStructure(element)));
    return studentProfileStatusCodes;
  }

  public List<GenderCode> getGenderCodes() {
    return getService().getGenderCodesList().stream().map(genderCodeMapper::toStructure).collect(Collectors.toList());
  }


  private void validatePayload(StudentProfile studentProfile, boolean isCreateOperation) {
    val validationResult = getPayloadValidator().validatePayload(studentProfile, isCreateOperation);
    if (!validationResult.isEmpty()) {
      ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
  }


  @Override
  @Transactional
  public ResponseEntity<Void> deleteById(final UUID studentProfileRequestId) {
    getService().deleteById(studentProfileRequestId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @Transactional(propagation = Propagation.SUPPORTS)
  public CompletableFuture<Page<StudentProfile>> findAll(Integer pageNumber, Integer pageSize, String sortCriteriaJson, String searchCriteriaListJson) {
    final List<Sort.Order> sorts = new ArrayList<>();
    Specification<StudentProfileEntity> studentProfileEntitySpecification = null;
    try {
      getSortCriteria(sortCriteriaJson, sorts);
      if (StringUtils.isNotBlank(searchCriteriaListJson)) {
        List<SearchCriteria> criteriaList = JsonUtil.mapper.readValue(searchCriteriaListJson, new TypeReference<List<SearchCriteria>>() {
        });
        studentProfileEntitySpecification = getStudentProfileEntitySpecification(criteriaList);
      }
    } catch (JsonProcessingException e) {
      throw new StudentProfileRuntimeException(e.getMessage());
    }
    return getService().findAll(studentProfileEntitySpecification, pageNumber, pageSize, sorts).thenApplyAsync(studentProfilEntities -> studentProfilEntities.map(mapper::toStructure));
  }

  @Override
  public ResponseEntity<StudentProfileStats> getStats(@NonNull final StatsType statsType) {
    return ResponseEntity.ok(this.studentProfileStatsService.getStats(statsType));
  }


  private void getSortCriteria(String sortCriteriaJson, List<Sort.Order> sorts) throws JsonProcessingException {
    if (StringUtils.isNotBlank(sortCriteriaJson)) {
      Map<String, String> sortMap = JsonUtil.mapper.readValue(sortCriteriaJson, new TypeReference<Map<String, String>>() {
      });
      sortMap.forEach((k, v) -> {
        if ("ASC".equalsIgnoreCase(v)) {
          sorts.add(new Sort.Order(Sort.Direction.ASC, k));
        } else {
          sorts.add(new Sort.Order(Sort.Direction.DESC, k));
        }
      });
    }
  }

  private Specification<StudentProfileEntity> getStudentProfileEntitySpecification(List<SearchCriteria> criteriaList) {
    Specification<StudentProfileEntity> penRequestSpecs = null;
    if (!criteriaList.isEmpty()) {
      var i = 0;
      for (SearchCriteria criteria : criteriaList) {
        if (criteria.getKey() != null && criteria.getOperation() != null && criteria.getValueType() != null) {
          Specification<StudentProfileEntity> typeSpecification = getTypeSpecification(criteria.getKey(), criteria.getOperation(), criteria.getValue(), criteria.getValueType());
          if (i == 0) {
            penRequestSpecs = Specification.where(typeSpecification);
          } else {
            penRequestSpecs = penRequestSpecs.and(typeSpecification);
          }
          i++;
        } else {
          throw new InvalidParameterException("Search Criteria can not contain null values for", criteria.getKey(), criteria.getOperation().toString(), criteria.getValueType().toString());
        }
      }
    }
    return penRequestSpecs;
  }

  private Specification<StudentProfileEntity> getTypeSpecification(String key, FilterOperation filterOperation, String value, ValueType valueType) {
    Specification<StudentProfileEntity> studentProfileSpecs = null;
    switch (valueType) {
      case STRING:
        studentProfileSpecs = studentProfileFilterSpecs.getStringTypeSpecification(key, value, filterOperation);
        break;
      case DATE_TIME:
        studentProfileSpecs = studentProfileFilterSpecs.getDateTimeTypeSpecification(key, value, filterOperation);
        break;
      case LONG:
        studentProfileSpecs = studentProfileFilterSpecs.getLongTypeSpecification(key, value, filterOperation);
        break;
      case INTEGER:
        studentProfileSpecs = studentProfileFilterSpecs.getIntegerTypeSpecification(key, value, filterOperation);
        break;
      case DATE:
        studentProfileSpecs = studentProfileFilterSpecs.getDateTypeSpecification(key, value, filterOperation);
        break;
      case UUID:
        studentProfileSpecs = studentProfileFilterSpecs.getUUIDTypeSpecification(key, value, filterOperation);
        break;
      default:
        break;
    }
    return studentProfileSpecs;
  }

}

