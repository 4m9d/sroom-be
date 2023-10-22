package com.m9d.sroom.summary;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class Summary {

    private final long videoId;

    private String content;

    private Timestamp updateAt;

    private boolean isModified;


    public Summary(long videoId, String content, Timestamp updateAt, boolean isModified) {
        this.videoId = videoId;
        this.content = content;
        this.updateAt = updateAt;
        this.isModified = isModified;
    }
}
