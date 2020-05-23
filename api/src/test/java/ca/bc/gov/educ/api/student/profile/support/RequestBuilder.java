package ca.bc.gov.educ.api.student.profile.support;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import ca.bc.gov.educ.api.student.profile.model.DocumentEntity;
import ca.bc.gov.educ.api.student.profile.model.StudentProfileEntity;

public class RequestBuilder {
  DocumentEntity document;

  UUID requestID = UUID.randomUUID();

  UUID digitalID = UUID.randomUUID();

  String requestStatusCode = "INITREV";

  String legalFirstName = "Tom";

  String legalMiddleNames;

  String legalLastName = "Wayen";

  String recordedLegalLastName = "Wayen";

  String recordedPen = "123456789";

  String createUser = "API";

  Date createDate = new Date();

  String updateUser = "API";

  String emailVerified = "N";

  Date updateDate = new Date();

  public RequestBuilder withDocument(DocumentEntity document) {
    this.document = document;
    return this;
  }

  public RequestBuilder withRequestID(UUID requestID) {
    this.requestID = requestID;
    return this;
  }

  public RequestBuilder withoutRequestID() {
    this.requestID = null;
    return this;
  }

  public RequestBuilder withDigitalID(UUID digitalID) {
    this.digitalID = digitalID;
    return this;
  }

  public RequestBuilder withRequestStatusCode(String requestStatusCode) {
    this.requestStatusCode = requestStatusCode;
    return this;
  }

  public RequestBuilder withLegalFirstName(String legalFirstName) {
    this.legalFirstName = legalFirstName;
    return this;
  }

  public RequestBuilder withLegalLastNamee(String legalLastName) {
    this.legalLastName = legalLastName;
    return this;
  }

  public RequestBuilder withoutCreateAndUpdateUser() {
    this.createUser = null;
    this.createDate = null;
    this.updateUser = null;
    this.updateDate = null;
    return this;
  }

  public StudentProfileEntity build() {
    StudentProfileEntity request = new StudentProfileEntity();
    request.setCreateUser(this.createUser);
    request.setCreateDate(LocalDateTime.now());
    request.setUpdateUser(this.updateUser);
    request.setUpdateDate(LocalDateTime.now());

    request.setRequestID(this.requestID);
    request.setDigitalID(this.digitalID);
    request.setRequestStatusCode(this.requestStatusCode);
    request.setLegalFirstName(this.legalFirstName);
    request.setLegalLastName(this.legalLastName);
    request.setRecordedLegalLastName(this.recordedLegalLastName);
    request.setRecordedPen(this.recordedPen);
    request.setEmailVerified(this.emailVerified);
    return request;
  }


}
