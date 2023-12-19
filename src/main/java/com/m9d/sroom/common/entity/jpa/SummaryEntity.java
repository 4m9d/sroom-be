package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.Feedback;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "SUMMARY")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryId;

    @OneToOne(mappedBy = "summary")
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    @Column(columnDefinition = "text")
    private String content;

    @UpdateTimestamp
    private Timestamp updatedTime;

    private boolean isModified;

    @Embedded
    private Feedback feedBack;
}
