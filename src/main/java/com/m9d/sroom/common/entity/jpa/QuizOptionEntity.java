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
}
