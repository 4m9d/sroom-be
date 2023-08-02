package com.m9d.sroom.util.youtube.vo.playlist;

import com.m9d.sroom.util.youtube.vo.global.PageInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistVo {
    private PageInfoVo pageInfo;
    private List<PlaylistItemVo> items;
}
