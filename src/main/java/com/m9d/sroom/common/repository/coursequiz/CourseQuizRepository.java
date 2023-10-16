package com.m9d.sroom.common.repository.coursequiz;


import com.m9d.sroom.common.dto.CourseQuiz;

import java.util.Optional;

public interface CourseQuizRepository {

    CourseQuiz save(CourseQuiz courseQuiz);

    CourseQuiz getById(Long courseQuizId);

    Optional<CourseQuiz> findById(Long courseQuizId);

    CourseQuiz updateById(Long courseQuizId, CourseQuiz courseQuiz);

    Optional<CourseQuiz> findByQuizIdAndCourseVideoId(Long quizId, Long courseVideoId);

    void deleteByCourseId(Long courseId);
}
