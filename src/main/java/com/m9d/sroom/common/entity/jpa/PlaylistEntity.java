package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.ContentInfo;
import com.m9d.sroom.common.entity.jpa.embedded.Review;
import com.m9d.sroom.recommendation.RecommendationScheduler;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="PLAYLIST")
public class PlaylistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long playlistId;

    private String playlistCode;

    @Embedded
    private ContentInfo contentInfo;

    private Integer videoCount;

    @Embedded
    private Review review;

    @CreationTimestamp
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "playlist")
    private List<PlaylistVideoEntity> playlistVideoEntityList = new ArrayList<PlaylistVideoEntity>();
}
