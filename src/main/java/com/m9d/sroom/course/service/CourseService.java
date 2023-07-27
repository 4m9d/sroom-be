package com.m9d.sroom.course.service;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseInfo> getCourseList(Long memberId) {
        return null;
    }

    @Transactional
    public EnrolledCourseInfo enrollCourse(Long memberId, String LectureId) {
        return null;
    }

    public void requestToFastApi(String lectureCode, String defaultLanguage){

    }
}
