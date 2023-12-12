package com.m9d.sroom.common.entity.jpa;

import javax.persistence.*;

@Entity
@Table(name = "QUIZ_OPTION")
public class QuizOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long quizOptionId;


    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizEntity quiz;


    @Column(nullable = false)
    private String optionText;

    @Column(nullable = false)
    private int optionIndex;
}
