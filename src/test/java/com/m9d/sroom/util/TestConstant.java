package com.m9d.sroom.util;

import com.m9d.sroom.util.constant.ContentConstant;

import java.sql.Timestamp;

public class TestConstant {

    public static final String MEMBER_PROFILE = "멤버닉네임";
    public static final String PLAYLIST_CHANNEL = "정보처리기사 전문가 손경식";
    public static final String PLAYLIST_DESCRIPTION = "잠실을 거쳐 선릉 소마센터까지";
    public static final String THUMBNAIL = "https://i.ytimg.com/vi/Pc6n6HgWU5c/mqdefault.jpg";
    public static final String SUMMARY_CONTENT = "강의노트 원본";
    public static final Long VIDEO_VIEW_COUNT = 100000L;
    public static final Timestamp PUBLISHED_AT = new Timestamp(System.currentTimeMillis() - 10 * 60 * 60 * 1000);
    public static final String LANGUAGE_KO = "KO";
    public static final String LICENSE_YOUTUBE = "youtube";
}
