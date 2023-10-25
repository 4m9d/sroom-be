package com.m9d.sroom.repository.coursequiz;


import com.m9d.sroom.global.mapper.CourseQuiz;

import java.util.List;
import java.util.Optional;

public interface CourseQuizRepository {

    CourseQuiz save(CourseQuiz courseQuiz);

    CourseQuiz getById(Long courseQuizId);

    List<CourseQuiz> getWrongQuizListByMemberId(Long memberId, int limit);

    Optional<CourseQuiz> findById(Long courseQuizId);

    CourseQuiz updateById(Long courseQuizId, CourseQuiz courseQuiz);

    Optional<CourseQuiz> findByQuizIdAndCourseVideoId(Long quizId, Long courseVideoId);

    void deleteByCourseId(Long courseId);
}
