package com.m9d.sroom.repository.lecture;

import com.m9d.sroom.global.model.Lecture;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Repository
public class LectureJdbcRepositoryImpl implements LectureRepository{
    @Override
    public Long save(Lecture lecture) {
        return null;
    }

    @Override
    public void deleteByCourseId(Long courseId) {

    }

    @Override
    public Long getCourseIdById(Long lectureId) {
        return null;
    }

    @Override
    public HashSet<String> getChannelSetByCourseId(Long courseId) {
        return null;
    }

    @Override
    public List<Integer> getIndexListByCourseId(Long courseId) {
        return null;
    }

    @Override
    public List<String> getChannelListOrderByCount(Long member_id) {
        return null;
    }
}
