package com.m9d.sroom.youtube.vo.global;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ThumbnailVo {
    @JsonProperty("default")
    @SerializedName("default")
    private ThumbnailDefaultVo defaultThumbnail;
    private ThumbnailMediumVo medium;
    private ThumbnailHighVo high;
    private ThumbnailStandardVo standard;
    private ThumbnailMaxresVo maxres;
}
