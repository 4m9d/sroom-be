package com.m9d.sroom.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "멤버 이름 수정")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NameUpdateRequest {

    @Schema(description = "수정할 멤버 이름", example = "손경식")
    private String name;
}
