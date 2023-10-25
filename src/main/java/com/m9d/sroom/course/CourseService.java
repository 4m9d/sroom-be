package com.m9d.sroom.course;

import com.m9d.sroom.common.LearningActivityUpdater;
import com.m9d.sroom.common.entity.CourseEntity;
import com.m9d.sroom.common.entity.CourseVideoEntity;
import com.m9d.sroom.common.entity.LectureEntity;
import com.m9d.sroom.common.repository.course.CourseRepository;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.lecture.LectureRepository;
import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.InnerContent;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.vo.Course;
import com.m9d.sroom.course.vo.CourseVideo;
import com.m9d.sroom.search.dto.VideoCompletionStatus;
import com.m9d.sroom.search.dto.response.CourseBrief;
import com.m9d.sroom.search.dto.response.LectureStatus;
import com.m9d.sroom.search.dto.response.Section;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final CourseQuizRepository courseQuizRepository;
    private final CourseServiceHelper courseServiceHelper;
    private final CourseCreator courseCreator;
    private final LearningActivityUpdater learningActivityUpdater;

    public CourseService(CourseRepository courseRepository, LectureRepository lectureRepository,
                         CourseVideoRepository courseVideoRepository, CourseQuizRepository courseQuizRepository,
                         CourseServiceHelper courseServiceHelper, CourseCreator courseCreator, LearningActivityUpdater learningActivityUpdater) {
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.courseServiceHelper = courseServiceHelper;
        this.courseCreator = courseCreator;
        this.learningActivityUpdater = learningActivityUpdater;
    }

    public MyCourses getMyCourses(Long memberId) {
        List<CourseEntity> latestCourseList = courseRepository.getLatestOrderByMemberId(memberId);
        List<CourseInfo> courseInfoList = courseServiceHelper.getCourseInfoList(latestCourseList);
        int unfinishedCourseCount = courseServiceHelper.getUnfinishedCourseCount(latestCourseList);
        int courseCount = latestCourseList.size();
        int completionRate = (int) ((float) (courseCount - unfinishedCourseCount) / courseCount * 100);

        return MyCourses.builder()
                .unfinishedCourse(unfinishedCourseCount)
                .completionRate(completionRate)
                .courses(courseInfoList)
                .build();
    }

    @Transactional
    public EnrolledCourseInfo enroll(Long memberId, NewLecture newLecture, boolean useSchedule,
                                     EnrollContentInfo contentInfo) {
        Course course = courseCreator.create(newLecture, useSchedule, contentInfo);
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

    @Transactional
    public EnrolledCourseInfo addLecture(Long memberId, Long courseId, EnrollContentInfo contentInfo) {
        Course course = courseServiceHelper.getCourse(courseId);
        addCourseVideo(course, contentInfo.getInnerContentList());

        LectureEntity lectureEntity = lectureRepository.save(LectureEntity.builder()
                .memberId(memberId)
                .courseId(courseId)
                .sourceId(contentInfo.getContentId())
                .channel(contentInfo.getChannel())
                .playlist(contentInfo.isPlaylist())
                .lectureIndex(course.getLastLectureIndex())
                .build());

        List<CourseVideo> addedCourseVideoList = course.getCourseVideoList().stream()
                .filter(video -> video.getLectureIndex() == lectureEntity.getLectureIndex())
                .collect(Collectors.toList());

        for (CourseVideo courseVideo : addedCourseVideoList) {
            courseVideoRepository.save(new CourseVideoEntity(memberId, courseId, lectureEntity.getId(), courseVideo));
        }

        if (course.isScheduled()) {
            course.reschedule(courseServiceHelper.getDurationArray(courseId));

            for (CourseVideo courseVideo : course.getCourseVideoList()) {
                courseServiceHelper.updateCourseVideoSection(courseId, courseVideo.getVideoIndex(), courseVideo.getSection());
            }
        }

        courseServiceHelper.updateCourseEntity(courseId, course);

        return EnrolledCourseInfo.builder()
                .title(course.getTitle())
                .courseId(courseId)
                .lectureId(lectureEntity.getId())
                .build();
    }

    public void addCourseVideo(Course course, List<InnerContent> innerContentList) {
        int videoIndex = course.getCourseVideoList().stream()
                .mapToInt(CourseVideo::getVideoIndex)
                .max()
                .orElse(0) + 1;

        int durationToAdd = 0;
        int lastLectureIndex = course.getLastLectureIndex();

        for (InnerContent innerContent : innerContentList) {
            course.getCourseVideoList().add(new CourseVideo(innerContent.getContentId(), innerContent.getSummaryId(),
                    CourseConstant.ENROLL_DEFAULT_SECTION_NO_SCHEDULE, videoIndex++, lastLectureIndex + 1));
            durationToAdd += innerContent.getDuration();
        }
        course.addCourseDuration(durationToAdd);
    }

    @Transactional
    public CourseDetail getCourseDetail(Long courseId) {
        Course course = courseServiceHelper.getCourse(courseId);

        Set<String> channels = lectureRepository.getListByCourseId(courseId).stream()
                .map(LectureEntity::getChannel)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return new CourseDetail(courseId, course, channels, getSectionList(courseId, course.getWeeks()),
                courseServiceHelper.getCourseProgress(course), courseVideoRepository.getLastInfoByCourseId(courseId));
    }

    private List<Section> getSectionList(Long courseId, int weeks) {
        List<Section> sectionList = new ArrayList<>();
        if (weeks == 0) {
            sectionList.add(
                    new Section(courseVideoRepository.getWatchInfoListByCourseIdAndSection(courseId, 0), 0));
        } else {
            for (int section = 1; section <= weeks; section++) {
                sectionList.add(
                        new Section(courseVideoRepository.getWatchInfoListByCourseIdAndSection(courseId, section), section));
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

        learningActivityUpdater.updateCourseVideoStatus(courseVideoEntity, viewDuration, status.isCompleted());

        if (!status.isRewound()) {
            learningActivityUpdater.updateCourseDailyLog(memberId, courseVideoEntity.getCourseId(), status);
            learningActivityUpdater.updateMemberLeaningTime(memberId, status);
            learningActivityUpdater.updateCourseLastViewTime(courseVideoEntity.getCourseId());
        }

        if (status.isCompletedNow()) {
            learningActivityUpdater.updateCourseProgress(memberId, courseVideoEntity.getCourseId());
        }

        if (status.isFullyWatched()) {
            learningActivityUpdater.updateLastViewVideoToNext(courseVideoEntity.getCourseId(), viewDuration);
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

    @Transactional
    public void deleteCourse(Long memberId, Long courseId) {
        courseServiceHelper.validateCourseForMember(memberId, courseId);
        courseRepository.deleteById(courseId);
        courseVideoRepository.deleteByCourseId(courseId);
        lectureRepository.deleteByCourseId(courseId);
        courseQuizRepository.deleteByCourseId(courseId);
    }
}
