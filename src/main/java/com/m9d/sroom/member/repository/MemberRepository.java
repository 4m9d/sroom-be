package com.m9d.sroom.member.repository;

import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.m9d.sroom.member.sql.MemberSqlQuery.*;

@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String memberCode, String memberName) {
        jdbcTemplate.update(SAVE_MEMBER_QUERY, memberCode, memberName);
    }

    public Member getByMemberCode(String memberCode) {
        try {
            Member member = jdbcTemplate.queryForObject(GET_BY_MEMBER_CODE_QUERY, memberRowMapper, memberCode);
            return member;
        } catch (EmptyResultDataAccessException e) {
            throw new MemberNotFoundException();
        }
    }

    public Optional<Member> findByMemberCode(String memberCode) {
        try {
            Member member = jdbcTemplate.queryForObject(FIND_BY_MEMBER_CODE_QUERY, memberRowMapper, memberCode);
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> Member.builder()
            .memberCode(rs.getString("member_code"))
            .memberName(rs.getString("member_name"))
            .memberId(rs.getLong("member_id"))
            .bio(rs.getString("bio"))
            .build();

    public void saveRefreshToken(Long memberId, String refreshToken) {
        jdbcTemplate.update(SAVE_REFRESH_TOKEN_QUERY, refreshToken, memberId);
    }

    public Optional<Member> findByMemberId(Long memberId) {
        try {
            Member member = jdbcTemplate.queryForObject(FIND_BY_MEMBER_ID_QUERY, memberRowMapper, memberId);
            return Optional.ofNullable(member);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public String getRefreshById(Long memberId) {
        try {
            String refreshToken = jdbcTemplate.queryForObject(GET_REFRESH_BY_ID_QUERY, (rs, rowNum) -> rs.getString(1), memberId);
            return refreshToken;
        } catch (EmptyResultDataAccessException e) {
            throw new MemberNotFoundException();
        }
    }
}
