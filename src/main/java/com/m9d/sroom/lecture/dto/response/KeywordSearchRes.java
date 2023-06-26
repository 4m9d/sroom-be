package com.m9d.sroom.lecture.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class KeywordSearchRes {

    private int resultPerPage;
    private String nextPageToken;
    private String prevPageToken;
    private List<Lecture> lectures;

    @Builder
    public KeywordSearchRes(int resultPerPage, String nextPageToken, String prevPageToken, List<Lecture> lectures) {
        this.resultPerPage = resultPerPage;
        this.nextPageToken = nextPageToken;
        this.prevPageToken = prevPageToken;
        this.lectures = lectures;
    }
}