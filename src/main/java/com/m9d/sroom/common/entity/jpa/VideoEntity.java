package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.ContentInfo;
import com.m9d.sroom.common.entity.jpa.embedded.Review;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.video.vo.Video;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "VIDEO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @Builder
    private VideoEntity(String videoCode, ContentInfo contentInfo, Long viewCount, String language, String license,
                        Timestamp updatedAt, Boolean membership, Review review, SummaryEntity summary,
                        Integer materialStatus, Boolean chapterUsage) {
        this.videoCode = videoCode;
        this.contentInfo = contentInfo;
        this.viewCount = viewCount;
        this.language = language;
        this.license = license;
        this.updatedAt = updatedAt;
        this.membership = membership;
        this.review = review;
        this.summary = summary;
        this.materialStatus = materialStatus;
        this.chapterUsage = chapterUsage;
    }

    public static VideoEntity create(Video video) {
        return VideoEntity.builder()
                .videoCode(video.getCode())
                .contentInfo(new ContentInfo(video.getTitle(), video.getChannel(), video.getDescription(),
                        video.getThumbnail(), true, video.getDuration(), video.getPublishedAt()))
                .viewCount(video.getViewCount())
                .language(video.getLanguage())
                .license(video.getLicense())
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .membership(video.getMembership())
                .review(new Review(0, 0, 0.0))
                .summary(null)
                .materialStatus(MaterialStatus.NO_REQUEST.getValue())
                .chapterUsage(false)
                .build();
    }
}
