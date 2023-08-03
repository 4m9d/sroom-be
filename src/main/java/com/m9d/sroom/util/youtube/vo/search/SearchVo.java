package com.m9d.sroom.util.youtube.vo.search;

import com.m9d.sroom.util.youtube.vo.global.PageInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class SearchVo {

    private String nextPageToken;

    private PageInfoVo pageInfo;

    List<SearchItemVo> items;

}
