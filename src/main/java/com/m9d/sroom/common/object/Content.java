package com.m9d.sroom.common.object;


import java.sql.Timestamp;

public abstract class Content {

    public abstract String getTitle();

    public abstract int getDuration();

    public abstract String getThumbnail();

    public abstract String getChannel();

    public abstract boolean isPlaylist();

    public abstract String getDescription();

    public abstract String getCode();

    public abstract Timestamp getPublishedAt();
}
