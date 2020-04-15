package ca.bc.gov.educ.api.student.profile.mappers;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileCommentsEntity;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileComments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class})
@SuppressWarnings("squid:S1214")
public interface StudentProfileCommentsMapper {
  StudentProfileCommentsMapper mapper = Mappers.getMapper(StudentProfileCommentsMapper.class);

  StudentProfileComments toStructure(StudentProfileCommentsEntity entity);

  @Mapping(target = "studentProfileEntity", ignore = true)
  StudentProfileCommentsEntity toModel(StudentProfileComments structure);
}
