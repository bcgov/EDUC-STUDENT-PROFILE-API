INSERT INTO STUDENT_PROFILE_REQUEST_MACRO_TYPE_CODE (STUDENT_PROFILE_REQUEST_MACRO_TYPE_CODE, LABEL, DESCRIPTION,
                                                   DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE, CREATE_USER, CREATE_DATE,
                                                   UPDATE_USER, UPDATE_DATE)
VALUES ('MOREINFO', 'More Information Macro',
        'Macros used when requesting that the student provide more information for a UMPI Request', 1,
        to_date('2020-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        to_date('2099-12-31 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO_TYPE_CODE (STUDENT_PROFILE_REQUEST_MACRO_TYPE_CODE, LABEL, DESCRIPTION,
                                                   DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE, CREATE_USER, CREATE_DATE,
                                                   UPDATE_USER, UPDATE_DATE)
VALUES ('REJECT', 'Reject Reason Macro', 'Macros used when rejecting a UMPI Request', 2,
        to_date('2020-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        to_date('2099-12-31 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO_TYPE_CODE (STUDENT_PROFILE_REQUEST_MACRO_TYPE_CODE, LABEL, DESCRIPTION,
                                                   DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE, CREATE_USER, CREATE_DATE,
                                                   UPDATE_USER, UPDATE_DATE)
values ('COMPLETE', 'Complete Reason Macro', 'Macros used when completing a UMPI Request', 3,
        to_date('2020-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        to_date('2099-12-31 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO (STUDENT_PROFILE_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'PCN',
        'Your information cannot be located using the details provided in your request.' || CHR(10) || CHR(10) ||
        'Please provide all other given names or surnames you have previously used or advise if you have never used any other names.',
        'MOREINFO', 'IDIR/MINYANG', to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO (STUDENT_PROFILE_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'PID',
        'To continue with your update request upload an IMG or PDF of your current Government Issued photo Identification (ID).' ||
        CHR(10) || CHR(10) ||
        'NOTE: If the name listed on the ID you upload is different from what''s in the PEN system, we will update our data to match. ID is covered by the B.C. Freedom of Information Protection of Privacy.',
        'MOREINFO', 'IDIR/MINYANG', to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO (STUDENT_PROFILE_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'SOA',
        'To continue with your update request please confirm the last B.C. Schools you attended or graduated from, including any applications to B.C. Post Secondary Institutions',
        'MOREINFO', 'IDIR/MINYANG', to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO (STUDENT_PROFILE_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'NPF',
        'Your information cannot be located using the details in your update request.' || CHR(10) || CHR(10) ||
        'For additional information visit: https://www2.gov.bc.ca/gov/content?id=74E29C67215B4988ABCD778F453A3129',
        'REJECT', 'IDIR/MINYANG', to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO (STUDENT_PROFILE_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'XPR',
        'The identity of the person making the request cannot be confirmed as the same as the PEN owner.' || CHR(10) ||
        CHR(10) ||
        'For additional information visit: https://www2.gov.bc.ca/gov/content?id=74E29C67215B4988ABCD778F453A3129',
        'REJECT', 'IDIR/MINYANG', to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO (STUDENT_PROFILE_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'NME',
        'Based on the information you have provided, we have updated your Legal Name format in the PEN system now.',
        'COMPLETE', 'IDIR/MINYANG', to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO (STUDENT_PROFILE_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'NMG',
        'Based on the information you have provided, we have updated your Legal Name format and Gender in the PEN system now.',
        'COMPLETE', 'IDIR/MINYANG', to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO STUDENT_PROFILE_REQUEST_MACRO (STUDENT_PROFILE_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'DOB',
        'Based on the information you have provided, we have updated your Date of Birth in the PEN system now.',
        'COMPLETE', 'IDIR/MINYANG', to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MINYANG',
        to_date('2020-06-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

DELETE FROM STUDENT_PROFILE_REQUEST_STATUS_CODE WHERE STUDENT_PROFILE_REQUEST_STATUS_CODE = 'AUTO';
DELETE FROM STUDENT_PROFILE_REQUEST_STATUS_CODE WHERE STUDENT_PROFILE_REQUEST_STATUS_CODE = 'MANUAL';
INSERT INTO STUDENT_PROFILE_REQUEST_STATUS_CODE (STUDENT_PROFILE_REQUEST_STATUS_CODE,LABEL,DESCRIPTION,DISPLAY_ORDER,EFFECTIVE_DATE,EXPIRY_DATE,CREATE_USER,CREATE_DATE,UPDATE_USER,UPDATE_DATE) VALUES ('ABANDONED','Abandoned','email not verified within 1 week.',6,TO_DATE('2020-01-01','YYYY-MM-DD'),TO_DATE('2099-12-31','YYYY-MM-DD'),'IDIR/GRCHWELO',TO_DATE('2019-11-07','YYYY-MM-DD'),'IDIR/GRCHWELO',TO_DATE('2019-11-07','YYYY-MM-DD'));

DECLARE
    l_exst number;
BEGIN
    SELECT COUNT(*) INTO l_exst FROM STUDENT_PROFILE_REQUEST_STATUS_CODE WHERE STUDENT_PROFILE_REQUEST_STATUS_CODE = 'COMPLETED';
    IF l_exst <= 0
    THEN
        INSERT INTO STUDENT_PROFILE_REQUEST_STATUS_CODE (STUDENT_PROFILE_REQUEST_STATUS_CODE,LABEL,DESCRIPTION,DISPLAY_ORDER,EFFECTIVE_DATE,EXPIRY_DATE,CREATE_USER,CREATE_DATE,UPDATE_USER,UPDATE_DATE) VALUES ('COMPLETED','Updates Completed','Request was completed by staff.',5,TO_DATE('2020-01-01','YYYY-MM-DD'),TO_DATE('2099-12-31','YYYY-MM-DD'),'IDIR/GRCHWELO',TO_DATE('2019-11-07','YYYY-MM-DD'),'IDIR/GRCHWELO',TO_DATE('2019-11-07','YYYY-MM-DD'));
    END IF;
END;

