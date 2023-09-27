package com.m9d.sroom.global.mapper;


import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Summary {

    private Long id;

    private Long courseId;

    private Long videoId;

    private Long courseVideoId;

    private String content;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean modified;
}