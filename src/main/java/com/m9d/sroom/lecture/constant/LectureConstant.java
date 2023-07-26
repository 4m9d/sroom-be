package com.m9d.sroom.lecture.constant;

public class LectureConstant {
    public static final int DEFAULT_REVIEW_COUNT = 50;
    public static final int DEFAULT_REVIEW_OFFSET = 0;

    //time format
    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final String FORMAT_WITH_HOUR = "%d:%02d:%02d";
    public static final String FORMAT_WITHOUT_HOUR = "%d:%02d";

    //check if playlist
    public static final int LECTURE_CODE_START_INDEX = 0;
    public static final int LECTURE_CODE_PLAYLIST_INDICATOR_LENGTH = 2;
    public static final String PLAYLIST_CODE_INDICATOR = "PL";

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
    public static final String JSONNODE_PRIVACY_STATUS = "privacyStatus";
    public static final String JSONNODE_PUBLIC = "public";
    public static final String JSONNODE_POSITION = "position";
    public static final String JSONNODE_PUBLISHED_AT = "publishedAt";
    public static final int PUBLISHED_DATE_START_INDEX = 0;
    public static final int PUBLISHED_DATE_END_INDEX = 10;

    public static final String JSONNODE_TYPE_PLAYLIST = "youtube#playlist";
    public static final String JSONNODE_TYPE_VIDEO = "youtube#video";

}
