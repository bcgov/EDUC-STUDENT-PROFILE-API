--Allow GENDER_CODE to be null
ALTER TABLE
    STUDENT_PROFILE_REQUEST
    MODIFY
        (GENDER_CODE NULL);
