package com.m9d.sroom.global.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistVideo {

    private Long id;

    private Long playlistId;

    private Long videoId;

    private Integer videoIndex;
}
