package com.m9d.sroom.common.entity.jpa;

import javax.persistence.*;

@Entity
@Table(name = "QUIZ_OPTION")
public class QuizOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long quizOptionId;


    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private QuizEntity quiz;

    private String optionText;

    private int optionIndex;
}
