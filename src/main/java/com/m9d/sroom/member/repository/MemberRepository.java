package com.m9d.sroom.member.repository;

import com.m9d.sroom.material.model.MemberQuizInfo;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.sql.MemberSqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String memberCode, String memberName) {
        jdbcTemplate.update(MemberSqlQuery.SAVE_MEMBER_QUERY, memberCode, memberName);
    }

    public Member getByMemberCode(String memberCode) {
        return jdbcTemplate.queryForObject(MemberSqlQuery.GET_BY_MEMBER_CODE_QUERY, memberRowMapper, memberCode);
    }

    public Optional<Member> findByMemberCode(String memberCode) {
        Member member = queryForObjectOrNull(MemberSqlQuery.FIND_BY_MEMBER_CODE_QUERY, memberRowMapper, memberCode);
        return Optional.ofNullable(member);
    }

    public void saveRefreshToken(Long memberId, String refreshToken) {
        jdbcTemplate.update(MemberSqlQuery.SAVE_REFRESH_TOKEN_QUERY, refreshToken, memberId);
    }

    public String getMemberNameById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberSqlQuery.GET_MEMBER_NAME_BY_ID_QUERY, (rs, rowNum) -> rs.getString(1), memberId);
    }

    public Optional<Member> findByMemberId(Long memberId) {
        Member member = queryForObjectOrNull(MemberSqlQuery.FIND_BY_MEMBER_ID_QUERY, memberRowMapper, memberId);
        return Optional.ofNullable(member);
    }

    public String getRefreshById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberSqlQuery.GET_REFRESH_BY_ID_QUERY, (rs, rowNum) -> rs.getString(1), memberId);
    }

    private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> Member.builder()
            .memberCode(rs.getString("member_code"))
            .memberName(rs.getString("member_name"))
            .memberId(rs.getLong("member_id"))
            .bio(rs.getString("bio"))
            .build();

    private <T> T queryForObjectOrNull(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addQuizCount(Long memberId, int quizCount, int correctCount) {
        jdbcTemplate.update(MemberSqlQuery.UPDATE_QUIZ_COUNT_QUERY, quizCount, correctCount, memberId);
    }

    public MemberQuizInfo getQuizInfoById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberSqlQuery.GET_QUIZ_INFO_QUERY, (rs, rowNum) -> MemberQuizInfo.builder()
                .TotalSolvedCount(rs.getInt("total_solved_count"))
                .totalCorrectCount(rs.getInt("total_correct_count"))
                .build(), memberId);
    }

    public void addTotalLearningTime(Long memberId, int timeToAdd) {
        jdbcTemplate.update(MemberSqlQuery.ADD_TOTAL_LEARNING_TIME_QUERY, timeToAdd, memberId);
    }

    public void updateCompletionRate(Long memberId, double completionRate) {
        jdbcTemplate.update(MemberSqlQuery.UPDATE_COMPLETION_RATE_QUERY, completionRate, memberId);
    }
}
