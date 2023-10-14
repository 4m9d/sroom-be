package com.m9d.sroom.repository.lecture;

import com.m9d.sroom.global.mapper.LectureDto;

import java.util.HashSet;
import java.util.List;

public interface LectureRepository {

    LectureDto save(LectureDto lectureDto);

    LectureDto getById(Long lectureId);

    LectureDto updateById(Long lectureId, LectureDto lectureDto);

    void deleteByCourseId(Long courseId);

    HashSet<String> getChannelSetByCourseId(Long courseId);

    List<LectureDto> getListByCourseId(Long courseId);

    List<String> getChannelListOrderByCount(Long member_id);
}
