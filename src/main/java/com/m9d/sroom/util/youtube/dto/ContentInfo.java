package com.m9d.sroom.util.youtube.dto;

import com.m9d.sroom.util.youtube.vo.global.ThumbnailVo;
import lombok.Data;
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
