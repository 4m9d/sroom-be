package com.m9d.sroom.courseQuiz;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CourseQuizRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("채점 결과를 저장하는데 성공합니다.")
    void saveCourseQuiz() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);
        VideoEntity video = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        CourseVideoEntity courseVideo = getCourseVideoEntity(video);
        QuizEntity quiz = quizRepository.save(
                QuizEntity.createChoiceType(video, "넌 이름이 뭐니?", 3));

        //when
        CourseQuizEntity courseQuiz = courseQuizRepository.save(CourseQuizEntity.create(courseVideo, quiz,
                "알아서뭐함?", false));

        //then
        Assertions.assertNotNull(courseQuiz.getCourseQuizId());
        Assertions.assertEquals(courseQuiz.getCourse(), course);
        Assertions.assertEquals(courseQuiz.getMember(), member);
        Assertions.assertEquals(courseQuiz.getCourseVideo(), courseVideo);
        Assertions.assertEquals(courseQuiz.getGrading().getIsCorrect(), false);
        Assertions.assertEquals(courseQuiz.getIsScrapped(), false);
    }

    @Test
    @DisplayName("채점 정보를 삭제합니다.")
    void deleteCourseQuiz() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);
        VideoEntity video = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        CourseVideoEntity courseVideo = getCourseVideoEntity(video);
        QuizEntity quiz = quizRepository.save(
                QuizEntity.createChoiceType(video, "넌 이름이 뭐니?", 3));
        CourseQuizEntity courseQuiz = courseQuizRepository.save(CourseQuizEntity.create(courseVideo, quiz,
                "알아서뭐함?", false));

        //when
        courseQuizRepository.deleteByCourseId(course.getCourseId());

        //then
        Assertions.assertEquals(courseQuizRepository.findListByCourseId(course.getCourseId()).size(), 0);
    }
}
