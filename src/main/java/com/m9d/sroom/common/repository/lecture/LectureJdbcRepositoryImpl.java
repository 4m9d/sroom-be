package com.m9d.sroom.common.repository.lecture;

import com.m9d.sroom.common.dto.LectureDto;
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
    public LectureDto save(LectureDto lectureDto) {
        jdbcTemplate.update(LectureRepositorySql.SAVE,
                lectureDto.getCourseId(),
                lectureDto.getSourceId(),
                lectureDto.getPlaylist(),
                lectureDto.getLectureIndex(),
                lectureDto.getMemberId(),
                lectureDto.getChannel());
        return getById(jdbcTemplate.queryForObject(LectureRepositorySql.GET_LAST_ID, Long.class));
    }

    public LectureDto getById(Long lectureId) {
        return jdbcTemplate.queryForObject(LectureRepositorySql.GET_BY_ID, LectureDto.getRowMapper(), lectureId);
    }

    @Override
    @Transactional
    public LectureDto updateById(Long lectureId, LectureDto lectureDto) {
        jdbcTemplate.update(LectureRepositorySql.UPDATE_BY_ID,
                lectureDto.getCourseId(),
                lectureDto.getSourceId(),
                lectureDto.getPlaylist(),
                lectureDto.getLectureIndex(),
                lectureDto.getReviewed(),
                lectureDto.getMemberId(),
                lectureDto.getChannel(),
                lectureDto.getId());

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
    public List<LectureDto> getListByCourseId(Long courseId) {
        return jdbcTemplate.query(LectureRepositorySql.GET_LIST_BY_COURSE_ID, LectureDto.getRowMapper(), courseId);
    }

    @Override
    public List<String> getChannelListOrderByCount(Long member_id) {
        return jdbcTemplate.query(LectureRepositorySql.GET_MOST_ENROLLED_CHANNELS_BY_MEMBER_ID_QUERY,
                (rs, rowNum) -> rs.getString("channel"), member_id);
    }
}
