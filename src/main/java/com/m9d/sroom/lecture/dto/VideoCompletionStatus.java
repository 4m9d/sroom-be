package com.m9d.sroom.lecture.dto;

import lombok.Data;

@Data
public class VideoCompletionStatus {

    private Boolean completed;

    private Boolean fullyWatched;

    private Boolean completedNow;

    private Boolean rewound;
}
