package com.m9d.sroom.material.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Summary {

    private String content;

    private boolean modified;

    private String modifiedAt;
}
