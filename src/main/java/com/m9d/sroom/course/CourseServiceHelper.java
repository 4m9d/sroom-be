package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.CourseDailyLogEntity;
import com.m9d.sroom.common.entity.CourseEntity;
import com.m9d.sroom.common.entity.CourseVideoEntity;
import com.m9d.sroom.common.entity.MemberEntity;
import com.m9d.sroom.common.repository.course.CourseRepository;
import com.m9d.sroom.common.repository.coursedailylog.CourseDailyLogRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.lecture.dto.VideoCompletionStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceHelper {

    private final CourseRepository courseRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final VideoRepository videoRepository;

    public CourseServiceHelper(CourseRepository courseRepository, CourseVideoRepository courseVideoRepository, VideoRepository videoRepository) {
        this.courseRepository = courseRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.videoRepository = videoRepository;
    }

    public List<CourseVideo> getCourseVideoList(Long courseId) {
        List<CourseVideo> courseVideoList = new ArrayList<>();

        for (CourseVideoEntity courseVideoEntity : courseVideoRepository.getListByCourseId(courseId)) {
            courseVideoList.add(courseVideoEntity.toCourseVideo());
        }
        return courseVideoList;
    }

    public Integer[] getDurationList(Long courseId) {
        List<CourseVideoEntity> courseVideoEntityList = courseVideoRepository.getListByCourseId(courseId);
        int lastVideoIndex = courseVideoEntityList.stream()
                .mapToInt(CourseVideoEntity::getVideoIndex)
                .max()
                .orElse(1);

        Integer[] durationList = new Integer[lastVideoIndex + 1];
        for (CourseVideoEntity courseVideoEntity : courseVideoEntityList) {
            durationList[courseVideoEntity.getVideoIndex()] = videoRepository.getById(courseVideoEntity.getVideoId())
                    .getDuration();
        }
        return durationList;
    }

    public void updateSections(Long courseId, List<CourseVideo> courseVideoList) {
        for (CourseVideo courseVideo : courseVideoList) {
            CourseVideoEntity courseVideoEntity = courseVideoRepository.getByCourseIdAndIndex(courseId, courseVideo.getVideoIndex());
            courseVideoEntity.setSection(courseVideo.getSection());
            courseVideoRepository.updateById(courseVideoEntity.getCourseVideoId(), courseVideoEntity);
        }
    }

    private int getCourseProgress(Course course) {
        if (course.getCourseVideoList().size() == 1) {
            return (course.getDuration() * 100) /
                    videoRepository.getById(course.getCourseVideoList().get(0).getVideoId()).getDuration();
        } else {
            return course.getCompletionRatio();
        }
    }

    public void updateCourseEntity(Long courseId, Course course) {
        CourseEntity courseEntity = courseRepository.getById(courseId);
        courseEntity.setCourseTitle(course.getTitle());
        courseEntity.setThumbnail(course.getThumbnail());
        courseEntity.setDuration(course.getDuration());
        courseEntity.setScheduled(course.isScheduled());
        courseEntity.setWeeks(course.getWeeks());
        courseEntity.setExpectedEndDate(course.getExpectedEndDate());
        courseRepository.updateById(courseId, courseEntity);
    }

    public CourseVideoEntity getCourseVideo(Long memberId, Long courseVideoId) {
        CourseVideoEntity courseVideo = courseVideoRepository.findById(courseVideoId)
                .orElseThrow(CourseVideoNotFoundException::new);

        if (!courseVideo.getMemberId().equals(memberId)) {
            throw new CourseNotMatchException();
        }

        return courseVideo;
    }

    public VideoCompletionStatus getVideoCompletionStatus(CourseVideo courseVideo, int viewDuration,
                                                          boolean isMarkedAsCompleted) {
        return courseVideo.getCompletionStatus(viewDuration,
                videoRepository.getById(courseVideo.getVideoId()).getDuration(), isMarkedAsCompleted);
    }
}
