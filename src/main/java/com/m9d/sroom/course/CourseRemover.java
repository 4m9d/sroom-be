package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.repository.course.CourseJpaRepository;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizJpaRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoJpaRepository;
import com.m9d.sroom.common.repository.lecture.LectureJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseRemover {

    private final CourseJpaRepository courseRepository;

    private final CourseVideoJpaRepository courseVideoRepository;

    private final CourseQuizJpaRepository courseQuizRepository;

    private final LectureJpaRepository lectureRepository;

    public CourseRemover(CourseJpaRepository courseRepository, CourseVideoJpaRepository courseVideoRepository, CourseQuizJpaRepository courseQuizRepository, LectureJpaRepository lectureRepository) {
        this.courseRepository = courseRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.lectureRepository = lectureRepository;
    }

    @Transactional
    public void remove(CourseEntity courseEntity) {
        courseQuizRepository.deleteByCourseId(courseEntity.getCourseId());
        courseVideoRepository.deleteByCourseId(courseEntity.getCourseId());

        lectureRepository.deleteByCourseId(courseEntity.getCourseId());
        courseEntity.getMember().getCourses().remove(courseEntity);
        courseRepository.deleteById(courseEntity.getCourseId());
    }
}
