package com.m9d.sroom.youtube.dto.search;

import com.m9d.sroom.youtube.vo.SearchInfo;
import com.m9d.sroom.youtube.vo.SearchItemInfo;
import com.m9d.sroom.youtube.dto.global.ContentDto;
import com.m9d.sroom.youtube.dto.global.PageInfoDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SearchDto extends ContentDto {

    private String nextPageToken;

    private PageInfoDto pageInfo;

    List<SearchItemDto> items;

    public SearchInfo toSearchInfo() {
        return SearchInfo.builder()
                .nextPageToken(nextPageToken)
                .totalResults(pageInfo.getTotalResults())
                .resultPerPage(pageInfo.getResultsPerPage())
                .searchItemInfoList(getSearchItemInfoList())
                .build();
    }

    public List<SearchItemInfo> getSearchItemInfoList() {
        List<SearchItemInfo> searchItemInfoList = new ArrayList<>();

        for (SearchItemDto itemVo : items) {
            searchItemInfoList.add(itemVo.toSearchItemInfo());
        }
        return searchItemInfoList;
    }

}
