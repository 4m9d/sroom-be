package com.m9d.sroom.repository.member

class MemberRepositorySql {
    public static final String GET_BY_ID = """
        SELECT 
        member_id, member_code, member_name, refresh_token, total_solved_count, total_correct_count, completion_rate,
        total_learning_time, sign_up_time, status, bio
        FROM MEMBER
        WHERE member_id = ?
    """
}
