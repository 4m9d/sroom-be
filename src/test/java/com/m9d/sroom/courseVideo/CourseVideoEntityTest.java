package com.m9d.sroom.courseVideo;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CourseVideoEntityTest extends RepositoryTest {

    @Test
    @DisplayName("퀴즈 id 를 받아 해당 채점 정보를 성공적으로 반환합니다.")
    void getCourseQuiz() {
        //given
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
        Assertions.assertTrue(courseVideo.findCourseQuizByQuizId(quiz1.getQuizId()).isPresent());
        Assertions.assertEquals(courseVideo.findCourseQuizByQuizId(quiz1.getQuizId()).get(), courseQuiz1);
        Assertions.assertEquals(courseVideo.findCourseQuizByQuizId(quiz2.getQuizId()).get(), courseQuiz2);
    }
}
