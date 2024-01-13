package com.m9d.sroom.material;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.material.dto.response.*;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.material.model.MaterialType;
import com.m9d.sroom.quiz.QuizMapper;
import com.m9d.sroom.summary.SummaryMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaterialMapper {

    public static FeedbackInfo getInfoByEntity(MaterialFeedbackEntity feedbackEntity) {
        return FeedbackInfo.builder()
                .type(MaterialType.from(feedbackEntity.getContentType()).toStr())
                .available(false)
                .hasFeedback(true)
                .isSatisfactory(feedbackEntity.getRating())
                .build();
    }

    public static Material getMaterial(CourseVideoEntity courseVideoEntity) {
        Optional<MaterialFeedbackEntity> summaryFeedbackEntityOptional = courseVideoEntity.getMember()
                .findFeedbackByMaterialIdAndType(MaterialType.SUMMARY, courseVideoEntity.getSummary().getSummaryId());

        FeedbackInfo feedbackInfo;
        if (summaryFeedbackEntityOptional.isEmpty()) {
            feedbackInfo = FeedbackInfo.getNoSubmittedInfo(MaterialType.SUMMARY);
        } else {
            feedbackInfo = getInfoByEntity(summaryFeedbackEntityOptional.get());
        }

        List<QuizResponse> quizResponseList = new ArrayList<>();
        for (QuizEntity quizEntity : courseVideoEntity.getVideo().getQuizzes()) {
            if (courseVideoEntity.getCourseQuizzes().isEmpty()) {
                quizResponseList.add(QuizMapper.getResponse(quizEntity, courseVideoEntity.getMember()));
            } else {
                CourseQuizEntity courseQuizEntity = courseVideoEntity.getCourseQuizzes().stream()
                        .filter(cq -> cq.getQuiz() == quizEntity)
                        .findFirst().get();
                quizResponseList.add(QuizMapper.getSubmittedResponse(courseQuizEntity));
            }
        }

        return Material.builder()
                .status(MaterialStatus.CREATED.getValue())
                .summaryBrief(SummaryMapper.getBrief(courseVideoEntity.getSummary(), feedbackInfo))
                .quizzes(quizResponseList)
                .totalQuizCount(quizResponseList.size())
                .build();
    }

    public static Material4PdfResponse getCourseMaterials(CourseEntity courseEntity) {
        List<Content4PdfResponse> contentList = new ArrayList<>();
        List<Answer4PdfResponse> answerList = new ArrayList<>();

        for (CourseVideoEntity courseVideoEntity : courseEntity.getCourseVideos()) {
            List<Quiz4PdfResponse> quizList = new ArrayList<>();
            int quizIndex = 1;
            SummaryBrief summaryBrief = null;

            if (courseVideoEntity.getMaterialStatus().equals(MaterialStatus.CREATED)) {
                summaryBrief = SummaryMapper.getBrief(courseVideoEntity.getSummary(),
                        FeedbackInfo.getNoSubmittedInfo(MaterialType.SUMMARY));

                Answer4PdfResponse answer4PdfResponse = Answer4PdfResponse.getDefault(
                        courseVideoEntity.getSequence().getVideoIndex(),
                        courseVideoEntity.getVideo().getContentInfo().getTitle());

                for (QuizEntity quizEntity : courseVideoEntity.getVideo().getQuizzes()) {
                    quizList.add(Quiz4PdfResponse.create(quizEntity, quizIndex));
                    answer4PdfResponse.getVideoAnswers().add(new VideoAnswer4PdfResponse(quizIndex,
                            String.valueOf(quizEntity.getChoiceAnswer()),
                            quizEntity.getOptionsStr().get(quizEntity.getChoiceAnswer() - 1)));
                    quizIndex++;
                }
                answerList.add(answer4PdfResponse);
            }
            contentList.add(new Content4PdfResponse(courseVideoEntity.getVideo().getContentInfo().getTitle(),
                    courseVideoEntity.getSequence().getVideoIndex(),
                    courseVideoEntity.getVideo().getContentInfo().getTitle(),
                    courseVideoEntity.getMaterialStatus().equals(MaterialStatus.CREATED), summaryBrief, quizList));
        }

        return Material4PdfResponse.createPrepared(courseEntity.getCourseTitle(), courseEntity.getCourseVideos().size(),
                contentList, answerList);
    }
}
