package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.common.repository.course.CourseJpaRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoJpaRepository;
import com.m9d.sroom.common.repository.lecture.LectureJpaRepository;
import com.m9d.sroom.common.repository.video.VideoJpaRepository;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.InnerContent;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.lecture.LectureConstant;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CourseCreatorVJpa {

    private final CourseJpaRepository courseRepository;
    private final CourseVideoJpaRepository courseVideoRepository;
    private final VideoJpaRepository videoRepository;
    private final LectureJpaRepository lectureRepository;

    public CourseCreatorVJpa(CourseJpaRepository courseRepository, CourseVideoJpaRepository courseVideoRepository, VideoJpaRepository videoRepository, LectureJpaRepository lectureRepository) {
        this.courseRepository = courseRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.videoRepository = videoRepository;
        this.lectureRepository = lectureRepository;
    }

    @Transactional
    public EnrolledCourseInfo enroll(MemberEntity memberEntity, NewLecture newLecture, boolean useSchedule,
                                     EnrollContentInfo contentInfo) {
        log.info("subject = courseCreated, memberId = {}, scheduleUsed = {}, isPlaylist = {}",
                memberEntity.getMemberId(), useSchedule, ValidateUtil.checkIfPlaylist(newLecture.getLectureCode()));

        CourseEntity courseEntity = createCourseEntity(newLecture, useSchedule, contentInfo,
                memberEntity);

        LectureEntity lectureEntity = lectureRepository.save(LectureEntity.create(courseEntity,
                contentInfo.getContentId(), contentInfo.isPlaylist(), LectureConstant.DEFAULT_INDEX,
                contentInfo.getChannel()));
        saveCourseVideos(newLecture.getScheduling(), useSchedule, contentInfo.getInnerContentList(),
                lectureEntity, courseEntity);

        return EnrolledCourseInfo.builder()
                .title(courseEntity.getCourseTitle())
                .courseId(courseEntity.getCourseId())
                .lectureId(lectureEntity.getLectureId())
                .build();
    }

    private CourseEntity createCourseEntity(NewLecture newLecture, boolean useSchedule, EnrollContentInfo contentInfo,
                                            MemberEntity memberEntity) {
        CourseEntity courseEntity;
        if (useSchedule) {
            log.info("subject = schedule, dailyTargetTimeInMinute = {}, weeks = {}", newLecture.getDailyTargetTime(),
                    newLecture.getScheduling().size());
            courseEntity = CourseEntity.createWithSchedule(memberEntity, contentInfo.getTitle(),
                    contentInfo.getThumbnail(), newLecture.getScheduling().size(),
                    DateUtil.convertStringToDate(newLecture.getExpectedEndDate()), newLecture.getDailyTargetTime());
        } else {
            courseEntity = CourseEntity.createWithoutSchedule(memberEntity, contentInfo.getTitle(),
                    contentInfo.getThumbnail());
        }
        courseRepository.save(courseEntity);
        return courseEntity;
    }

    public void saveCourseVideos(List<Integer> scheduling, boolean useSchedule, List<InnerContent> innerContentList,
                                 LectureEntity lectureEntity, CourseEntity courseEntity) {
        int[] sectionArr = getSectionArray(useSchedule, innerContentList.size(), scheduling);
        int index = courseEntity.getCourseVideos().stream()
                .mapToInt(courseVideoEntity -> courseVideoEntity.getSequence().getVideoIndex())
                .max()
                .orElse(0) + 1;
        for (int i = 0; i < innerContentList.size(); i++) {
            VideoEntity videoEntity = videoRepository.getById(innerContentList.get(i).getContentId());
            courseVideoRepository.save(CourseVideoEntity.create(lectureEntity.getCourse(), videoEntity, lectureEntity,
                    videoEntity.getSummary(), sectionArr[i], index++));
        }
    }

    private int[] getSectionArray(boolean useSchedule, int size, List<Integer> scheduling) {
        int[] sectionArr = new int[size];

        if (useSchedule) {
            List<Integer> sectionList = new ArrayList<>();

            for (int i = 0; i < scheduling.size(); i++) {
                int number = i + 1;
                int repetitions = scheduling.get(i);
                for (int j = 0; j < repetitions; j++) {
                    sectionList.add(number);
                }
            }
            return sectionList.stream()
                    .mapToInt(i -> i)
                    .toArray();
        } else {
            return sectionArr; // 일정관리를 사용하지 않으면 모든 섹션이 디폴트 0으로 저장됩니다.
        }
    }
}
