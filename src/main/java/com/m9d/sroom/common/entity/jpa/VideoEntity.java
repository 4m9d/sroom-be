package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.ContentInfo;
import com.m9d.sroom.common.entity.jpa.embedded.Review;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VIDEO")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;

    private String videoCode;

    @Embedded
    private ContentInfo contentInfo;

    private Long viewCount;

    private String language;

    private String license;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private Boolean membership;

    @Embedded
    private Review review;

    @OneToOne
    @JoinColumn(name = "summary_id")
    private SummaryEntity summary;

    private Integer materialStatus;

    private Boolean chapterUsage;

    @OneToMany(mappedBy = "video_id")
    private List<QuizEntity> quizzes = new ArrayList<QuizEntity>();
}
