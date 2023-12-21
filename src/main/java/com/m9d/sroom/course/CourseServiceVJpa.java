package com.m9d.sroom.course;

import com.m9d.sroom.common.LearningActivityUpdaterVJpa;
import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.common.entity.jpa.embedded.LearningStatus;
import com.m9d.sroom.common.repository.course.CourseJpaRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoJpaRepository;
import com.m9d.sroom.common.repository.lecture.LectureJpaRepository;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.exception.CourseNotFoundException;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.search.dto.VideoCompletionStatus;
import com.m9d.sroom.search.dto.response.CourseBrief;
import com.m9d.sroom.search.dto.response.LectureStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.m9d.sroom.search.constant.SearchConstant.LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS;
import static com.m9d.sroom.search.constant.SearchConstant.MINIMUM_VIEW_PERCENT_FOR_COMPLETION;

@Service
@Slf4j
public class CourseServiceVJpa {
    private final CourseJpaRepository courseRepository;
    private final CourseVideoJpaRepository courseVideoRepository;
    private final LectureJpaRepository lectureRepository;
    private final CourseCreatorVJpa courseCreator;
    private final LearningActivityUpdaterVJpa learningActivityUpdater;

    public CourseServiceVJpa(CourseJpaRepository courseRepository, CourseVideoJpaRepository courseVideoRepository, LectureJpaRepository lectureRepository,
                             CourseCreatorVJpa courseCreator, LearningActivityUpdaterVJpa learningActivityUpdater) {
        this.courseRepository = courseRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.lectureRepository = lectureRepository;
        this.courseCreator = courseCreator;
        this.learningActivityUpdater = learningActivityUpdater;
    }

    public MyCourses getMyCourses(MemberEntity memberEntity) {
        List<CourseEntity> latestCourseList = memberEntity.getCoursesByLatestOrder();
        List<CourseInfo> courseInfoList = latestCourseList.stream()
                .map(CourseMapper::getInfoByEntity)
                .collect(Collectors.toList());

        int unfinishedCourseCount = (int) latestCourseList.stream()
                .filter(course -> course.getProgress() < 100)
                .count();
        int courseCount = latestCourseList.size();
        int completionRate = (int) ((float) (courseCount - unfinishedCourseCount) / courseCount);

        return MyCourses.builder()
                .unfinishedCourse(unfinishedCourseCount)
                .completionRate(completionRate)
                .courses(courseInfoList)
                .build();
    }

    @Transactional
    public EnrolledCourseInfo addLecture(CourseEntity courseEntity, EnrollContentInfo contentInfo) {
        log.info("subject = addedLectureInCourse, memberId = {}, scheduleUsed = {}, isPlaylist = {}",
                courseEntity.getMember().getMemberId(), courseEntity.getScheduling().getIsScheduled(),
                contentInfo.isPlaylist());

        courseEntity.addDuration(contentInfo.getTotalContentDuration());

        LectureEntity lectureEntity = lectureRepository.save(LectureEntity.create(courseEntity,
                contentInfo.getContentId(), contentInfo.isPlaylist(), courseEntity.getLastLectureIndex() + 1,
                contentInfo.getChannel()));

        courseCreator.saveCourseVideos(null, false, contentInfo.getInnerContentList(),
                lectureEntity, courseEntity);

        if (courseEntity.getScheduling().getIsScheduled()) {
            courseEntity.reschedule();
        }

        //learningActivityUpdater.updateCourseProgress(memberId, courseRepository.getById(courseId));

        return EnrolledCourseInfo.builder()
                .title(courseEntity.getCourseTitle())
                .courseId(courseEntity.getCourseId())
                .lectureId(lectureEntity.getLectureId())
                .build();
    }

    @Transactional
    public CourseEntity getCourseEntity(MemberEntity memberEntity, Long courseId) {
        CourseEntity courseEntity = courseRepository.findById(courseId)
                .orElseThrow(CourseNotFoundException::new);

        if (!memberEntity.getCourses().contains(courseEntity)) {
            throw new CourseNotMatchException();
        }
        return courseEntity;
    }

