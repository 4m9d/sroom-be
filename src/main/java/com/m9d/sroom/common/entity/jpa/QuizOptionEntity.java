package com.m9d.sroom.common.entity.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "QUIZ_OPTION")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class QuizOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizOptionId;


    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private QuizEntity quiz;

    @Column(columnDefinition = "text")
    private String optionText;

    private int optionIndex;

    private QuizOptionEntity(QuizEntity quiz, String optionText, int optionIndex) {
        setQuiz(quiz);
        this.optionIndex = optionIndex;
        this.optionText = optionText;
    }

    private void setQuiz(QuizEntity quiz) {
        if (this.quiz != null) {
            this.quiz.getQuizOptions().remove(this);
        }

        quiz.getQuizOptions().add(this);
        this.quiz = quiz;
    }

    public static QuizOptionEntity create(QuizEntity quiz, String optionText, int optionIndex) {
        return new QuizOptionEntity(quiz, optionText, optionIndex);
    }
}
