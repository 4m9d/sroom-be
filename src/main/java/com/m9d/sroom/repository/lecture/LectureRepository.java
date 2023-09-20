package com.m9d.sroom.repository.lecture;

import com.m9d.sroom.global.model.Lecture;

import java.util.HashSet;
import java.util.List;

public interface LectureRepository {

    Long save(Lecture lecture); // 쿼리문 또 써서 객체를 받아오는 것이 좋다. 이 메서드 안에서 get~ 을 호출해도 된다. 밖에서 안쓰면 priavate

    void deleteByCourseId(Long courseId);

    HashSet<String> getChannelSetByCourseId(Long courseId);

    List<Lecture> getListByCourseId(Long courseId);

    List<String> getChannelListOrderByCount(Long member_id);



}
