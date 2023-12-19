package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.TestConstant;
import com.m9d.sroom.util.constant.ContentConstant;
import com.m9d.sroom.video.vo.Video;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

public class CourseEntityTest extends RepositoryTest {

    @Test
    @DisplayName("해당 날자의 로그 데이터를 불러옵니다.")
    void getDailyLog() {
        Date today = new Date();
        MemberEntity member = getMemberEntity();
        //given
        CourseEntity course = getCourseEntity(member);

        //when
        CourseDailyLogEntity dailyLog = getCourseDailyLogEntity(member, course);

        //then
        Assertions.assertTrue(course.findDailyLogByDate(today).isPresent());
        Assertions.assertEquals(course.findDailyLogByDate(today).get().getLearningTime(),
                dailyLog.getLearningTime());
    }

    @Test
    @DisplayName("해당 인덱스의 수강영상을 불러옵니다.")
    void getCourseVideoByIndex() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(getMemberEntity());
        VideoEntity video1 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        VideoEntity video2 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[1]);
        LectureEntity lecture = getLectureEntity(video1.getVideoId());

        //when
        CourseVideoEntity courseVideo1 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video1, lecture, 1, 1));
        CourseVideoEntity courseVideo2 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video2, lecture, 2, 2));

        //then
        Assertions.assertEquals(course.getCourseVideoByIndex(2), courseVideo2);
        Assertions.assertEquals(course.getCourseVideoByIndex(1), courseVideo1);
    }

    @Test
    @DisplayName("이전 인덱스를 통해 수강영상을 불러옵니다.")
    void getCourseVideoByPrevIndex() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(getMemberEntity());
        VideoEntity video1 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        VideoEntity video2 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[1]);
        LectureEntity lecture = getLectureEntity(video1.getVideoId());

        //when
        CourseVideoEntity courseVideo1 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video1, lecture, 1, 1));
        CourseVideoEntity courseVideo2 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video2, lecture, 2, 2));

        //then
        Assertions.assertTrue(course.getCourseVideoByPrevIndex(1).isPresent());
        Assertions.assertEquals(course.getCourseVideoByPrevIndex(1).get(), courseVideo2);
    }

    @Test
    @DisplayName("인덱스 순서로 수강영상을 불러옵니다.")
    void getCourseVideoOrderByIndex() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(getMemberEntity());
        VideoEntity video1 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        VideoEntity video2 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[1]);
        LectureEntity lecture = getLectureEntity(video1.getVideoId());

        //when
        CourseVideoEntity courseVideo1 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video1, lecture, 1, 1));
        CourseVideoEntity courseVideo2 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video2, lecture, 2, 2));

        //then
        Assertions.assertEquals(course.getCourseVideoListOrderByIndex().size(), 2);
        Assertions.assertEquals(course.getCourseVideoListOrderByIndex().get(0), courseVideo1);
    }

    @Test
    @DisplayName("마지막 순서의 수강영상을 불러옵니다.")
    void getLastCourseVideo() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(getMemberEntity());
        VideoEntity video1 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        VideoEntity video2 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[1]);
        LectureEntity lecture = getLectureEntity(video1.getVideoId());

        //when
        CourseVideoEntity courseVideo1 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video1, lecture, 1, 1));
        CourseVideoEntity courseVideo2 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video2, lecture, 2, 2));

        //then
        Assertions.assertEquals(course.getLastCourseVideo(), courseVideo1);
    }

    @Test
    @DisplayName("주차별로 수강영상을 불러옵니다.")
    void getCourseVideoBySection() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(getMemberEntity());
        VideoEntity video1 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        VideoEntity video2 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[1]);
        LectureEntity lecture = getLectureEntity(video1.getVideoId());

        //when
        CourseVideoEntity courseVideo1 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video1, lecture, 1, 1));
        CourseVideoEntity courseVideo2 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video2, lecture, 2, 2));

        //then
        Assertions.assertEquals(course.getCourseVideoBySection(2).get(0), courseVideo2);
        Assertions.assertEquals(course.getCourseVideoBySection(1).size(), 1);
    }

    @Test
    @DisplayName("수강 완료된 수강영상을 불러옵니다.")
    void getCourseVideoCompleted() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(getMemberEntity());
        VideoEntity video1 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        VideoEntity video2 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[1]);
        LectureEntity lecture = getLectureEntity(video1.getVideoId());

        //when
        CourseVideoEntity courseVideo1 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video1, lecture, 1, 1));
        CourseVideoEntity courseVideo2 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video2, lecture, 2, 2));

        //then
        Assertions.assertEquals(course.countCompletedVideo(), 0);
    }

    @Test
    @DisplayName("수강정보를 적절히 불러옵니다.")
    void getWatchInfoByCourse() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(getMemberEntity());
        VideoEntity video1 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        VideoEntity video2 = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[1]);
        LectureEntity lecture = getLectureEntity(video1.getVideoId());

        //when
        CourseVideoEntity courseVideo1 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video1, lecture, 1, 1));
        CourseVideoEntity courseVideo2 = courseVideoRepository.save(CourseVideoEntity.createWithoutSummary(course,
                video2, lecture, 2, 2));

        //then
        Assertions.assertNotNull(course.getWatchInfoListBySection(2));
    }

    @Test
    @DisplayName("강의 채널셋을 불러옵니다.")
    void getChannelSet() {
        //given
        String channel1 = TestConstant.PLAYLIST_CHANNEL;
        String channel2 = "채널~";

        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(getMemberEntity());

        //when
        lectureRepository.save(LectureEntity.create(course, 1L, false, 1, channel1));
        lectureRepository.save(LectureEntity.create(course, 1L, false, 1, channel2));

        //then
        Set<String> channelSet = course.getLectureChannelSet();
        Assertions.assertTrue(channelSet.contains(channel1));
        Assertions.assertEquals(channelSet.size(), 2);
        Assertions.assertTrue(channelSet.contains(channel2));
    }
}
