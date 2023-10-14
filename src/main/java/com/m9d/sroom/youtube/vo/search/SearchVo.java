package com.m9d.sroom.youtube.vo.search;

import com.m9d.sroom.youtube.dto.SearchItemInfo;
import com.m9d.sroom.youtube.vo.common.PageInfoVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchVo {

    private String nextPageToken;

    private PageInfoVo pageInfo;

    List<SearchItemVo> items;

    public List<SearchItemInfo> convertToInfoList() {
        List<SearchItemInfo> searchItemInfoList = new ArrayList<>();

        for (SearchItemVo itemVo : items) {
            searchItemInfoList.add(new SearchItemInfo(itemVo));
        }

        return searchItemInfoList;
    }
}
