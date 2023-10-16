package com.m9d.sroom.youtube.vo.playlist;

import lombok.Data;

@Data
public class PlaylistItemVo {
    private String id;
    private PlaylistSnippetVo snippet;
    private PlaylistStatusVo status;
    private PlaylistContentDetailsVo contentDetails;
}
