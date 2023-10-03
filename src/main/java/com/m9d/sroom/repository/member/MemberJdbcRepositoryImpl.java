package com.m9d.sroom.repository.member;

import com.m9d.sroom.global.mapper.Member;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberJdbcRepositoryImpl implements MemberRepository {

    JdbcTemplate jdbcTemplate;

    public MemberJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Member save(Member member) {
        jdbcTemplate.update(MemberRepositorySql.SAVE,
                member.getMemberCode(),
                member.getMemberName());
        return getById(jdbcTemplate.queryForObject(MemberRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public Member getById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_ID, Member.getRowMapper(), memberId);
    }

    @Override
    public Member getByCode(String memberCode) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_CODE, Member.getRowMapper(), memberCode);
    }

    @Override
    public Optional<Member> findByCode(String memberCode) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_CODE, Member.getRowMapper(),
                    memberCode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Member> findById(Long memberId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_ID, Member.getRowMapper(),
                    memberId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Member updateById(Long memberId, Member member) {
        jdbcTemplate.update(MemberRepositorySql.UPDATE_BY_ID,
                member.getMemberName(),
                member.getRefreshToken(),
                member.getTotalSolvedCount(),
                member.getTotalCorrectCount(),
                member.getCompletionRate(),
                member.getTotalLearningTime(),
                member.getStatus(),
                member.getBio(),
                memberId);
        return getById(memberId);
    }

    @Override
    public void updateRefreshTokenById(Long memberId, String refreshToken) {

    }

    @Override
    public void addQuizCountById(Long memberId, int quizCount, int correctCount) {

    }

    @Override
    public void addTotalLearningTimeById(Long memberId, int timeToAddInSecond) {

    }

    @Override
    public void updateCompletionRateById(Long memberId, int completionRate) {

    }

    @Override
    public Integer countCompletedCourseById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.COUNT_COMPLETED_COURSE_BY_ID, Integer.class, memberId);
    }

    @Override
    public Integer countCourseById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.COUNT_COURSE_BY_ID, Integer.class, memberId);
    }
}
