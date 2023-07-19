package com.m9d.sroom.course.service;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseInfo> getCourseList(Long memberId) {
        return null;
    }
}
