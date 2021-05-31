package ca.bc.gov.educ.api.student.profile.mappers.v1;

import ca.bc.gov.educ.api.student.profile.mappers.LocalDateTimeMapper;
import ca.bc.gov.educ.api.student.profile.mappers.UUIDMapper;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileMacroEntity;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileMacro;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class})
@SuppressWarnings("squid:S1214")
public interface StudentProfileMacroMapper {

  StudentProfileMacroMapper mapper = Mappers.getMapper(StudentProfileMacroMapper.class);

  StudentProfileMacro toStructure(StudentProfileMacroEntity entity);

  StudentProfileMacroEntity toModel(StudentProfileMacro struct);
}
