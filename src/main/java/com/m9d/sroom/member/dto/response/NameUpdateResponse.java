package com.m9d.sroom.member.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NameUpdateResponse {

    private Long memberId;

    private String name;
}
