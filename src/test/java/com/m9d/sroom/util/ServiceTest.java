package com.m9d.sroom.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.course.domain.Playlist;
import com.m9d.sroom.course.domain.Video;
import com.m9d.sroom.member.domain.Member;

import java.sql.Timestamp;
import java.util.UUID;

public class ServiceTest extends SroomTest{

    protected Member getNewMember() {
        UUID uuid = UUID.randomUUID();

        String memberCode = uuid.toString();
        return memberService.findOrCreateMemberByMemberCode(memberCode);
    }

    protected void enrollVideo(String videoCode, int duration, String channel, String thumbnail, double rating, int reviewCount, String title, String license, int viewCount) {
        Video video = Video.builder()
                .videoCode(videoCode)
                .duration(duration)
                .channel(channel)
                .thumbnail(thumbnail)
                .rating(rating)
                .reviewCount(reviewCount)
                .title(title)
                .license(license)
                .viewCount(viewCount)
                .build();

        courseRepository.saveVideo(video);
    }

    protected void enrollPlaylist(String playlistCode, int duration, String channel, String thumbnail, double rating, int reviewCount, String title) {
        Playlist playlist = Playlist.builder()
                .playlistCode(playlistCode)
                .duration(duration)
                .channel(channel)
                .thumbnail(thumbnail)
                .rating(rating)
                .reviewCount(reviewCount)
                .title(title)
                .publishedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        courseRepository.savePlaylist(playlist);
    }
}
