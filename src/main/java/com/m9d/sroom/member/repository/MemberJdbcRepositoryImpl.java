package com.m9d.sroom.member.repository;

import com.m9d.sroom.member.MemberDto;
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
    public MemberDto save(MemberDto memberDto) {
        jdbcTemplate.update(MemberRepositorySql.SAVE,
                memberDto.getMemberCode(),
                memberDto.getMemberName());
        return getByCode(memberDto.getMemberCode());
    }

    @Override
    public MemberDto getById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_ID, MemberDto.getRowMapper(), memberId);
    }

    @Override
    public MemberDto getByCode(String memberCode) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_CODE, MemberDto.getRowMapper(), memberCode);
    }

    @Override
    public Optional<MemberDto> findByCode(String memberCode) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_CODE, MemberDto.getRowMapper(),
                    memberCode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<MemberDto> findById(Long memberId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_ID, MemberDto.getRowMapper(),
                    memberId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public MemberDto updateById(Long memberId, MemberDto memberDto) {
        jdbcTemplate.update(MemberRepositorySql.UPDATE_BY_ID,
                memberDto.getMemberName(),
                memberDto.getRefreshToken(),
                memberDto.getTotalSolvedCount(),
                memberDto.getTotalCorrectCount(),
                memberDto.getCompletionRate(),
                memberDto.getTotalLearningTime(),
                memberDto.getStatus(),
                memberDto.getBio(),
                memberId);
        return getById(memberId);
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
