package com.m9d.sroom.common.vo;

import lombok.Getter;

@Getter
public class Lecture {
    private final Long sourceId;

    private final boolean isPlaylist;

    private final Integer index;

    public Lecture(Long sourceId, boolean isPlaylist, Integer index) {
        this.sourceId = sourceId;
        this.isPlaylist = isPlaylist;
        this.index = index;
    }
}
