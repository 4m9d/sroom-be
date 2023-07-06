package com.m9d.sroom.lecture.dto.response;

import com.m9d.sroom.lecture.domain.Index;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class IndexList {

    private List<Index> indexList;

    private String nextPageToken;

    @Builder
    public IndexList(List<Index> indexList, String nextPageToken) {
        this.indexList = indexList;
        this.nextPageToken = nextPageToken;
    }
}
