package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.CourseVideoEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.embedded.Scheduling;
import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.vo.Course;
import com.m9d.sroom.search.dto.response.CourseBrief;
import com.m9d.sroom.search.dto.response.Section;
import com.m9d.sroom.search.dto.response.VideoInfo;
import com.m9d.sroom.search.dto.response.VideoWatchInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseMapper {

    public static CourseBrief getBriefByEntity(CourseEntity courseEntity) {
        return CourseBrief.builder()
                .courseId(courseEntity.getCourseId())
                .courseTitle(courseEntity.getCourseTitle())
                .totalVideoCount(courseEntity.getCourseVideos().size())
                .build();
    }

    public static CourseInfo getInfoByEntity(CourseEntity courseEntity) {
        return CourseInfo.builder()
                .courseId(courseEntity.getCourseId())
                .courseTitle(courseEntity.getCourseTitle())
                .thumbnail(courseEntity.getThumbnail())
                .channels(String.join(", ", courseEntity.getLectureChannelSet()))
                .lastViewTime(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(courseEntity.getLastViewTime()))
                .totalVideoCount(courseEntity.getCourseVideos().size())
                .completedVideoCount(courseEntity.countCompletedVideo())
                .duration(courseEntity.getSumOfMaxDuration())
                .progress(courseEntity.getProgress())
                .build();
    }

    public static CourseDetail getDetailByEntity(CourseEntity courseEntity) {
        return CourseDetail.builder()
                .courseId(courseEntity.getCourseId())
                .useSchedule(courseEntity.getScheduling().getIsScheduled())
                .thumbnail(courseEntity.getThumbnail())
                .courseTitle(courseEntity.getCourseTitle())
                .channels(String.join(", ", courseEntity.getLectureChannelSet()))
                .courseDuration(courseEntity.getCourseDuration())
                .currentDuration(courseEntity.getSumOfMaxDuration())
                .totalVideoCount(courseEntity.getCourseVideos().size())
                .completedVideoCount(courseEntity.countCompletedVideo())
                .progress(courseEntity.getProgress())
                .lastViewVideo(getVideoInfoByCourseVideo(courseEntity.getLastCourseVideo()))
                .sections(getSectionList(courseEntity))
                .build();

    }

    private static List<Section> getSectionList(CourseEntity courseEntity) {
        List<Section> sectionList = new ArrayList<>();
        if (courseEntity.getScheduling().getWeeks() == 0) {
            sectionList.add(
                    new Section(CourseMapper.getWatchInfoListBySection(courseEntity, 0), 0));
        } else {
            for (int section = 1; section <= courseEntity.getScheduling().getWeeks(); section++) {
                sectionList.add(
                        new Section(CourseMapper.getWatchInfoListBySection(courseEntity, section), section));
            }
        }
        return sectionList;
    }

    public static List<VideoWatchInfo> getWatchInfoListBySection(CourseEntity courseEntity, int section) {
        return courseEntity.getCourseVideos().stream()
                .filter(courseVideo -> courseVideo.getSequence().getSection() == section)
                .map(CourseMapper::getWatchInfo)
                .collect(Collectors.toList());
    }

    public static VideoWatchInfo getWatchInfo(CourseVideoEntity courseVideoEntity) {
        return VideoWatchInfo.builder()
                .videoId(courseVideoEntity.getVideo().getVideoId())
                .videoCode(courseVideoEntity.getVideo().getVideoCode())
                .channel(courseVideoEntity.getVideo().getContentInfo().getChannel())
                .videoTitle(courseVideoEntity.getVideo().getContentInfo().getTitle())
                .completed(courseVideoEntity.getStatus().getIsComplete())
                .lastViewDuration(courseVideoEntity.getStatus().getStartTime())
                .videoDuration(courseVideoEntity.getVideo().getContentInfo().getDuration())
                .courseVideoId(courseVideoEntity.getCourseVideoId())
                .maxDuration(courseVideoEntity.getStatus().getMaxDuration())
                .build();
    }

    public static VideoInfo getVideoInfoByCourseVideo(CourseVideoEntity courseVideoEntity) {
        return VideoInfo.builder()
                .videoId(courseVideoEntity.getVideo().getVideoId())
                .courseVideoId(courseVideoEntity.getCourseVideoId())
                .videoTitle(courseVideoEntity.getVideo().getContentInfo().getTitle())
                .channel(courseVideoEntity.getVideo().getContentInfo().getChannel())
                .lastViewDuration(courseVideoEntity.getStatus().getStartTime())
                .build();
    }
}
