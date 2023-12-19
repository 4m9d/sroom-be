package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.Feedback;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "quiz_id")
    private List<QuizOptionEntity> quizOptions = new ArrayList<QuizOptionEntity>();
}
