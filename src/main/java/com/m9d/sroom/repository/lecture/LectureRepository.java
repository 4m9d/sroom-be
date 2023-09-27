package com.m9d.sroom.repository.lecture;

import com.m9d.sroom.global.mapper.Lecture;

import java.util.HashSet;
import java.util.List;

public interface LectureRepository {

    Lecture save(Lecture lecture);

    Lecture getById(Long lectureId);

    Lecture updateById(Long lectureId, Lecture lecture);

    void deleteByCourseId(Long courseId);

    HashSet<String> getChannelSetByCourseId(Long courseId);

    List<Lecture> getListByCourseId(Long courseId);

    List<String> getChannelListOrderByCount(Long member_id);



}
