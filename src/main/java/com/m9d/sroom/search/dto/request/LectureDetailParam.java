package com.m9d.sroom.search.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LectureDetailParam {

    private boolean index_only = false;

    private int index_limit = 50;

    private boolean review_only = false;

    private int review_offset = 0;

    private int review_limit = 10;

    public boolean isIndexOnly() {
        return index_only;
    }

    public int getIndexLimit() {
        return index_limit;
    }

    public boolean isReviewOnly() {
        return review_only;
    }

    public int getReviewOffset() {
        return review_offset;
    }

    public int getReviewLimit() {
        return review_limit;
    }
}
