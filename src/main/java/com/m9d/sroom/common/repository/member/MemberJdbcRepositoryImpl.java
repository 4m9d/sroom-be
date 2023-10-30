package com.m9d.sroom.common.repository.member;

import com.m9d.sroom.common.entity.MemberEntity;
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
    public MemberEntity save(MemberEntity member) {
        jdbcTemplate.update(MemberRepositorySql.SAVE,
                member.getMemberCode(),
                member.getMemberName());
        return getByCode(member.getMemberCode());
    }

    @Override
    public MemberEntity getById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_ID, MemberEntity.getRowMapper(), memberId);
    }

    @Override
    public MemberEntity getByCode(String memberCode) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_CODE, MemberEntity.getRowMapper(), memberCode);
    }

    @Override
    public Optional<MemberEntity> findByCode(String memberCode) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_CODE, MemberEntity.getRowMapper(),
                    memberCode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<MemberEntity> findById(Long memberId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(MemberRepositorySql.GET_BY_ID, MemberEntity.getRowMapper(),
                    memberId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public MemberEntity updateById(Long memberId, MemberEntity member) {
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
    public Integer countCompletedCourseById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.COUNT_COMPLETED_COURSE_BY_ID, Integer.class, memberId);
    }

    @Override
    public Integer countCourseById(Long memberId) {
        return jdbcTemplate.queryForObject(MemberRepositorySql.COUNT_COURSE_BY_ID, Integer.class, memberId);
    }
}
