ALTER TABLE STUDENT_PROFILE_REQUEST ADD (
  RECORDED_PEN VARCHAR2(9),
  RECORDED_LEGAL_FIRST_NAME VARCHAR2(40),
  RECORDED_LEGAL_MIDDLE_NAMES VARCHAR2(255),
  RECORDED_LEGAL_LAST_NAME VARCHAR2(40),
  RECORDED_DOB DATE,
  RECORDED_GENDER_CODE VARCHAR2(1),
  RECORDED_EMAIL VARCHAR2(255)
);