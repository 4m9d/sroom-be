package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.CourseEntity;
import com.m9d.sroom.common.entity.LectureEntity;
import com.m9d.sroom.common.repository.course.CourseRepository;
import com.m9d.sroom.common.repository.lecture.LectureRepository;
import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@Slf4j
public class CourseServiceV2 {

    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final CourseVideoService courseVideoService;

    public CourseServiceV2(CourseRepository courseRepository, LectureRepository lectureRepository, CourseVideoService courseVideoService) {
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.courseVideoService = courseVideoService;
    }

    @Transactional
    public EnrolledCourseInfo enroll(Long memberId, NewLecture newLecture, boolean useSchedule, EnrollContentInfo contentInfo) {
        Course course = new EnrollCondition(newLecture, useSchedule).createCourse(contentInfo);
        CourseEntity courseEntity = courseRepository.save(new CourseEntity(memberId, course));

        LectureEntity lectureEntity = lectureRepository.save(LectureEntity.builder()
                .memberId(memberId)
                .courseId(courseEntity.getCourseId())
                .sourceId(contentInfo.getContentId())
                .channel(contentInfo.getChannel())
                .playlist(contentInfo.isPlaylist())
                .lectureIndex(CourseConstant.ENROLL_LECTURE_INDEX)
                .build());

        courseVideoService.saveCourseVideoList(memberId, courseEntity.getCourseId(), lectureEntity.getId(), course.getCourseVideoList());

        return EnrolledCourseInfo.builder()
                .title(contentInfo.getTitle())
                .courseId(courseEntity.getCourseId())
                .lectureId(lectureEntity.getId())
                .build();
    }

    public boolean validateCourseForMember(Long memberIdFromRequest, Long courseId) {
        return courseRepository.getById(courseId).getMemberId().equals(memberIdFromRequest);
    }

    @Transactional
    public EnrolledCourseInfo addLecture(Long memberId, Long courseId, EnrollContentInfo contentInfo) {
        Course course = courseRepository.getById(courseId).toCourse(courseVideoService.getCourseVideoList(courseId));
        course.addCourseVideo(contentInfo.getInnerContentList());

        LectureEntity lectureEntity = lectureRepository.save(LectureEntity.builder()
                .memberId(memberId)
                .courseId(courseId)
                .sourceId(contentInfo.getContentId())
                .channel(contentInfo.getChannel())
                .playlist(contentInfo.isPlaylist())
                .lectureIndex(course.getLastLectureIndex())
                .build());

        log.debug("last lecture index = {}", course.getLastLectureIndex());
        courseVideoService.saveCourseVideoList(memberId, courseId, lectureEntity.getId(),
                course.getCourseVideoListByLectureIndex(course.getLastLectureIndex()));

        if (course.isScheduled()) {
            course.reschedule(courseVideoService.getDurationList(courseId));
            courseVideoService.updateSections(courseId, course.getCourseVideoList());
        }

        updateCourse(courseId, course);

        return EnrolledCourseInfo.builder()
                .title(course.getTitle())
                .courseId(courseId)
                .lectureId(lectureEntity.getId())
                .build();
    }

    private void updateCourse(Long courseId, Course course) {
        CourseEntity courseEntity = courseRepository.getById(courseId);
        courseEntity.setCourseTitle(course.getTitle());
        courseEntity.setThumbnail(course.getThumbnail());
        courseEntity.setDuration(course.getDuration());
        courseEntity.setScheduled(course.isScheduled());
        courseEntity.setWeeks(course.getWeeks());
        courseEntity.setExpectedEndDate(course.getExpectedEndDate());
        courseRepository.updateById(courseId, courseEntity);
    }
}
