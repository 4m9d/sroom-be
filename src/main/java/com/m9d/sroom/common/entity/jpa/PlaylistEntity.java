package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.ContentInfo;
import com.m9d.sroom.common.entity.jpa.embedded.Review;
import com.m9d.sroom.playlist.vo.Playlist;
import com.m9d.sroom.playlist.vo.PlaylistWithItemList;
import com.m9d.sroom.recommendation.RecommendationScheduler;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "PLAYLIST")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaylistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistId;

    private String playlistCode;

    @Embedded
    private ContentInfo contentInfo;

    private Integer videoCount;

    @Embedded
    private Review review;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "playlist")
    private List<PlaylistVideoEntity> playlistVideoEntityList = new ArrayList<PlaylistVideoEntity>();

    @Builder
    private PlaylistEntity(String playlistCode, ContentInfo contentInfo, Integer videoCount, Review review,
                           Timestamp updatedAt, List<PlaylistVideoEntity> playlistVideoEntityList) {
        this.playlistCode = playlistCode;
        this.contentInfo = contentInfo;
        this.videoCount = videoCount;
        this.review = review;
        this.updatedAt = updatedAt;
    }

    public static PlaylistEntity create(PlaylistWithItemList playlistWithItemList) {
        return PlaylistEntity.builder()
                .playlistCode(playlistWithItemList.getCode())
                .contentInfo(new ContentInfo(playlistWithItemList.getTitle(), playlistWithItemList.getChannel(),
                        playlistWithItemList.getDescription(), playlistWithItemList.getThumbnail(), true,
                        playlistWithItemList.getPlaylistDuration(), playlistWithItemList.getPublishedAt()))
                .videoCount(playlistWithItemList.getVideoCount())
                .review(new Review(0, 0, 0.0))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public List<PlaylistVideoEntity> createPlaylistVideo(List<VideoEntity> videoEntityList) {
        if (playlistVideoEntityList != null) {
            this.playlistVideoEntityList = null;
        }

        ArrayList<PlaylistVideoEntity> playlistVideoList = new ArrayList<>();
        for (int i = 0; i < videoEntityList.size(); i++) {
            playlistVideoList.add(new PlaylistVideoEntity(this, i + 1, videoEntityList.get(i)));
        }

        this.playlistVideoEntityList = playlistVideoList;
        this.videoCount = playlistVideoList.size();
        return playlistVideoList;
    }

    public List<VideoEntity> getVideoList() {
        return playlistVideoEntityList.stream()
                .map(PlaylistVideoEntity::getVideo)
                .collect(Collectors.toList());
    }
}
