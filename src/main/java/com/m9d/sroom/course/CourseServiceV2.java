package com.m9d.sroom.course;

import com.m9d.sroom.common.dto.CourseVideoDto;
import com.m9d.sroom.common.object.ContentSaved;
import com.m9d.sroom.common.object.CourseVideo;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.common.dto.LectureDto;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.lecture.LectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.m9d.sroom.course.constant.CourseConstant.ENROLL_LECTURE_INDEX;

@Service
public class CourseServiceV2 {

    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final CourseVideoRepository courseVideoRepository;

    public CourseServiceV2(CourseRepository courseRepository, LectureRepository lectureRepository, CourseVideoRepository courseVideoRepository) {
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.courseVideoRepository = courseVideoRepository;
    }

    @Transactional
    public EnrolledCourseInfo enroll(Long memberId, NewLecture newLecture, boolean useSchedule, ContentSaved contentSaved) {
        EnrollCondition enrollCondition = new EnrollCondition(newLecture, useSchedule);

        Course course = enrollCondition.createCourse(contentSaved);
        CourseDto courseDto = courseRepository.save(new CourseDto(memberId, course));

        LectureDto lectureDto = lectureRepository.save(LectureDto.builder()
                .memberId(memberId)
                .courseId(courseDto.getCourseId())
                .sourceId(course.getFirstSourceId())
                .channel(contentSaved.getChannel())
                .playlist(contentSaved.isPlaylist())
                .lectureIndex(ENROLL_LECTURE_INDEX)
                .build());

        saveCourseVideoDtoList(memberId, courseDto.getCourseId(), lectureDto.getId(), course.getCourseVideoList());

        return EnrolledCourseInfo.builder()
                .title(contentSaved.getTitle())
                .courseId(courseDto.getCourseId())
                .lectureId(lectureDto.getId())
                .build();
    }

    private void saveCourseVideoDtoList(Long memberId, Long courseId, Long lectureId, List<CourseVideo> courseVideoList) {
        for (CourseVideo courseVideo : courseVideoList) {
            courseVideoRepository.save(new CourseVideoDto(memberId, courseId, lectureId, courseVideo));
        }
    }
}
