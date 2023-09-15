package com.m9d.sroom.review.repository;

import com.m9d.sroom.review.dto.LectureBrief4Review;
import com.m9d.sroom.review.dto.LectureData;
import com.m9d.sroom.review.dto.Review;
import com.m9d.sroom.review.sql.ReviewSqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.m9d.sroom.course.sql.CourseSqlQuery.GET_LAST_INSERT_ID_QUERY;
import static com.m9d.sroom.review.sql.ReviewSqlQuery.*;

@Repository
public class ReviewRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LectureData> getLectureDataListByCourseId(Long courseId) {
        return jdbcTemplate.query(GET_LECTURE_DATA_BY_COURSE_ID,
                (rs, rowNum) -> LectureData.builder()
                        .lectureId(rs.getLong("lecture_id"))
                        .courseId(rs.getLong("course_id"))
                        .sourceId(rs.getLong("source_id"))
                        .isPlaylist(rs.getBoolean("is_playlist"))
                        .lectureIndex(rs.getInt("lecture_index"))
                        .channel(rs.getString("channel"))
                        .isReviewed(rs.getBoolean("is_reviewed"))
                        .build()
                , courseId);
    }

    public LectureData getLectureDataById(Long lectureId){
        return jdbcTemplate.queryForObject(GET_LECTURE_DATA_BY_ID,
                (rs, rowNum) -> LectureData.builder()
                        .lectureId(rs.getLong("lecture_id"))
                        .courseId(rs.getLong("course_id"))
                        .sourceId(rs.getLong("source_id"))
                        .isPlaylist(rs.getBoolean("is_playlist"))
                        .lectureIndex(rs.getInt("lecture_index"))
                        .channel(rs.getString("channel"))
                        .isReviewed(rs.getBoolean("is_reviewed"))
                        .build()
                , lectureId);
    }

    public LectureBrief4Review getVideoCountData(Long lectureId) {
        return jdbcTemplate.queryForObject(GET_VIDEO_COUNT_BY_LECTURE_ID,
                (rs, rowNum) -> LectureBrief4Review.builder()
                        .totalVideoCount(rs.getInt("total_video_count"))
                        .completedVideoCount(rs.getInt("completed_video_count"))
                        .build()
                , lectureId);
    }

    public LectureBrief4Review getPlaylistDataBySourceId(Long sourceId) {
        return jdbcTemplate.queryForObject(GET_PLAYLIST_DATA_BY_SOURCE_ID,
                (rs, rowNum) -> LectureBrief4Review.builder()
                        .title(rs.getString("title"))
                        .thumbnail(rs.getString("thumbnail"))
                        .build()
                , sourceId);
    }

    public LectureBrief4Review getVideoDataBySourceId(Long sourceId) {
        return jdbcTemplate.queryForObject(GET_VIDEO_DATA_BY_SOURCE_ID,
                (rs, rowNum) -> LectureBrief4Review.builder()
                        .title(rs.getString("title"))
                        .thumbnail(rs.getString("thumbnail"))
                        .lectureDuration(rs.getInt("duration"))
                        .build()
                , sourceId);
    }

    public int getViewDurationByLectureId(Long lectureId) {
        return jdbcTemplate.queryForObject(GET_VIEW_DURATION_BY_LECTURE_ID, (rs, rowNum) -> rs.getInt("max_duration"), lectureId);
    }

    public Review getReviewByLectureId(Long lectureId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        return jdbcTemplate.queryForObject(GET_REVIEW_BY_LECTURE_ID,
                (rs, rowNum) -> Review.builder()
                        .reviewId(rs.getLong("review_id"))
                        .sourceCode(rs.getString("source_code"))
                        .memberId(rs.getLong("member_id"))
                        .lectureId(rs.getLong("lecture_id"))
                        .content(rs.getString("content"))
                        .submittedRating(rs.getInt("submitted_rating"))
                        .submittedDate(dateFormat.format(rs.getTimestamp("submitted_date")))
                        .build()
                , lectureId);
    }

    public Long insertReview(Review review) {

        jdbcTemplate.update(INSERT_REVIEW,
                review.getSourceCode(),
                review.getMemberId(),
                review.getLectureId(),
                review.getSubmittedRating(),
                review.getContent());

        Long reviewId = jdbcTemplate.queryForObject(GET_LAST_INSERT_ID_QUERY, Long.class);

        return reviewId;
    }
}