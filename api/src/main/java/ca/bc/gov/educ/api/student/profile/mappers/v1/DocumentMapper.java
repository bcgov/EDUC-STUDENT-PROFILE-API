package ca.bc.gov.educ.api.student.profile.mappers.v1;

import ca.bc.gov.educ.api.student.profile.mappers.Base64Mapper;
import ca.bc.gov.educ.api.student.profile.mappers.LocalDateTimeMapper;
import ca.bc.gov.educ.api.student.profile.mappers.UUIDMapper;
import ca.bc.gov.educ.api.student.profile.model.v1.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocMetadata;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocument;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileDocumentMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, Base64Mapper.class, LocalDateTimeMapper.class})
@SuppressWarnings("squid:S1214")
public interface DocumentMapper {

  DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);

  StudentProfileDocument toStructure(DocumentEntity entity);

  @Mapping(target = "request", ignore = true)
  DocumentEntity toModel(StudentProfileDocument struct);

  StudentProfileDocMetadata toMetadataStructure(DocumentEntity entity);

  @Mapping(target = "studentRequestID", source = "request.studentRequestID")
  @Mapping(target = "digitalID", source = "request.digitalID")
  StudentProfileDocumentMetadata toMetaData(DocumentEntity entity);
}
