package com.m9d.sroom.youtube.dto.playlist;

import lombok.Data;

@Data
public class PlaylistItemDto {
    private String id;
    private PlaylistSnippetDto snippet;
    private PlaylistStatusDto status;
    private PlaylistContentDetailsDto contentDetails;
}
