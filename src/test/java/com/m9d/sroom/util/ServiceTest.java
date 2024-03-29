package com.m9d.sroom.util;

import com.m9d.sroom.common.entity.jdbctemplate.MemberEntity;
import com.m9d.sroom.course.CourseService;
import com.m9d.sroom.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.UUID;

public class ServiceTest extends SroomTest {

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected CourseService courseService;

    protected MemberEntity getNewMember() {
        UUID uuid = UUID.randomUUID();

        String memberCode = uuid.toString();
        return memberService.findOrCreateMember(memberCode);
    }

    protected void saveVideo(String videoCode, int duration, String channel, String thumbnail, int accumulated_rating, int reviewCount, String title, String license, int viewCount) {

        String sql = "INSERT INTO VIDEO(video_code, duration, channel, thumbnail, accumulated_rating, review_count, title, license, view_count) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, videoCode, duration, channel, thumbnail, accumulated_rating, reviewCount, title, license, viewCount);

    }

    protected void savePlaylist(String playlistCode, int duration, String channel, String thumbnail, int accumulated_rating, int reviewCount, String title) {

        String sql = "INSERT INTO PLAYLIST(playlist_code, duration, channel, thumbnail, accumulated_rating, review_count, title, published_at, video_count) values(?, ?, ?, ?, ?, ?, ?, ?, 10)";
        jdbcTemplate.update(sql, playlistCode, duration, channel, thumbnail, accumulated_rating, reviewCount, title, new Timestamp(System.currentTimeMillis()));

    }
}
