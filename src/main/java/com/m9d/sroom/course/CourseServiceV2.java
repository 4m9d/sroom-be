package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.CourseEntity;
import com.m9d.sroom.common.entity.CourseVideoEntity;
import com.m9d.sroom.common.entity.LectureEntity;
import com.m9d.sroom.common.repository.course.CourseRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.lecture.LectureRepository;
import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.lecture.dto.VideoCompletionStatus;
import com.m9d.sroom.lecture.dto.response.CourseBrief;
import com.m9d.sroom.lecture.dto.response.LectureStatus;
import com.m9d.sroom.lecture.dto.response.Section;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.m9d.sroom.lecture.constant.LectureConstant.LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS;

@Service
@Slf4j
public class CourseServiceV2 {

    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final CourseServiceHelper courseServiceHelper;
    private final CourseInfoUpdater courseInfoUpdater;

    public CourseServiceV2(CourseRepository courseRepository, LectureRepository lectureRepository, CourseVideoRepository courseVideoRepository, CourseServiceHelper courseServiceHelper, CourseInfoUpdater courseInfoUpdater) {
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.courseServiceHelper = courseServiceHelper;
        this.courseInfoUpdater = courseInfoUpdater;
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

        for (CourseVideo courseVideo : course.getCourseVideoList()) {
            courseVideoRepository.save(new CourseVideoEntity(memberId, courseEntity.getCourseId(),
                    lectureEntity.getId(), courseVideo));
        }

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
        Course course = courseRepository.getById(courseId).toCourse(courseServiceHelper.getCourseVideoList(courseId));
        course.addCourseVideo(contentInfo.getInnerContentList());

        LectureEntity lectureEntity = lectureRepository.save(LectureEntity.builder()
                .memberId(memberId)
                .courseId(courseId)
                .sourceId(contentInfo.getContentId())
                .channel(contentInfo.getChannel())
                .playlist(contentInfo.isPlaylist())
                .lectureIndex(course.getLastLectureIndex())
                .build());

        for (CourseVideo courseVideo : course.getCourseVideoListByLectureIndex(lectureEntity.getLectureIndex())) {
            courseVideoRepository.save(new CourseVideoEntity(memberId, courseId, lectureEntity.getId(), courseVideo));
        }

        if (course.isScheduled()) {
            course.reschedule(courseServiceHelper.getDurationList(courseId));
            courseServiceHelper.updateSections(courseId, course.getCourseVideoList());
        }

        courseServiceHelper.updateCourseEntity(courseId, course);

        return EnrolledCourseInfo.builder()
                .title(course.getTitle())
                .courseId(courseId)
                .lectureId(lectureEntity.getId())
                .build();
    }

    @Transactional
    public CourseDetail getCourseDetail(Long courseId) {
        CourseEntity courseEntity = courseRepository.getById(courseId);
        Course course = courseEntity.toCourse(courseServiceHelper.getCourseVideoList(courseId));

        Set<String> channels = lectureRepository.getListByCourseId(courseId).stream()
                .map(LectureEntity::getChannel)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return new CourseDetail(courseId, course, channels, getSectionList(courseEntity),
                courseVideoRepository.getLastInfoByCourseId(courseId));
    }

    private List<Section> getSectionList(CourseEntity courseEntity) {
        List<Section> sectionList = new ArrayList<>();
        if (courseEntity.getWeeks() == 0) {
            sectionList.add(
                    new Section(courseVideoRepository.getWatchInfoListByCourseIdAndSection(courseEntity.getCourseId(), 0), 0));
        } else {
            for (int section = 1; section <= courseEntity.getWeeks(); section++) {
                sectionList.add(
                        new Section(courseVideoRepository.getWatchInfoListByCourseIdAndSection(courseEntity.getCourseId(), section), section));
            }
        }
        return sectionList;
    }

    @Transactional
    public LectureStatus updateLectureTime(Long memberId, Long courseVideoId, int viewDuration,
                                           boolean isMarkedAsCompleted) {
        CourseVideoEntity courseVideoEntity = courseServiceHelper.getCourseVideo(memberId, courseVideoId);
        VideoCompletionStatus status = courseServiceHelper
                .getVideoCompletionStatus(courseVideoEntity.toCourseVideo(), viewDuration, isMarkedAsCompleted);

        courseInfoUpdater.updateCourseVideoStatus(courseVideoEntity, viewDuration, status.isCompleted());

        if (!status.isRewound()) {
            courseInfoUpdater.updateCourseDailyLog(memberId, courseVideoEntity.getCourseId(), status);
            courseInfoUpdater.updateMemberLeaningTime(memberId, status);
            courseInfoUpdater.updateCourseLastViewTime(courseVideoEntity.getCourseId());
        }

        if (status.isCompletedNow()) {
            courseInfoUpdater.updateCourseProgress(memberId, courseVideoEntity.getCourseId());
        }

        if(status.isFullyWatched()){
            courseInfoUpdater.updateLastViewVideoToNext(courseVideoEntity.getCourseId(), viewDuration);
        }

        return LectureStatus.builder()
                .courseVideoId(courseVideoId)
                .viewDuration(viewDuration)
                .complete(status.isCompleted())
                .build();
    }

    public List<CourseBrief> getCourseBriedList(Long memberId) {
        return courseRepository.getBriefListByMemberId(memberId);
    }
}
