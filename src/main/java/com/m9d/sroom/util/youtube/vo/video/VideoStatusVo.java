package com.m9d.sroom.util.youtube.vo.video;

import lombok.Data;

@Data
public class VideoStatusVo {
    private String uploadStatus;
    private String embeddable;
    private String license;
    private String publishAt;
}
