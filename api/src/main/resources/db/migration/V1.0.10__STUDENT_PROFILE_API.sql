ALTER TABLE STUDENT_PROFILE_REQUEST_DOCUMENT
    DROP CONSTRAINT STUDENT_PROFILE_REQUEST_DOCUMENT_STUDENT_PROFILE_REQUEST_ID_FK;

CREATE INDEX STUDENT_PROFILE_REQUEST_DOCUMENT_STUDENT_PROFILE_REQUEST_ID_IDX ON STUDENT_PROFILE_REQUEST_DOCUMENT (STUDENT_PROFILE_REQUEST_ID) TABLESPACE API_STUDPROFRQST_IDX;
