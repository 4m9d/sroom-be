package com.m9d.sroom.course.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCourse {

    private String lecture_code;

    private int daily_target_time;

    private List<Integer> scheduling;

    private String expected_end_time;
}
