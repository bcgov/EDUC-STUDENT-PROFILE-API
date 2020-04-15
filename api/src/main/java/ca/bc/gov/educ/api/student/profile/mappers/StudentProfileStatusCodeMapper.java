package ca.bc.gov.educ.api.student.profile.mappers;

import ca.bc.gov.educ.api.student.profile.model.StudentProfileStatusCodeEntity;
import ca.bc.gov.educ.api.student.profile.struct.StudentProfileStatusCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UUIDMapper.class)
@SuppressWarnings("squid:S1214")
public interface StudentProfileStatusCodeMapper {


    StudentProfileStatusCodeMapper mapper = Mappers.getMapper(StudentProfileStatusCodeMapper.class);

    StudentProfileStatusCode toStructure(StudentProfileStatusCodeEntity entity);

    @Mapping(target = "updateUser", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "createUser", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    StudentProfileStatusCodeEntity toModel(StudentProfileStatusCode struct);
}
