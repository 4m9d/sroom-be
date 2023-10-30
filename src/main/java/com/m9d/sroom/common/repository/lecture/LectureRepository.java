package com.m9d.sroom.common.repository.lecture;

import com.m9d.sroom.common.entity.LectureEntity;

import java.util.HashSet;
import java.util.List;

public interface LectureRepository {

    LectureEntity save(LectureEntity lecture);

    LectureEntity getById(Long lectureId);

    LectureEntity updateById(Long lectureId, LectureEntity lecture);

    void deleteByCourseId(Long courseId);

    HashSet<String> getChannelSetByCourseId(Long courseId);

    List<LectureEntity> getListByCourseId(Long courseId);

    List<String> getChannelListOrderByCount(Long member_id);
}
