package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.ContentInfo;
import com.m9d.sroom.common.entity.jpa.embedded.Review;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.video.vo.Video;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VIDEO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
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

    @OneToMany(mappedBy = "video")
    private List<QuizEntity> quizzes = new ArrayList<QuizEntity>();

    private VideoEntity(String videoCode, ContentInfo contentInfo, Long viewCount, String language, String license,
                        Boolean membership) {
        this.videoCode = videoCode;
        this.contentInfo = contentInfo;
        this.viewCount = viewCount;
        this.language = language;
        this.license = license;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.membership = membership;
        this.review = new Review(0, 0, 0.0);
        this.materialStatus = MaterialStatus.NO_REQUEST.getValue();
        this.chapterUsage = false;
    }

    public static VideoEntity create(Video video) {
        return new VideoEntity(video.getCode(), new ContentInfo(video.getTitle(), video.getChannel(),
                video.getDescription(), video.getThumbnail(), true, video.getDuration(),
                video.getPublishedAt()), video.getViewCount(), video.getLanguage(), video.getLicense(),
                video.getMembership());
    }

    public void setSummary(SummaryEntity summary) {
        this.summary = summary;
    }
}
