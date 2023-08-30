package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Data
public class Summary {

    private String content;

    @JsonProperty("is_modifed")
    private boolean modified;

    private String modifiedAt;

    @Builder
    public Summary(String content, boolean modified, Timestamp timestamp) {
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.content = content;
        this.modified = modified;
        this.modifiedAt = simpleDateFormatter.format(timestamp);
    }
}
