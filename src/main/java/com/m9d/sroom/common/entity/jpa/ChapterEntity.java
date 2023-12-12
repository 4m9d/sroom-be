package com.m9d.sroom.common.entity.jpa;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "CHAPTER")
public class ChapterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long chapterId;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private VideoEntity video;

    @Column(nullable = false)
    private int startTime;

    @Column(nullable = false)
    private int duration;

    @OneToOne
    @JoinColumn(name = "summary_id", nullable = false)
    private SummaryEntity summary;
}