    @Transactional
    public CourseDetail getCourseDetail(CourseEntity courseEntity) {
        log.info("courseDetail. video_count = {}, channel_count = {}", courseEntity.getCourseVideos().size(),
                courseEntity.getLectureChannelSet().size());

        return CourseMapper.getDetailByEntity(courseEntity);
    }

    @Transactional
    public LectureStatus updateLectureTime(CourseVideoEntity courseVideoEntity, int viewDuration,
                                           boolean isMarkedAsCompleted) {
        VideoCompletionStatus viewingStatus =
                getVideoCompletionStatus(courseVideoEntity, viewDuration, isMarkedAsCompleted);

        courseVideoEntity.updateStatus(new LearningStatus(viewingStatus.getViewDuration(), viewingStatus.isCompleted(),
                new Timestamp(System.currentTimeMillis()), Math.max(viewDuration,
                courseVideoEntity.getStatus().getMaxDuration())));

        if (!viewingStatus.isRewound()) {
            learningActivityUpdater.updateCourseDailyLog(courseVideoEntity.getCourse(), viewingStatus);
            courseVideoEntity.getMember().addLearningTime(Math.max(viewingStatus.getTimeGap(), 0));
        }

        if (viewingStatus.isCompletedNow() || !viewingStatus.isRewound()) {
            courseVideoEntity.getCourse().updateLastViewTime();
            courseVideoEntity.getCourse().updateProgress();

            if (courseVideoEntity.getCourse().getProgress() == 100) {
                courseVideoEntity.getMember().updateCompletionRate();
            }
        }

        if (viewingStatus.isFullyWatched()) {
            courseVideoEntity.getCourse()
                    .updateLastViewVideoToNext(courseVideoEntity.getSequence().getVideoIndex());
        }

        return LectureStatus.builder()
                .courseVideoId(courseVideoEntity.getCourseVideoId())
                .viewDuration(viewDuration)
                .complete(viewingStatus.isCompleted())
                .build();
    }

    private VideoCompletionStatus getVideoCompletionStatus(CourseVideoEntity courseVideoEntity, int viewDuration, boolean isMarkedAsCompleted) {
        VideoCompletionStatus status = new VideoCompletionStatus();
        status.setRewound(viewDuration - courseVideoEntity.getStatus().getMaxDuration() <= 0);
        status.setCompletedNow(false);

        int videoDuration = courseVideoEntity.getVideo().getContentInfo().getDuration();

        if (courseVideoEntity.getStatus().getIsComplete()) {
            status.setCompleted(true);
        } else {
            status.setCompleted(false);

            boolean currVideoComplete = (viewDuration / (double) videoDuration)
                    > MINIMUM_VIEW_PERCENT_FOR_COMPLETION || isMarkedAsCompleted;
            if (currVideoComplete) {
                status.setCompleted(true);
                status.setCompletedNow(true);
            }
        }

        if (viewDuration >= videoDuration - LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS) {
            status.setFullyWatched(true);
            status.setCompleted(true);
        } else {
            status.setFullyWatched(false);
        }

        if (isMarkedAsCompleted) {
            status.setTimeGap(videoDuration - courseVideoEntity.getStatus().getMaxDuration());
            status.setViewDuration(videoDuration - 1);
            status.setRewound(false);
        } else {
            status.setTimeGap(Math.max(viewDuration - courseVideoEntity.getStatus().getMaxDuration(), 0));
            status.setViewDuration(viewDuration);
        }

        return status;
    }

    public List<CourseBrief> getCourseBriefList(MemberEntity member) {
        return member.getCourses().stream()
                .map(CourseMapper::getBriefByEntity)
                .collect(Collectors.toList());
    }

    public CourseVideoEntity getCourseVideoEntity(MemberEntity memberEntity, Long courseVideoId) {
        CourseVideoEntity courseVideoEntity = courseVideoRepository.findById(courseVideoId)
                .orElseThrow(CourseVideoNotFoundException::new);

        if (memberEntity.getCourses().contains(courseVideoEntity.getCourse())) {
            return courseVideoEntity;
        } else {
            throw new CourseNotMatchException();
        }
    }
}
