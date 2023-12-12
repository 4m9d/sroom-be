package com.m9d.sroom.common.entity.jpa;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "SUMMARY")
public class SummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long summaryId;

    @OneToOne
    @JoinColumn(name = "video_id", nullable = false)
    private VideoEntity video;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @CreationTimestamp
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean modified;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer positiveFeedbackCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer negativeFeedbackCount;
}
