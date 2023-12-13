package com.m9d.sroom.common.entity.jpa;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "CHAPTER")
public class ChapterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chapterId;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    private int startTime;

    private int duration;

    @OneToOne
    @JoinColumn(name = "summary_id")
    private SummaryEntity summary;
}
