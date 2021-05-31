package ca.bc.gov.educ.api.student.profile.mappers.v1;

import ca.bc.gov.educ.api.student.profile.mappers.LocalDateTimeMapper;
import ca.bc.gov.educ.api.student.profile.mappers.UUIDMapper;
import ca.bc.gov.educ.api.student.profile.model.v1.StudentProfileEntity;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class})
@SuppressWarnings("squid:S1214")
public interface StudentProfileEntityMapper {

  StudentProfileEntityMapper mapper = Mappers.getMapper(StudentProfileEntityMapper.class);

  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  StudentProfile toStructure(StudentProfileEntity entity);

  StudentProfileEntity toModel(StudentProfile struct);
}
