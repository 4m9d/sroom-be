package com.m9d.sroom.course.service;

import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.lecture.dto.response.CourseDetail;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CourseServiceV2 {

    private final CourseRepository courseRepository;
    private final YoutubeUtil youtubeUtil;
    private final DateUtil dateUtil;

    public CourseServiceV2(CourseRepository courseRepository, YoutubeUtil youtubeUtil, DateUtil dateUtil) {
        this.courseRepository = courseRepository;
        this.youtubeUtil = youtubeUtil;
        this.dateUtil = dateUtil;
    }

    public CourseDetail getCourseDetail(Long memberId, Long courseId) {
        return new CourseDetail();
    }
}
