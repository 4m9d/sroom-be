package com.m9d.sroom.search.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureTimeRecord {

    @NotNull
    private int view_duration;

    public int getViewDuration() {
        return view_duration;
    }
}
