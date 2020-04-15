package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.struct.BaseRequest;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public abstract class BaseController {
  protected void setAuditColumns(@NotNull BaseRequest baseRequest) {
    if (StringUtils.isBlank(baseRequest.getCreateUser())) {
      baseRequest.setCreateUser(ApplicationProperties.CLIENT_ID);
    }
    if (StringUtils.isBlank(baseRequest.getUpdateUser())) {
      baseRequest.setUpdateUser(ApplicationProperties.CLIENT_ID);
    }
    baseRequest.setCreateDate(LocalDateTime.now().toString());
    baseRequest.setUpdateDate(LocalDateTime.now().toString());
  }
}
