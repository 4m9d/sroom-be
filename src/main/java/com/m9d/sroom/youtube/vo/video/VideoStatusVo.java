package com.m9d.sroom.youtube.vo.video;

import lombok.Data;

@Data
public class VideoStatusVo {
    private String uploadStatus;
    private String embeddable;
    private String license;
    private String publishAt;
}
