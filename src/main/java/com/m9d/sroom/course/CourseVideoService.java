package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.CourseVideoEntity;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import com.m9d.sroom.lecture.dto.response.VideoWatchInfo;
import com.m9d.sroom.video.VideoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseVideoService {

    private final CourseVideoRepository courseVideoRepository;
    private final VideoService videoService;

    public CourseVideoService(CourseVideoRepository courseVideoRepository, VideoService videoService) {
        this.courseVideoRepository = courseVideoRepository;
        this.videoService = videoService;
    }

    public List<CourseVideo> getCourseVideoList(Long courseId) {
        List<CourseVideo> courseVideoList = new ArrayList<>();

        for (CourseVideoEntity courseVideoEntity : courseVideoRepository.getListByCourseId(courseId)) {
            courseVideoList.add(courseVideoEntity.toCourseVideo());
        }
        return courseVideoList;
    }

    public void saveCourseVideoList(Long memberId, Long courseId, Long lectureId, List<CourseVideo> courseVideoList) {
        for (CourseVideo courseVideo : courseVideoList) {
            courseVideoRepository.save(new CourseVideoEntity(memberId, courseId, lectureId, courseVideo));
        }
    }

    public void updateSections(Long courseId, List<CourseVideo> courseVideoList) {
        for (CourseVideo courseVideo : courseVideoList) {
            CourseVideoEntity courseVideoEntity = courseVideoRepository.getByCourseIdAndIndex(courseId, courseVideo.getVideoIndex());
            courseVideoEntity.setSection(courseVideo.getSection());
            courseVideoRepository.updateById(courseVideoEntity.getCourseVideoId(), courseVideoEntity);
        }
    }

    public Integer[] getDurationList(Long courseId) {
        List<CourseVideoEntity> courseVideoEntityList = courseVideoRepository.getListByCourseId(courseId);
        int lastVideoIndex = courseVideoEntityList.stream()
                .mapToInt(CourseVideoEntity::getVideoIndex)
                .max()
                .orElse(1);

        Integer[] durationList = new Integer[lastVideoIndex + 1];
        for (CourseVideoEntity courseVideoEntity : courseVideoEntityList) {
            durationList[courseVideoEntity.getVideoIndex()] = videoService.getVideoDuration(courseVideoEntity.getVideoId());
        }
        return durationList;
    }

    public LastVideoInfo getLastVideoInfo(Long courseId) {
        return courseVideoRepository.getLastInfoByCourseId(courseId);
    }

    public List<VideoWatchInfo> getWatchInfoList(Long courseId, int section) {
        return courseVideoRepository.getWatchInfoListByCourseIdAndSection(courseId,section);
    }
}
