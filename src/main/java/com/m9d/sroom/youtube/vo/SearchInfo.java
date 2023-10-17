package com.m9d.sroom.youtube.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class SearchInfo {

    private final String nextPageToken;

    private final Integer totalResults;

    private final Integer resultPerPage;

    private final List<SearchItemInfo> searchItemInfoList;
}
