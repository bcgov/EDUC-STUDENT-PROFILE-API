package ca.bc.gov.educ.api.student.profile.mappers.v1;

import ca.bc.gov.educ.api.student.profile.mappers.UUIDMapper;
import ca.bc.gov.educ.api.student.profile.model.v1.GenderCodeEntity;
import ca.bc.gov.educ.api.student.profile.struct.GenderCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UUIDMapper.class)
@SuppressWarnings("squid:S1214")
public interface StudentProfileGenderCodeMapper {


    StudentProfileGenderCodeMapper mapper = Mappers.getMapper(StudentProfileGenderCodeMapper.class);

    GenderCode toStructure(GenderCodeEntity entity);

    @Mapping(target = "updateUser", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "createUser", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    GenderCodeEntity toModel(GenderCode struct);
}
