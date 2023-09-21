package com.m9d.sroom.repository.lecture;

import com.m9d.sroom.global.mapper.Lecture;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Repository
public class LectureJdbcRepositoryImpl implements LectureRepository{
    @Override
    public Lecture save(Lecture lecture) {
        return null;
    }

    @Override
    public void deleteByCourseId(Long courseId) {

    }

    @Override
    public HashSet<String> getChannelSetByCourseId(Long courseId) {
        return null;
    }

    @Override
    public List<Lecture> getListByCourseId(Long courseId) {
        return null;
    }

    @Override
    public List<String> getChannelListOrderByCount(Long member_id) {
        return null;
    }
}
