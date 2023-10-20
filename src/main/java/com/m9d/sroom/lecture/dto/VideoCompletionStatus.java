package com.m9d.sroom.lecture.dto;

import lombok.Data;

@Data
public class VideoCompletionStatus {

    private boolean completed;

    private boolean fullyWatched;

    private boolean completedNow;

    private boolean rewound;

    private int timeGap;
}
