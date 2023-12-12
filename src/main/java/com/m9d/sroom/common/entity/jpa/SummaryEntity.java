package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.Feedback;
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

    @OneToOne(mappedBy = "video")
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    private String content;

    @CreationTimestamp
    @UpdateTimestamp
    private Timestamp updatedAt;

    private boolean modified;

    @Embedded
    private Feedback feedBack;
}
