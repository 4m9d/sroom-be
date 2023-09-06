package com.m9d.sroom.material.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseQuizIdList {
    private List<Long> courseQuizIdList;
}
