package com.m9d.sroom.repository.coursequiz;

import com.m9d.sroom.global.mapper.CourseQuiz;
import org.springframework.stereotype.Repository;

@Repository
public class CourseQuizJdbcRepositoryImpl implements CourseQuizRepository {
    @Override
    public Long save(CourseQuiz courseQuiz) {
        return null;
    }

    @Override
    public void deleteByCourseId(Long courseId) {

    }

    @Override
    public void updateScrappedById(Long courseQuizId) {

    }

    @Override
    public Boolean isScrappedById(Long courseQuizId) {
        return null;
    }
}
