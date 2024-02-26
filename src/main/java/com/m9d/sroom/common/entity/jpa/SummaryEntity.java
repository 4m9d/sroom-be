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

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    @Column(columnDefinition = "text")
    private String content;

    @UpdateTimestamp
    private Timestamp updatedTime;

    private boolean isModified;

    @Embedded
    private Feedback feedBack;

    private SummaryEntity(VideoEntity video, String content) {
        setVideo(video);
        this.content = content;
        this.updatedTime = new Timestamp(System.currentTimeMillis());
        this.feedBack = new Feedback(0, 0);
    }

    private void setVideo(VideoEntity video) {
        if (video.getSummary() == null) {
            video.setSummary(this);
            this.isModified = false;
        } else {
            this.isModified = true;
        }
        this.video = video;
    }

    public static SummaryEntity create(VideoEntity video, String content) {
        return new SummaryEntity(video, content);
    }

    public void feedback(boolean isSatisfactory) {
        if (isSatisfactory) {
            this.feedBack.setPositiveFeedbackCount(feedBack.getPositiveFeedbackCount() + 1);
        } else {
            this.feedBack.setNegativeFeedbackCount(feedBack.getNegativeFeedbackCount() + 1);
        }
    }

    public void update(String content) {
        this.content = content;
        this.updatedTime = new Timestamp(System.currentTimeMillis());
    }
}
