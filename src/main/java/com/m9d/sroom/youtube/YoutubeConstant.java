package com.m9d.sroom.youtube;

import org.springframework.http.HttpMethod;

import java.util.Map;

public class YoutubeConstant {

    public static final Map<String, String> LECTURE_LIST_PARAMETERS = Map.of(
            "part", "id,snippet",
            "fields", "nextPageToken,prevPageToken,pageInfo,items(id,snippet(title,channelTitle,thumbnails,description,publishTime))"
    );

    public static final Map<String, String> VIDEO_PARAMETERS = Map.of(
            "part", "snippet,contentDetails,statistics,status",
            "fields", "pageInfo(totalResults),items(id,snippet(publishedAt,title,description,thumbnails,channelTitle,defaultAudioLanguage),contentDetails(duration,dimension),status(uploadStatus,embeddable,license,publishAt,privacyStatus),statistics(viewCount))"
    );

    public static final Map<String, String> PLAYLIST_PARAMETERS = Map.of(
            "part", "id,snippet,status,contentDetails",
            "fields", "pageInfo,items(id,snippet(publishedAt,title,description,thumbnails,channelTitle),status,contentDetails)"
    );

    public static final Map<String, String> PLAYLIST_ITEMS_PARAMETERS = Map.of(
            "part", "snippet,status",
            "fields", "pageInfo,nextPageToken,prevPageToken,items(snippet(title,position,resourceId,thumbnails),status)"
    );

    public static final String REQUEST_METHOD_GET = HttpMethod.GET.name();
    public static final String YOUTUBE_REQUEST_CONTENT_TYPE = "application/json";
    public static final int DEFAULT_INDEX_COUNT = 50;
    public static final int MAX_PLAYLIST_ITEM = 5000;

    public static final String UNKNOWN_LANGUAGE = "unknown";

    //JsonNode
    public static final int FIRST_INDEX = 0;
    public static final String JSONNODE_PROCESSED = "processed";
    public static final String JSONNODE_PRIVATE = "private";
    public static final String JSONNODE_UNSPECIFIED = "privacyStatusUnspecified";

    public static final String JSONNODE_TYPE_PLAYLIST = "youtube#playlist";
    public static final String JSONNODE_TYPE_VIDEO = "youtube#video";

    public static final Long VIEW_COUNT_DEFAULT = -1L;
}
