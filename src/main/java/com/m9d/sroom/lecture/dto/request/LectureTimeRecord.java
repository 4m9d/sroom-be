package com.m9d.sroom.lecture.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
public class LectureTimeRecord {

    @NotNull
    private int view_duration;

    public int getViewDuration() {
        return view_duration;
    }
}
