package com.m9d.sroom.youtube.dto.global;

import lombok.Data;

@Data
public class ContentDto {
    protected String selectThumbnailInVo(ThumbnailDto thumbnailDto) {
        String selectedThumbnailUrl = "";


        if (thumbnailDto.getMedium() != null) {
            selectedThumbnailUrl = thumbnailDto.getMedium().getUrl();
        }

        if (thumbnailDto.getMaxres() != null) {
            return thumbnailDto.getMaxres().getUrl();
        }

        return selectedThumbnailUrl;
    }
}
