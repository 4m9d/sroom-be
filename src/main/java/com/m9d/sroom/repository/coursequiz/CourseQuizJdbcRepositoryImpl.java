package com.m9d.sroom.repository.coursequiz;

import com.m9d.sroom.global.model.CourseQuiz;
import com.m9d.sroom.material.model.CourseQuizInfo;
import com.m9d.sroom.material.model.SubmittedQuizInfo;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
