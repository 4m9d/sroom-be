package com.m9d.sroom.util.youtube.vo.video;

import com.m9d.sroom.util.youtube.vo.global.PageInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class VideoVo {

    private String nextPageToken;

    private PageInfoVo pageInfo;

    private List<VideoItemVo> items;
}
