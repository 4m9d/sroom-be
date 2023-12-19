package com.m9d.sroom.lecture;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.LectureEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.VideoEntity;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LectureRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("강의 저장에 성공합니다.")
    void saveLecture() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);
        VideoEntity video = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);

        //when
        LectureEntity lecture = LectureEntity.create(course, video.getVideoId(), false, 1,
                video.getContentInfo().getChannel());
        lectureRepository.save(lecture);

        //then
        Assertions.assertNotNull(lecture.getLectureId());
        Assertions.assertEquals(lecture.getCourse(), course);
        Assertions.assertEquals(lecture.getMember(), member);
        Assertions.assertEquals(member.getLectures().get(0), lecture);
        Assertions.assertEquals(course.getLectures().get(0), lecture);
    }

    @Test
    @DisplayName("강의 삭제에 성공합니다.")
    void deleteLecture() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);
        VideoEntity video = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        LectureEntity lecture = LectureEntity.create(course, video.getVideoId(), false, 1,
                video.getContentInfo().getChannel());
        lectureRepository.save(lecture);

        //when
        course.getLectures().remove(lecture);
        member.getLectures().remove(lecture);
        lectureRepository.deleteByCourseId(course.getCourseId());

        //then
        Assertions.assertNull(lectureRepository.getById(1L));
    }
}
