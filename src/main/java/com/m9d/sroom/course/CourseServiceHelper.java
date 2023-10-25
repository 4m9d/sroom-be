package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.CourseEntity;
import com.m9d.sroom.common.entity.CourseQuizEntity;
import com.m9d.sroom.common.entity.CourseVideoEntity;
import com.m9d.sroom.common.repository.course.CourseRepository;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.lecture.LectureRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.course.vo.Course;
import com.m9d.sroom.course.vo.CourseVideo;
import com.m9d.sroom.search.dto.VideoCompletionStatus;
import com.m9d.sroom.material.exception.CourseQuizNotFoundException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceHelper {

    private final CourseRepository courseRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final VideoRepository videoRepository;
    private final CourseQuizRepository courseQuizRepository;
    private final LectureRepository lectureRepository;

    public CourseServiceHelper(CourseRepository courseRepository, CourseVideoRepository courseVideoRepository,
                               VideoRepository videoRepository, CourseQuizRepository courseQuizRepository,
                               LectureRepository lectureRepository) {
        this.courseRepository = courseRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.videoRepository = videoRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.lectureRepository = lectureRepository;
    }

    public Course getCourse(Long courseId) {
        return courseRepository.getById(courseId).toCourse(getCourseVideoList(courseId));
    }

    public List<CourseVideo> getCourseVideoList(Long courseId) {
        List<CourseVideo> courseVideoList = new ArrayList<>();

        for (CourseVideoEntity courseVideoEntity : courseVideoRepository.getListByCourseId(courseId)) {
            courseVideoList.add(courseVideoEntity.toCourseVideo());
        }
        return courseVideoList;
    }

    public boolean validateCourseForMember(Long memberIdFromRequest, Long courseId) {
        return memberIdFromRequest.equals(courseRepository.getById(courseId).getMemberId());
    }

    public void validateCourseQuizForMember(Long memberId, Long courseQuizId) {
        CourseQuizEntity courseQuiz = courseQuizRepository.findById(courseQuizId)
                .orElseThrow(CourseQuizNotFoundException::new);

        Long memberIdByCourse = courseRepository.getById(courseQuiz
                        .getCourseId())
                .getMemberId();
        if (!memberId.equals(memberIdByCourse)) {
            throw new CourseNotMatchException();
        }
    }

    public Integer[] getDurationArray(Long courseId) {
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

    public void updateCourseVideoSection(Long courseId, int videoIndex, int section) {
        CourseVideoEntity courseVideoEntity = courseVideoRepository.getByCourseIdAndIndex(courseId, videoIndex);
        courseVideoEntity.setSection(section);
        courseVideoRepository.updateById(courseVideoEntity.getCourseVideoId(), courseVideoEntity);
    }

    public int getCourseProgress(Course course) {
        if (course.getCourseVideoList().size() == 1) {
            return (course.getSumOfMaxDuration() * 100) /
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

    public void updateCourseVideoSummaryId(Long courseVideoId, Long summaryId) {
        CourseVideoEntity courseVideoEntity = courseVideoRepository.getById(courseVideoId);
        courseVideoEntity.setSummaryId(summaryId);
        courseVideoRepository.updateById(courseVideoId, courseVideoEntity);
    }

    public List<CourseInfo> getCourseInfoList(List<CourseEntity> latestCourseList) {
        List<CourseInfo> courseInfoList = new ArrayList<>();

        for (CourseEntity courseEntity : latestCourseList) {
            Long courseId = courseEntity.getCourseId();
            Course course = getCourse(courseId);

            int videoCount = course.getCourseVideoList().size();
            int completedVideoCount = (int)course.getCourseVideoList().stream()
                    .filter(CourseVideo::isComplete)
                    .count();


            CourseInfo courseInfo = CourseInfo.builder()
                    .courseId(courseId)
                    .courseTitle(course.getTitle())
                    .thumbnail(course.getThumbnail())
                    .channels(String.join(", ", lectureRepository.getChannelSetByCourseId(courseId)))
                    .lastViewTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(course.getLastViewTime()))
                    .totalVideoCount(videoCount)
                    .completedVideoCount(completedVideoCount)
                    .progress(getCourseProgress(course))
                    .build();

            courseInfoList.add(courseInfo);
        }

        return courseInfoList;
    }

    public int getUnfinishedCourseCount(List<CourseEntity> courseInfoList) {

        int unfinishedCourseCount = 0;

        for (int i = 0; i < courseInfoList.size(); i++) {
            if (courseInfoList.get(i).getProgress() < 100) {
                unfinishedCourseCount++;
            }
        }

        return unfinishedCourseCount;
    }
}
