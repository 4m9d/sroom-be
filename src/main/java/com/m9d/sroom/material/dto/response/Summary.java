package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Summary {

    private String content;

    @JsonProperty("is_modifed")
    private boolean modified;

    private String modifiedAt;
}
