package com.m9d.sroom.lecture.dto;


import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class PlaylistInfoInSearch {

    private String playlistCode;

    private Integer videoCount;

    private Timestamp updatedAt;

    private String description;
}
