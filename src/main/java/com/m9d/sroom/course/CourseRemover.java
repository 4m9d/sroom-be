package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.LectureEntity;
import com.m9d.sroom.common.entity.jpa.ReviewEntity;
import com.m9d.sroom.common.repository.course.CourseJpaRepository;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizJpaRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoJpaRepository;
import com.m9d.sroom.common.repository.lecture.LectureJpaRepository;
import com.m9d.sroom.common.repository.review.ReviewJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseRemover {

    private final CourseJpaRepository courseRepository;

    private final CourseVideoJpaRepository courseVideoRepository;

    private final CourseQuizJpaRepository courseQuizRepository;

    private final LectureJpaRepository lectureRepository;

    private final ReviewJpaRepository reviewRepository;

    public CourseRemover(CourseJpaRepository courseRepository, CourseVideoJpaRepository courseVideoRepository, CourseQuizJpaRepository courseQuizRepository, LectureJpaRepository lectureRepository, ReviewJpaRepository reviewRepository) {
        this.courseRepository = courseRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.lectureRepository = lectureRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public void remove(CourseEntity courseEntity) {
        courseQuizRepository.deleteByCourseId(courseEntity.getCourseId());
        courseVideoRepository.deleteByCourseId(courseEntity.getCourseId());

        List<LectureEntity> lectureEntityList = lectureRepository.findListByCourseId(courseEntity.getCourseId());
        for(LectureEntity lectureEntity : lectureEntityList){
            makeLectureIdNull(lectureEntity);
        }

        lectureRepository.deleteByCourseId(courseEntity.getCourseId());
        courseEntity.getMember().getCourses().remove(courseEntity);
        courseRepository.deleteById(courseEntity.getCourseId());
    }

    private void makeLectureIdNull(LectureEntity lectureEntity) {
        lectureEntity.getReview().removeLecture();
    }
}
