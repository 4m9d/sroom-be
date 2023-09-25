package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class CourseVideo {

    private Long courseVideoId;

    private Long courseId;

    private Long lectureId;

    private Long videoId;

    private int section;

    private int videoIndex;

    private int startTime;

    private boolean complete;

    private Long summaryId;

    private int lectureIndex;

    private Long memberId;

    private Timestamp lastViewTime;

    private int maxDuration;

    public static RowMapper<CourseVideo> getRowMapper() {
        return (rs, rowNum) -> CourseVideo.builder()
                .courseVideoId(rs.getLong("course_video_id"))
                .courseId(rs.getLong("course_id"))
                .videoId(rs.getLong("video_id"))
                .section(rs.getInt("section"))
                .videoIndex(rs.getInt("video_index"))
                .startTime(rs.getInt("start_time"))
                .complete(rs.getBoolean("is_complete"))
                .summaryId(rs.getLong("summary_id"))
                .lectureIndex(rs.getInt("lecture_index"))
                .memberId(rs.getLong("member_id"))
                .lastViewTime(rs.getTimestamp("last_view_time"))
                .maxDuration(rs.getInt("max_duration"))
                .lectureId(rs.getLong("lecture_id"))
                .build();
    }
}
