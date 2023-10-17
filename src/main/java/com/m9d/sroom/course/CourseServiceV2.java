package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.CourseEntity;
import com.m9d.sroom.common.entity.LectureEntity;
import com.m9d.sroom.common.repository.course.CourseRepository;
import com.m9d.sroom.common.repository.lecture.LectureRepository;
import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.lecture.dto.response.Section;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseServiceV2 {

    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final CourseVideoService courseVideoService;

    public CourseServiceV2(CourseRepository courseRepository, LectureRepository lectureRepository,
                           CourseVideoService courseVideoService) {
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.courseVideoService = courseVideoService;
    }

    @Transactional
    public EnrolledCourseInfo enroll(Long memberId, NewLecture newLecture, boolean useSchedule,
                                     EnrollContentInfo contentInfo) {
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

        courseVideoService.saveCourseVideoList(memberId, courseEntity.getCourseId(), lectureEntity.getId(),
                course.getCourseVideoList());

        return EnrolledCourseInfo.builder()
                .title(contentInfo.getTitle())
                .courseId(courseEntity.getCourseId())
                .lectureId(lectureEntity.getId())
                .build();
    }

    public boolean validateCourseForMember(Long memberIdFromRequest, Long courseId) {
        return memberIdFromRequest.equals(courseRepository.getById(courseId).getMemberId());
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

        courseVideoService.saveCourseVideoList(memberId, courseId, lectureEntity.getId(),
                course.getCourseVideoListByLectureIndex(course.getLastLectureIndex()));

        if (course.isScheduled()) {
            course.reschedule(courseVideoService.getDurationList(courseId));
            courseVideoService.updateSections(courseId, course.getCourseVideoList());
        }

        updateCourseEntity(courseId, course);

        return EnrolledCourseInfo.builder()
                .title(course.getTitle())
                .courseId(courseId)
                .lectureId(lectureEntity.getId())
                .build();
    }

    private void updateCourseEntity(Long courseId, Course course) {
        CourseEntity courseEntity = courseRepository.getById(courseId);
        courseEntity.setCourseTitle(course.getTitle());
        courseEntity.setThumbnail(course.getThumbnail());
        courseEntity.setDuration(course.getDuration());
        courseEntity.setScheduled(course.isScheduled());
        courseEntity.setWeeks(course.getWeeks());
        courseEntity.setExpectedEndDate(course.getExpectedEndDate());
        courseRepository.updateById(courseId, courseEntity);
    }

    @Transactional
    public CourseDetail getCourseDetail(Long courseId) {
        CourseEntity courseEntity = courseRepository.getById(courseId);
        Course course = courseEntity.toCourse(courseVideoService.getCourseVideoList(courseId));

        Set<String> channels = lectureRepository.getListByCourseId(courseId).stream()
                .map(LectureEntity::getChannel)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return new CourseDetail(courseId, course, channels, getSectionList(courseEntity),
                courseVideoService.getLastVideoInfo(courseId));
    }

    private List<Section> getSectionList(CourseEntity courseEntity) {
        List<Section> sectionList = new ArrayList<>();
        if (courseEntity.getWeeks() == 0) {
            sectionList.add(
                    new Section(courseVideoService.getWatchInfoList(courseEntity.getCourseId(), 0), 0));
        } else {
            for (int section = 1; section <= courseEntity.getWeeks(); section++) {
                sectionList.add(
                        new Section(courseVideoService.getWatchInfoList(courseEntity.getCourseId(), section), section));
            }
        }
        return sectionList;
    }
}
