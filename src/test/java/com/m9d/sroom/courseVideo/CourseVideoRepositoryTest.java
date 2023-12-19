package com.m9d.sroom.courseVideo;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CourseVideoRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("CourseVideo 엔티티 저장 성공합니다.")
    void saveCourseVideo() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);
        VideoEntity video = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        LectureEntity lecture = lectureRepository.save(LectureEntity.create(member, course, video.getVideoId(), false, 1,
                video.getContentInfo().getChannel()));
        CourseVideoEntity courseVideo = CourseVideoEntity.createWithoutSummary(member, course, video, lecture,
                1, 1);

        //when
        courseVideoRepository.save(courseVideo);

        //then
        Assertions.assertNotNull(courseVideo.getCourseVideoId());
        Assertions.assertEquals(member.getCourseVideos().get(0), courseVideo);
        Assertions.assertEquals(course.getCourseVideos().get(0), courseVideo);
        Assertions.assertEquals(course.getCourseVideos().size(), 1);
        Assertions.assertEquals(lecture.getCourseVideos().size(), 1);
        Assertions.assertEquals(lecture.getCourseVideos().get(0), courseVideo);
    }
}
