package com.m9d.sroom.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EnrollContentInfo {

    private final boolean isPlaylist;

    private final Long contentId;

    private final String title;

    private final Integer totalContentDuration;

    private final String thumbnail;

    private final String channel;

    private final List<InnerContent> innerContentList;
}
