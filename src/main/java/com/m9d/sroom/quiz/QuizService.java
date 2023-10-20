package com.m9d.sroom.quiz;

import com.m9d.sroom.common.entity.QuizEntity;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.common.repository.quiz.QuizRepository;
import com.m9d.sroom.common.repository.quizoption.QuizOptionRepository;
import com.m9d.sroom.material.dto.response.QuizRes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseQuizRepository courseQuizRepository;
    private final QuizOptionRepository quizOptionRepository;

    public QuizService(QuizRepository quizRepository, CourseQuizRepository courseQuizRepository, QuizOptionRepository quizOptionRepository) {
        this.quizRepository = quizRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.quizOptionRepository = quizOptionRepository;
    }

    public List<QuizRes> getQuizResList(Long videoId, Long courseVideoId) {
        List<QuizRes> quizResList = new ArrayList<>();
        for (QuizEntity quizEntity : quizRepository.getListByVideoId(videoId)) {
            Quiz quiz = new Quiz(quizEntity, courseQuizRepository
                    .findByQuizIdAndCourseVideoId(quizEntity.getId(), courseVideoId),
                    quizOptionRepository.getListByQuizId(quizEntity.getId()));
            quizResList.add(new QuizRes(quizEntity.getId(), quiz));
        }
        return quizResList;
    }
}
