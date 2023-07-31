package com.m9d.sroom.util.youtube;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

import java.util.Map;

public class YoutubeConstant {

    public static final Map<String, String> LECTURE_LIST_PARAMETERS = Map.of(
            "part", "id,snippet",
            "fields", "nextPageToken,prevPageToken,pageInfo,items(id,snippet(title,channelTitle,thumbnails,description,publishTime))"
    );

    public static final Map<String, String> VIDEO_PARAMETERS = Map.of(
            "part", "snippet,contentDetails,statistics,status",
            "fields", "pageInfo(totalResults),items(id,snippet(publishedAt,title,description,thumbnails,channelTitle,defaultAudioLanguage),contentDetails(duration,dimension),status(uploadStatus,embeddable,license,publishAt),statistics(viewCount))"
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
    public static final int DEFAULT_INDEX_OFFSET = 0;

    public static final String UNKNOWN_LANGUAGE = "unknown";

    //JsonNode
    public static final int FIRST_INDEX = 0;
    public static final String JSONNODE_ITEMS = "items";
    public static final String JSONNODE_SNIPPET = "snippet";
    public static final String JSONNODE_ID = "id";
    public static final String JSONNODE_RESOURCE_ID = "resourceId";
    public static final String JSONNODE_PLAYLIST_ID = "playlistId";
    public static final String JSONNODE_VIDEO_ID = "videoId";
    public static final String JSONNODE_KIND = "kind";
    public static final String JSONNODE_THUMBNAILS = "thumbnails";
    public static final String JSONNODE_THUMBNAIL_MEDIUM = "medium";
    public static final String JSONNODE_THUMBNAIL_URL = "url";
    public static final String JSONNODE_THUMBNAIL_MAXRES = "maxres";
    public static final String JSONNODE_TITLE = "title";
    public static final String JSONNODE_CHANNEL_TITLE = "channelTitle";
    public static final String JSONNODE_DESCRIPTION = "description";
    public static final String JSONNODE_NEXT_PAGE_TOKEN = "nextPageToken";
    public static final String JSONNODE_PREV_PAGE_TOKEN = "prevPageToken";
    public static final String JSONNODE_PAGE_INFO = "pageInfo";
    public static final String JSONNODE_TOTAL_RESULTS = "totalResults";
    public static final String JSONNODE_RESULT_PER_PAGE = "resultsPerPage";
    public static final String JSONNODE_CONTENT_DETAIL = "contentDetails";
    public static final String JSONNODE_DURATION = "duration";
    public static final String JSONNODE_STATISTICS = "statistics";
    public static final String JSONNODE_VIEW_COUNT = "viewCount";
    public static final String JSONNODE_ITEM_COUNT = "itemCount";
    public static final String JSONNODE_STATUS = "status";
    public static final String JSONNODE_LICENCE = "license";
    public static final String JSONNODE_PRIVACY_STATUS = "privacyStatus";
    public static final String JSONNODE_PUBLIC = "public";
    public static final String JSONNODE_LANGUAGE = "defaultAudioLanguage";
    public static final String JSONNODE_POSITION = "position";
    public static final String JSONNODE_PUBLISHETIME = "publishTime";
    public static final String JSONNODE_PUBLISHED_AT = "publishedAt";
    public static final int PUBLISHED_DATE_START_INDEX = 0;
    public static final int PUBLISHED_DATE_END_INDEX = 10;

    public static final String JSONNODE_TYPE_PLAYLIST = "youtube#playlist";
    public static final String JSONNODE_TYPE_VIDEO = "youtube#video";
}
