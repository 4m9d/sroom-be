package com.m9d.sroom.member.sql

class MemberSqlQuery {

    public static final String SAVE_MEMBER_QUERY = "INSERT INTO MEMBER(member_code, member_name) values(?, ?)"

    public static final String GET_BY_MEMBER_CODE_QUERY = "SELECT member_code, member_name, member_id, bio FROM MEMBER WHERE member_code = ?"

    public static final String FIND_BY_MEMBER_CODE_QUERY = "SELECT member_code, member_name, member_id, bio FROM MEMBER WHERE member_code = ?"

    public static final String SAVE_REFRESH_TOKEN_QUERY = "UPDATE MEMBER SET refresh_token = ? WHERE member_id = ?"

    public static final String FIND_BY_MEMBER_ID_QUERY = "SELECT member_code, member_name, member_id, bio FROM MEMBER WHERE member_id = ?"

    public static final String GET_REFRESH_BY_ID_QUERY = "SELECT refresh_token FROM MEMBER WHERE member_id = ?"

    public static final String GET_MEMBER_NAME_BY_ID_QUERY = "SELECT member_name FROM MEMBER WHERE member_id = ?"

    public static final String UPDATE_QUIZ_COUNT_QUERY = """
        UPDATE MEMBER
        SET total_solved_count = total_solved_count + ?, total_correct_count = total_correct_count + ?
        WHERE member_id = ?
    """

    public static final String GET_QUIZ_INFO_QUERY = """
        SELECT
        total_solved_count, total_correct_count
        FROM MEMBER
        WHERE member_id = ?
    """

    public static final String ADD_TOTAL_LEARNING_TIME_QUERY = """
        UPDATE MEMBER
        SET total_learning_time = total_learning_time + ?
        WHERE member_id = ?
    """

    public static final String UPDATE_COMPLETION_RATE_QUERY = """
        UPDATE MEMBER
        SET completion_rate = ?
        WHERE member_id = ?
    """

}

