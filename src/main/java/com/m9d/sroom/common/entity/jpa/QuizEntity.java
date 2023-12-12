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
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    private int type;

    private String question;

    private String subjectiveAnswer;

    private Integer choiceAnswer;

    private Integer positiveFeedbackCount;

     private Integer negativeFeedbackCount;
}
