package com.m9d.sroom.repository.coursequiz;


import com.m9d.sroom.global.mapper.CourseQuiz;

public interface CourseQuizRepository {

    Long save(CourseQuiz courseQuiz);

    void deleteByCourseId(Long courseId);

    void updateScrappedById(Long courseQuizId);

    Boolean isScrappedById(Long courseQuizId);
}
