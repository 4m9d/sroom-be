package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.Feedback;
import com.m9d.sroom.quiz.QuizType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "QUIZ")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    private int type;

    @Column(columnDefinition = "text")
    private String question;

    @Column(columnDefinition = "text")
    private String subjectiveAnswer;

    private Integer choiceAnswer;

    @Embedded
    private Feedback feedback;

    @OneToMany(mappedBy = "quiz")
    private List<QuizOptionEntity> quizOptions = new ArrayList<QuizOptionEntity>();

    private QuizEntity(VideoEntity video, int quizType, String question, String subjectiveAnswer,
                       Integer choiceAnswer) {
        setVideo(video);
        this.type = quizType;
        this.question = question;
        this.subjectiveAnswer = subjectiveAnswer;
        this.choiceAnswer = choiceAnswer;
        this.feedback = new Feedback(0, 0);
    }

    private void setVideo(VideoEntity video) {
        if (this.video != null) {
            this.video.getQuizzes().remove(this);
        }

        this.video = video;
        video.getQuizzes().add(this);
    }

    public static QuizEntity createChoiceType(VideoEntity video, String question, int choiceAnswer) {
        return new QuizEntity(video, QuizType.MULTIPLE_CHOICE.getValue(), question, null, choiceAnswer);
    }

    public void feedback(boolean isSatisfactory) {
        if (isSatisfactory) {
            this.feedback.setPositiveFeedbackCount(feedback.getPositiveFeedbackCount() + 1);
        } else {
            this.feedback.setNegativeFeedbackCount(feedback.getNegativeFeedbackCount() + 1);
        }
    }

    public List<String> getOptionsStr(){
        return quizOptions.stream()
                .map(QuizOptionEntity::getOptionText)
                .collect(Collectors.toList());
    }
}
