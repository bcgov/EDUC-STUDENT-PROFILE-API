UPDATE STUDENT_PROFILE_REQUEST_MACRO
SET MACRO_TEXT =    'Your information cannot be updated using the details in your update request.' || CHR(10) || CHR(10) ||
                    'For additional information visit: https://www2.gov.bc.ca/gov/content?id=74E29C67215B4988ABCD778F453A3129',
    UPDATE_DATE = to_date('2021-04-28 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
    UPDATE_USER = 'IDIR/JOCOX'
WHERE MACRO_CODE = 'NPF';
