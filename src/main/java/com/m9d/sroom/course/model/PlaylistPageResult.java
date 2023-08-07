package com.m9d.sroom.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistPageResult {
    private String nextPageToken;
    private int totalDurationPerPage;
}
