package com.m9d.sroom.youtube.dto.video;

import lombok.Data;

@Data
public class VideoStatusDto {
    private String uploadStatus;
    private String embeddable;
    private String license;
    private String publishAt;
}
