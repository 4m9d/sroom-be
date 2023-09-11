package com.m9d.sroom.repository.coursequiz;


import com.m9d.sroom.global.model.CourseQuiz;
import com.m9d.sroom.material.model.CourseQuizInfo;
import com.m9d.sroom.material.model.SubmittedQuizInfo;

import java.util.Optional;

public interface CourseQuizRepository {

    Long save(CourseQuiz courseQuiz);

    Optional<SubmittedQuizInfo> getSubmittedQuizInfoByQuizIdAndCourseVideoId(Long quizId, Long coursevideoId);

    void deleteByCourseId(Long courseId);

    Optional<CourseQuizInfo> getInfoById(Long courseQuizId);

    void updateScrappedById(Long courseQuizId);

    Boolean isScrappedById(Long courseQuizId);


}
