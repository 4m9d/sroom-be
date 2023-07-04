package com.m9d.sroom.member.repository;

import com.m9d.sroom.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String memberCode, String memberName) {
        String sql = "INSERT INTO MEMBER(member_code, member_name) values(?, ?)";
        jdbcTemplate.update(sql, memberCode, memberName);
    }

    public Member getByMemberCode(String memberCode) {
        String sql = "SELECT * FROM MEMBER WHERE member_code = ?";
        return jdbcTemplate.query(sql, new Object[]{memberCode}, memberRowMapper).get(0);
    }

    public Optional<Member> findByMemberCode(String memberCode) {
        String sql = "SELECT * FROM MEMBER WHERE member_code = ?";
        List<Member> members = jdbcTemplate.query(sql, new Object[]{memberCode}, memberRowMapper);
        return members.isEmpty() ? Optional.empty() : Optional.of(members.get(0));
    }

    private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> {
        Member member = Member.builder()
                .memberCode(rs.getString("member_code"))
                .memberName(rs.getString("member_name"))
                .memberId(rs.getLong("member_id"))
                .build();
        return member;
    };

    public void saveRefreshToken(Long memberId, String refreshToken) {
        String sql = "UPDATE MEMBER SET refresh_token = ? WHERE member_id = ?";
        jdbcTemplate.update(sql, refreshToken, memberId);
    }
}
