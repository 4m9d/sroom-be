package com.m9d.sroom.util;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.common.repository.course.CourseJpaRepository;
import com.m9d.sroom.common.repository.coursedailylog.CourseDailyLogJpaRepository;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizJpaRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoJpaRepository;
import com.m9d.sroom.common.repository.lecture.LectureJpaRepository;
import com.m9d.sroom.common.repository.materialfeedback.MaterialFeedbackJpaRepository;
import com.m9d.sroom.common.repository.member.MemberJpaRepository;
import com.m9d.sroom.common.repository.playlist.PlaylistJpaRepository;
import com.m9d.sroom.common.repository.playlistvideo.PlaylistVideoJpaRepository;
import com.m9d.sroom.common.repository.quiz.QuizJpaRepository;
import com.m9d.sroom.common.repository.video.VideoJpaRepository;
import com.m9d.sroom.util.constant.ContentConstant;
import com.m9d.sroom.video.vo.Video;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Optional;

public class RepositoryTest extends SroomTest {

    @Autowired
    protected EntityManager em;

    @Autowired
    protected MemberJpaRepository memberRepository;

    @Autowired
    protected CourseJpaRepository courseRepository;

    @Autowired
    protected CourseDailyLogJpaRepository courseDailyLogRepository;

    @Autowired
    protected VideoJpaRepository videoRepository;

    @Autowired
    protected PlaylistJpaRepository playlistRepository;

    @Autowired
    protected PlaylistVideoJpaRepository playlistVideoRepository;

    @Autowired
    protected MaterialFeedbackJpaRepository feedbackRepository;

    @Autowired
    protected LectureJpaRepository lectureRepository;

    @Autowired
    protected CourseVideoJpaRepository courseVideoRepository;

    @Autowired
    protected QuizJpaRepository quizRepository;

    @Autowired
    protected CourseQuizJpaRepository courseQuizRepository;

    protected MemberEntity getMemberEntity() {
        Optional<MemberEntity> memberEntityOptional = memberRepository.findById(1L);

        return memberEntityOptional.orElseGet(() -> memberRepository.save(MemberEntity.builder()
                .memberName(TestConstant.MEMBER_PROFILE)
                .memberCode(TestConstant.MEMBER_CODE)
                .build()));
    }

    protected CourseEntity getCourseEntity(MemberEntity member) {
        Optional<CourseEntity> courseEntityOptional = courseRepository.findById(1L);

        return courseEntityOptional.orElseGet(() -> courseRepository.save(CourseEntity.createWithoutSchedule(
                member, TestConstant.COURSE_TITLE, TestConstant.THUMBNAIL)));
    }

    protected CourseDailyLogEntity getCourseDailyLogEntity(MemberEntity member, CourseEntity course) {
        Optional<CourseDailyLogEntity> courseDailyLogEntityOptional = courseDailyLogRepository.findById(1L);

        return courseDailyLogEntityOptional.orElseGet(() -> courseDailyLogRepository.save(
                CourseDailyLogEntity.create(course, TestConstant.LOG_LEARNING_TIME, TestConstant.LOG_QUIZ_COUNT,
                        TestConstant.LOG_LECTURE_COUNT)));
    }

    protected LectureEntity getLectureEntity(Long sourceId) {
        return  lectureRepository.save(LectureEntity.create(getCourseEntity(getMemberEntity()),
                sourceId, false, 1, TestConstant.PLAYLIST_CHANNEL));
    }

    protected VideoEntity getVideoEntity(String videoCode) {
        return videoRepository.save(VideoEntity.create(Video.builder()
                .code(videoCode)
                .title(ContentConstant.VIDEO_TITLE)
                .channel(TestConstant.PLAYLIST_CHANNEL)
                .thumbnail(TestConstant.THUMBNAIL)
                .description(TestConstant.PLAYLIST_DESCRIPTION)
                .duration(100)
                .viewCount(10000L)
                .publishedAt(new Timestamp(System.currentTimeMillis()))
                .language("ko")
                .license("youtube")
                .membership(false)
                .reviewCount(0)
                .rating(0.0)
                .build()));
    }

    protected CourseVideoEntity getCourseVideoEntity(VideoEntity video) {
        return courseVideoRepository.save(
                CourseVideoEntity.createWithoutSummary(getCourseEntity(getMemberEntity()), video,
                        getLectureEntity(video.getVideoId()), 1, 1));
    }
}
