package ca.bc.gov.educ.api.student.profile.service;

import ca.bc.gov.educ.api.student.profile.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileMacroEntity;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileMacroTypeCodeEntity;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileMacroRepository;
import ca.bc.gov.educ.api.student.profile.repository.v1.StudentProfileMacroTypeCodeRepository;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
public class StudentProfileMacroService {
  @Getter(PRIVATE)
  private final StudentProfileMacroRepository studentProfileMacroRepository;
  @Getter(PRIVATE)
  private final StudentProfileMacroTypeCodeRepository studentProfileMacroTypeCodeRepository;

  @Autowired
  public StudentProfileMacroService(StudentProfileMacroRepository studentProfileMacroRepository, StudentProfileMacroTypeCodeRepository studentProfileMacroTypeCodeRepository) {
    this.studentProfileMacroRepository = studentProfileMacroRepository;
    this.studentProfileMacroTypeCodeRepository = studentProfileMacroTypeCodeRepository;
  }

  public List<StudentProfileMacroTypeCodeEntity> findAllMacroTypeCodes() {
    return getStudentProfileMacroTypeCodeRepository().findAll();
  }

  public Optional<StudentProfileMacroTypeCodeEntity> getMacroTypeCode(String code) {
    return getStudentProfileMacroTypeCodeRepository().findById(code);
  }

  public List<StudentProfileMacroEntity> findAllMacros() {
    return getStudentProfileMacroRepository().findAll();
  }

  public Optional<StudentProfileMacroEntity> getMacro(UUID macroId) {
    return getStudentProfileMacroRepository().findById(macroId);
  }

  public List<StudentProfileMacroEntity> findMacrosByMacroTypeCode(String macroTypeCode) {
    return getStudentProfileMacroRepository().findAllByMacroTypeCode(macroTypeCode);
  }

  public StudentProfileMacroEntity createMacro(StudentProfileMacroEntity entity) {
    return getStudentProfileMacroRepository().save(entity);
  }

  public StudentProfileMacroEntity updateMacro(UUID macroId, StudentProfileMacroEntity entity) {
    val result = getStudentProfileMacroRepository().findById(macroId);
    if (result.isPresent()) {
      return getStudentProfileMacroRepository().save(entity);
    } else {
      throw new EntityNotFoundException(entity.getClass(), macroId.toString());
    }
  }
}
