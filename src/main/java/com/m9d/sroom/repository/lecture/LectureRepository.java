package com.m9d.sroom.repository.lecture;

import com.m9d.sroom.global.model.Lecture;

import java.util.HashSet;
import java.util.List;

public interface LectureRepository {

    Long save(Lecture lecture);

    void deleteByCourseId(Long courseId);

    Long getCourseIdById(Long lectureId);

    HashSet<String> getChannelSetByCourseId(Long courseId);

    List<Integer> getIndexListByCourseId(Long courseId);

    List<String> getChannelListOrderByCount(Long member_id);



}
