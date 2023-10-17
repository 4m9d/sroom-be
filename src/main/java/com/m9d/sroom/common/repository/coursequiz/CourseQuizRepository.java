package com.m9d.sroom.common.repository.coursequiz;


import com.m9d.sroom.common.entity.CourseQuizEntity;

import java.util.Optional;

public interface CourseQuizRepository {

    CourseQuizEntity save(CourseQuizEntity courseQuiz);

    CourseQuizEntity getById(Long courseQuizId);

    Optional<CourseQuizEntity> findById(Long courseQuizId);

    CourseQuizEntity updateById(Long courseQuizId, CourseQuizEntity courseQuiz);

    Optional<CourseQuizEntity> findByQuizIdAndCourseVideoId(Long quizId, Long courseVideoId);

    void deleteByCourseId(Long courseId);
}
