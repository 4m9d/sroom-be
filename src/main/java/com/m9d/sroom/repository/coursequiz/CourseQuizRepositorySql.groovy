package com.m9d.sroom.repository.coursequiz

class CourseQuizRepositorySql {

    public static final String DELETE_BY_COURSE_ID = """
        DELETE FROM COURSEQUIZ
        WHERE course_id = ?
    """

}
