package com.m9d.sroom.youtube.dto;

import com.m9d.sroom.youtube.vo.search.SearchVo;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchInfo {

    private final String nextPageToken;

    private final Integer totalResults;

    private final Integer resultPerPage;

    private final List<SearchItemInfo> searchItemInfoList;

    public SearchInfo(SearchVo searchVo) {
        this.nextPageToken = searchVo.getNextPageToken();
        this.totalResults = searchVo.getPageInfo().getTotalResults();
        this.resultPerPage = searchVo.getPageInfo().getResultsPerPage();
        this.searchItemInfoList = searchVo.convertToInfoList();
    }
}
