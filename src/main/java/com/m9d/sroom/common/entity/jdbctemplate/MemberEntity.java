package com.m9d.sroom.common.entity.jdbctemplate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Getter
@Builder
public class MemberEntity {

    @Id
    private Long memberId;

    @Setter
    private String memberCode;

    @Setter
    private String memberName;

    @Setter
    private String bio;

    @Setter
    private String refreshToken;

    @Setter
    private Integer totalSolvedCount;

    @Setter
    private Integer totalCorrectCount;

    @Setter
    private Integer completionRate;

    @Setter
    private Integer totalLearningTime;

    private Timestamp signUpTime;

    @Setter
    private Integer status;

    public static RowMapper<MemberEntity> getRowMapper() {
        return (rs, rowNum) -> MemberEntity.builder()
                .memberId(rs.getLong("member_id"))
                .memberCode(rs.getString("member_code"))
                .memberName(rs.getString("member_name"))
                .refreshToken(rs.getString("refresh_token"))
                .totalSolvedCount(rs.getInt("total_solved_count"))
                .totalCorrectCount(rs.getInt("total_correct_count"))
                .completionRate(rs.getInt("completion_rate"))
                .totalLearningTime(rs.getInt("total_learning_time"))
                .signUpTime(rs.getTimestamp("sign_up_time"))
                .status(rs.getInt("status"))
                .bio(rs.getString("bio"))
                .build();
    }
}
