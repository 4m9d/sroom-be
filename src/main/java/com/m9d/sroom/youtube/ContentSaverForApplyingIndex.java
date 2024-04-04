package com.m9d.sroom.youtube;

import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.search.SearchService;
import com.m9d.sroom.search.dto.response.KeywordSearchResponse;
import com.m9d.sroom.search.exception.LectureNotFoundException;
import com.m9d.sroom.video.VideoService;
import com.m9d.sroom.video.vo.Video;
import com.m9d.sroom.youtube.vo.SearchInfo;
import com.m9d.sroom.youtube.vo.SearchItemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@RestController
@Slf4j
public class ContentSaverForApplyingIndex {

    private final VideoService videoService;
    private final SearchService searchService;
    private final YoutubeMapper youtubeMapper;
    private final PlaylistService playlistService;

    public ContentSaverForApplyingIndex(VideoService videoService, SearchService searchService, YoutubeMapper youtubeMapper, PlaylistService playlistService) {
        this.videoService = videoService;
        this.searchService = searchService;
        this.youtubeMapper = youtubeMapper;
        this.playlistService = playlistService;
    }

//    @PostMapping("/savecontent")
//    public int saveContents(@RequestParam int scale) {
//
//        int count = 0;
//
//        for(int i=0; i<scale; i++){
//            String searchStr = generateRandomString(4);
//            log.debug("maked str = " + searchStr + " trying count = " + i);
//
//            SearchInfo searchInfo = youtubeMapper.getSearchInfo(searchStr, null, 50, "video");
//
//            log.debug("searched video count = " + searchInfo.getSearchItemInfoList().size());
//
//            count = saveVideo(searchInfo, count);
//        }
//
//        log.debug("yield = " + (double) count / scale);
//
//        return count;
//    }

    private int saveVideo(SearchInfo searchInfo, int count) {
        for(SearchItemInfo searchItemInfo : searchInfo.getSearchItemInfoList()){
            Video video = videoService.getRecentVideo(searchItemInfo.getCode());

            videoService.putVideo(video);
            log.debug("video title = " + video.getTitle() + " count = " + count);
            count++;
        }
        return count;
    }

    public static String generateRandomString(int length) {
        // 랜덤 문자열 생성을 위한 문자셋
        String charset = "abcdefghijklmnopqrstuvwxyz";
        String consonant = "bcdfghjklmnpqrstvwxyz";
        String vowels = "aeiou";
        // 문자열을 저장할 StringBuilder 생성
        StringBuilder sb = new StringBuilder();
        // 랜덤 객체 생성
        Random random = new Random();

        int randomIndex = random.nextInt(consonant.length());
        sb.append(consonant.charAt(randomIndex));

        randomIndex = random.nextInt(vowels.length());
        sb.append(vowels.charAt(randomIndex));

        // 길이 만큼 반복하여 랜덤 문자열 생성
        for (int i = 2; i < length; i++) {
            // 문자셋에서 랜덤한 인덱스 선택
            randomIndex = random.nextInt(charset.length());
            // 선택된 인덱스의 문자를 StringBuilder에 추가
            sb.append(charset.charAt(randomIndex));
        }

        // StringBuilder를 String으로 변환하여 반환
        return sb.toString();
    }
}
