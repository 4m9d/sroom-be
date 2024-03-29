package com.m9d.sroom.common.repository.lecture;

import com.m9d.sroom.common.entity.jdbctemplate.LectureEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Repository
public class LectureJdbcRepositoryImpl implements LectureRepository {

    private final JdbcTemplate jdbcTemplate;

    public LectureJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public LectureEntity save(LectureEntity lecture) {
        jdbcTemplate.update(LectureRepositorySql.SAVE,
                lecture.getCourseId(),
                lecture.getSourceId(),
                lecture.getPlaylist(),
                lecture.getLectureIndex(),
                lecture.getMemberId(),
                lecture.getChannel());
        return getById(jdbcTemplate.queryForObject(LectureRepositorySql.GET_LAST_ID, Long.class));
    }

    public LectureEntity getById(Long lectureId) {
        return jdbcTemplate.queryForObject(LectureRepositorySql.GET_BY_ID, LectureEntity.getRowMapper(), lectureId);
    }

    @Override
    @Transactional
    public LectureEntity updateById(Long lectureId, LectureEntity lecture) {
        jdbcTemplate.update(LectureRepositorySql.UPDATE_BY_ID,
                lecture.getCourseId(),
                lecture.getSourceId(),
                lecture.getPlaylist(),
                lecture.getLectureIndex(),
                lecture.getReviewed(),
                lecture.getMemberId(),
                lecture.getChannel(),
                lecture.getId());

        return getById(lectureId);
    }

    @Override
    @Transactional
    public void deleteByCourseId(Long courseId) {
        jdbcTemplate.update(LectureRepositorySql.DELETE_BY_COURSE_ID, courseId);
    }

    @Override
    public HashSet<String> getChannelSetByCourseId(Long courseId) {
        return new HashSet<>(jdbcTemplate.query(LectureRepositorySql.GET_CHANNELS_BY_COURSE_ID,
                (rs, rowNum) -> rs.getString("channel"), courseId));
    }

    @Override
    public List<LectureEntity> getListByCourseId(Long courseId) {
        return jdbcTemplate.query(LectureRepositorySql.GET_LIST_BY_COURSE_ID, LectureEntity.getRowMapper(), courseId);
    }

    @Override
    public List<String> getChannelListOrderByCount(Long member_id) {
        return jdbcTemplate.query(LectureRepositorySql.GET_MOST_ENROLLED_CHANNELS_BY_MEMBER_ID_QUERY,
                (rs, rowNum) -> rs.getString("channel"), member_id);
    }
}
