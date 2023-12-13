package com.m9d.sroom.common.entity.jpa;

import javax.persistence.*;

@Entity
@Table(name = "QUIZ_OPTION")
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
}
