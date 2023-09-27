package com.m9d.sroom.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PlaylistPageResult {

    private String nextPageToken;

    private Integer totalDurationPerPage;
}
