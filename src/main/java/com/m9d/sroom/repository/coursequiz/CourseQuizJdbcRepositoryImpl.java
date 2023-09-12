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
    public Optional<SubmittedQuizInfo> getSubmittedQuizInfoByQuizIdAndCourseVideoId(Long quizId, Long coursevideoId) {
        return Optional.empty();
    }

    @Override
    public void deleteByCourseId(Long courseId) {

    }

    @Override
    public Optional<CourseQuizInfo> getInfoById(Long courseQuizId) {
        return Optional.empty();
    }

    @Override
    public void updateScrappedById(Long courseQuizId) {

    }

    @Override
    public Boolean isScrappedById(Long courseQuizId) {
        return null;
    }
}
