package com.m9d.sroom.summary;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class Summary {

    private String content;

    private Timestamp updateAt;

    private boolean isModified;


    public Summary(String content, Timestamp updateAt, boolean isModified) {
        this.content = content;
        this.updateAt = updateAt;
        this.isModified = isModified;
    }
}
