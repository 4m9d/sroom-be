package com.m9d.sroom.youtube.dto.global;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ThumbnailDto {
    @JsonProperty("default")
    @SerializedName("default")
    private ThumbnailDefaultDto defaultThumbnail;
    private ThumbnailMediumDto medium;
    private ThumbnailHighDto high;
    private ThumbnailStandardDto standard;
    private ThumbnailMaxresDto maxres;
}
