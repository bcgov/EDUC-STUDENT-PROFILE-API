UPDATE STUDENT_PROFILE_REQUEST_GENDER_CODE
SET EXPIRY_DATE = SYSDATE
WHERE GENDER_CODE in ('X','U');
