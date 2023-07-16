package com.m9d.sroom.util.youtube;

import java.util.Map;

public class YoutubeApiParameters {
    public static final Map<String, String> LECTURE_LIST_PARAMETERS = Map.of(
            "part", "id,snippet",
            "fields", "nextPageToken,prevPageToken,pageInfo,items(id,snippet(title,channelTitle,thumbnails,description,publishTime))"
    );

    public static final Map<String, String> VIDEO_PARAMETERS = Map.of(
            "part", "snippet,contentDetails,statistics,status",
            "fields", "pageInfo(totalResults),items(id,snippet(publishedAt,title,description,thumbnails,channelTitle,defaultAudioLanguage),contentDetails(duration,dimension),status(uploadStatus,embeddable),statistics(viewCount))"
    );

    public static final Map<String, String> PLAYLIST_PARAMETERS = Map.of(
            "part", "id,snippet,status,contentDetails",
            "fields", "pageInfo,items(id,snippet(publishedAt,title,description,thumbnails,channelTitle),status,contentDetails)"
    );

    public static final Map<String, String> PLAYLIST_ITEMS_PARAMETERS = Map.of(
            "part", "snippet,status",
            "fields", "pageInfo,nextPageToken,prevPageToken,items(snippet(title,position,resourceId,thumbnails),status)"
    );
}