package com.m9d.sroom.youtube.vo.video;

import com.m9d.sroom.youtube.vo.global.PageInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class VideoVo {

    private String nextPageToken;

    private PageInfoVo pageInfo;

    private List<VideoItemVo> items;
}
