package ca.bc.gov.educ.api.student.profile.controller;

import ca.bc.gov.educ.api.student.profile.struct.StudentProfile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

public abstract class BaseReqControllerTest {

  protected String dummyStudentProfileJson() {
	  return "{\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"recordedPen\":\"127054021\",\"recordedLegalLastName\":\"Wayne\",\"recordedDob\":\"1952-11-01\",\"recordedLegalFirstName\":\"OM\"}";
  }

  protected String dummyStudentProfileJsonWithInitialSubmitDate() {
	  return "{\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"initialSubmitDate\":\"1952-10-31T00:00:00\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"recordedPen\":\"127054021\",\"recordedLegalLastName\":\"Wayne\",\"recordedDob\":\"1952-11-01\",\"recordedLegalFirstName\":\"OM\"}";
  }

  protected String dummyStudentProfileJsonWithInvalidReqID() {
	  return "{\"studentRequestID\":\"0a004b01-7027-17b1-8170-27cb21100000\",\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"initialSubmitDate\":\"1952-10-31T00:00:00\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"recordedPen\":\"127054021\",\"recordedLegalLastName\":\"Wayne\",\"recordedDob\":\"1952-11-01\",\"recordedLegalFirstName\":\"OM\"}";
  }
  
  protected String dummyStudentProfileJsonWithInvalidEmailVerifiedFlag() {
	  return "{\"studentRequestID\":\"0a004b01-7027-17b1-8170-27cb21100000\",\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"Chester\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"initialSubmitDate\":\"1952-10-31T00:00:00\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"n\",\\\"pen\\\":\\\"123456789\\\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"recordedPen\":\"127054021\",\"recordedLegalLastName\":\"Wayne\",\"recordedDob\":\"1952-11-01\",\"recordedLegalFirstName\":\"OM\"}";
  }

  protected String dummyStudentProfileJsonWithValidReqID(String reqId) {
	  return "{\"studentRequestID\":\"" + reqId + "\",\"digitalID\":\"b1e0788a-7dab-4b92-af86-c678e411f1e3\",\"legalFirstName\":\"OM\",\"legalMiddleNames\":\"Grestie\",\"legalLastName\":\"Baulk\",\"dob\":\"1952-10-31\",\"initialSubmitDate\":\"1952-10-31T00:00:00\",\"genderCode\":\"M\",\"email\":\"cbaulk0@bluehost.com\",\"emailVerified\":\"N\",\"currentSchool\":\"Xanthoparmelia wyomingica (Gyel.) Hale\",\"recordedPen\":\"127054021\",\"recordedLegalLastName\":\"Wayne\",\"recordedDob\":\"1952-11-01\",\"recordedLegalFirstName\":\"OM\"}";
  }

  protected StudentProfile getStudentProfileEntityFromJsonString() {
    try {
      return new ObjectMapper().readValue(dummyStudentProfileJson(), StudentProfile.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
