package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.video.vo.Video;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "QUIZ")
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long quizId;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private VideoEntity video;

    @Column(nullable = false)
    private int type;

    @Column(nullable = false)
    private String question;

    @Column(nullable = true)
    private String subjectiveAnswer;

    @Column(nullable = true)
    private Integer choiceAnswer;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer positiveFeedbackCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer negativeFeedbackCount;
}
