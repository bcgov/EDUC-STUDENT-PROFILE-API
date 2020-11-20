package ca.bc.gov.educ.api.student.profile.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StudentProfileMVCConfig implements WebMvcConfigurer {

  @Getter(AccessLevel.PRIVATE)
  private final StudentProfileInterceptor studentProfileInterceptor;

  @Autowired
  public StudentProfileMVCConfig(final StudentProfileInterceptor studentProfileInterceptor) {
    this.studentProfileInterceptor = studentProfileInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(studentProfileInterceptor).addPathPatterns("/**");
  }
}
