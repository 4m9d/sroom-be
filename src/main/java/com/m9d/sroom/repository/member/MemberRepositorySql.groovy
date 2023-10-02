package com.m9d.sroom.repository.member

class MemberRepositorySql {
    public static final String GET_BY_ID = """
        SELECT 
        member_id, member_code, member_name, refresh_token, total_solved_count, total_correct_count, completion_rate,
        total_learning_time, sign_up_time, status, bio
        FROM MEMBER
        WHERE member_id = ?
    """
    public static final String UPDATE_BY_ID = """
        UPDATE
        MEMBER SET member_name = ?, refresh_token = ?, total_solved_count = ?, total_correct_count = ?,
        completion_rate = ?, total_learning_time = ?, status = ?, bio = ?
        WHERE member_id = ?
    """
    public static final String COUNT_COMPLETED_COURSE_BY_ID = """
        SELECT COUNT(1)
        FROM COURSE
        WHERE member_id = ? AND progress = 100
    """
    public static final String COUNT_COURSE_BY_ID = """
        SELECT COUNT(1)
        FROM COURSE
        WHERE member_id = ?
    """
}
