package ca.bc.gov.educ.api.student.profile.mappers;

import ca.bc.gov.educ.api.student.profile.model.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocMetadata;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, Base64Mapper.class, LocalDateTimeMapper.class})
@SuppressWarnings("squid:S1214")
public interface DocumentMapper {

  DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);

  StudentProfileDocument toStructure(DocumentEntity entity);

  @Mapping(target = "studentProfileEntity", ignore = true)
  DocumentEntity toModel(StudentProfileDocument struct);

  StudentProfileDocMetadata toMetadataStructure(DocumentEntity entity);
}
