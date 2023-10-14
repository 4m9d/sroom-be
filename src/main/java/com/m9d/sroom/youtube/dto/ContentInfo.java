package com.m9d.sroom.youtube.dto;

import com.m9d.sroom.youtube.vo.common.ThumbnailVo;
import lombok.Getter;

@Getter
public class ContentInfo {

    protected String selectThumbnailInVo(ThumbnailVo thumbnailVo) {
        String selectedThumbnailUrl = "";


        if (thumbnailVo.getMedium() != null) {
            selectedThumbnailUrl = thumbnailVo.getMedium().getUrl();
        }

        if (thumbnailVo.getMaxres() != null) {
            return thumbnailVo.getMaxres().getUrl();
        }

        return selectedThumbnailUrl;
    }
}
