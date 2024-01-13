package com.m9d.sroom.quiz;

import com.m9d.sroom.common.entity.jpa.CourseQuizEntity;
import com.m9d.sroom.common.entity.jpa.MaterialFeedbackEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.QuizEntity;
import com.m9d.sroom.material.MaterialMapper;
import com.m9d.sroom.material.dto.response.FeedbackInfo;
import com.m9d.sroom.material.dto.response.QuizResponse;
import com.m9d.sroom.material.model.MaterialType;

import java.text.SimpleDateFormat;
import java.util.Optional;

public class QuizMapper {

    public static QuizResponse getSubmittedResponse(CourseQuizEntity courseQuizEntity) {
        Optional<MaterialFeedbackEntity> feedbackEntityOptional = courseQuizEntity.getMember()
                .findFeedbackByMaterialIdAndType(MaterialType.QUIZ, courseQuizEntity.getQuiz().getQuizId());

        FeedbackInfo feedbackInfo;
        if (feedbackEntityOptional.isEmpty()) {
            feedbackInfo = FeedbackInfo.getNoSubmittedInfo(MaterialType.QUIZ);
        } else {
            feedbackInfo = MaterialMapper.getInfoByEntity(feedbackEntityOptional.get());
        }

        return QuizResponse.builder()
                .id(courseQuizEntity.getQuiz().getQuizId())
                .type(courseQuizEntity.getQuiz().getType())
                .question(courseQuizEntity.getQuiz().getQuestion())
                .options(courseQuizEntity.getQuiz().getOptionsStr())
                .submitted(true)
                .answer(String.valueOf(courseQuizEntity.getQuiz().getChoiceAnswer()))
                .submittedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(courseQuizEntity.getGrading()
                        .getSubmittedTime()))
                .submittedAnswer(courseQuizEntity.getGrading().getSubmittedAnswer())
                .correct(courseQuizEntity.getGrading().getIsCorrect())
                .scrapped(courseQuizEntity.getIsScrapped())
                .feedbackInfo(feedbackInfo)
                .build();
    }

    public static QuizResponse getResponse(QuizEntity quizEntity, MemberEntity member) {
        Optional<MaterialFeedbackEntity> feedbackEntityOptional =
                member.findFeedbackByMaterialIdAndType(MaterialType.QUIZ, quizEntity.getQuizId());

        FeedbackInfo feedbackInfo;
        if (feedbackEntityOptional.isEmpty()) {
            feedbackInfo = FeedbackInfo.getNoSubmittedInfo(MaterialType.QUIZ);
        } else {
            feedbackInfo = MaterialMapper.getInfoByEntity(feedbackEntityOptional.get());
        }

        return QuizResponse.builder()
                .id(quizEntity.getQuizId())
                .type(quizEntity.getType())
                .question(quizEntity.getQuestion())
                .options(quizEntity.getOptionsStr())
                .submitted(false)
                .answer(String.valueOf(quizEntity.getChoiceAnswer()))
                .submittedAt(null)
                .submittedAnswer(null)
                .correct(false)
                .scrapped(false)
                .feedbackInfo(feedbackInfo)
                .build();
    }
}
