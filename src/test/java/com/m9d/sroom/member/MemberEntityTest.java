package com.m9d.sroom.member;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.material.model.MaterialType;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.TestConstant;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

public class MemberEntityTest extends RepositoryTest {

    @Test
    @DisplayName("해당 멤버의 모든 로그정보를 불러옵니다.")
    void getLogs() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);

        //when
        courseDailyLogRepository.save(CourseDailyLogEntity.create(course, TestConstant.LOG_LEARNING_TIME,
                TestConstant.LOG_QUIZ_COUNT, TestConstant.LOG_LECTURE_COUNT));

        //then
        Assertions.assertEquals(member.getLogList().size(), 1);
        Assertions.assertEquals(member.getLogList().get(0).getCourse(), course);
    }

    @Test
    @DisplayName("해당 멤버의 모든 코스를 최신순으로 불러옵니다.")
    void getCourses() {
        //given
        MemberEntity member = getMemberEntity();

        //when
        CourseEntity course1 = courseRepository.save(CourseEntity.createWithoutSchedule(
                member, TestConstant.COURSE_TITLE, TestConstant.THUMBNAIL));
        CourseEntity course2 = courseRepository.save(CourseEntity.createWithSchedule(member, TestConstant.COURSE_TITLE, TestConstant.THUMBNAIL,
                true, 2, new Date(), 30));

        System.out.println(course1.getLastViewTime());
        System.out.println(course2.getLastViewTime());

        //then
        Assertions.assertEquals(member.getCoursesByLatestOrder().size(), 2);
        Assertions.assertEquals(member.getCoursesByLatestOrder().get(0), course2);
    }

    @Test
    @DisplayName("해당 멤버가 남긴 피드백을 조회합니다.")
    void getFeedbackByMember() {
        //given
        MemberEntity member = getMemberEntity();

        //when
        MaterialFeedbackEntity feedbackEntity = feedbackRepository.save(MaterialFeedbackEntity.create(member,
                1L, MaterialType.QUIZ.getValue(), false));

        //then
        Assertions.assertEquals(member.getFeedbacks().size(), 1);
        Assertions.assertEquals(member.getFeedbacks().get(0), feedbackEntity);
    }

    @Test
    @DisplayName("많이 등록된 채널 순서대로 리스트를 불러옵니다.")
    void getChannelListOrderByCount() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);
        String channelMany = "다수의 채널";
        String channelFew = "소수의 채널";

        //when
        lectureRepository.save(LectureEntity.create(course, 1L, false, 1, channelFew));
        lectureRepository.save(LectureEntity.create(course, 1L, false, 1, channelMany));
        lectureRepository.save(LectureEntity.create(course, 1L, false, 1, channelMany));

        //then
        List<String> channelList = member.getChannelListOrderByCount();
        Assertions.assertEquals(channelList.size(), 2);
        Assertions.assertEquals(channelList.get(0), channelMany);
    }

    @Test
    @DisplayName("틀린 문제 리스트를 리턴합니다.")
    void getWrongQuizzes() {
        //given
        MemberEntity member = getMemberEntity();
        VideoEntity video = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        CourseVideoEntity courseVideo = getCourseVideoEntity(video);
        QuizEntity quiz1 = quizRepository.save(
                QuizEntity.createChoiceType(video, "넌 이름이 뭐니?", 3));
        QuizEntity quiz2 = quizRepository.save(
                QuizEntity.createChoiceType(video, "한국의 수도는?", 2));

        //when
        CourseQuizEntity courseQuiz1 = courseQuizRepository.save(CourseQuizEntity.create(courseVideo, quiz1,
                "알아서뭐함?", false));
        CourseQuizEntity courseQuiz2 = courseQuizRepository.save(CourseQuizEntity.create(courseVideo, quiz2,
                "서울이자네~", true));

        //then
        Assertions.assertEquals(1, member.getWrongQuizList(3).size());
        Assertions.assertTrue(member.getWrongQuizList(3).contains(courseQuiz1));
        Assertions.assertFalse(member.getWrongQuizList(3).contains(courseQuiz2));
    }
}
