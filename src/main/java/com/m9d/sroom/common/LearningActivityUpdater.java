package com.m9d.sroom.common;

import com.m9d.sroom.common.entity.CourseDailyLogEntity;
import com.m9d.sroom.common.entity.CourseEntity;
import com.m9d.sroom.common.entity.CourseVideoEntity;
import com.m9d.sroom.common.entity.MemberEntity;
import com.m9d.sroom.common.repository.course.CourseRepository;
import com.m9d.sroom.common.repository.coursedailylog.CourseDailyLogRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.member.MemberRepository;
import com.m9d.sroom.course.CourseServiceHelper;
import com.m9d.sroom.search.dto.VideoCompletionStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.m9d.sroom.search.constant.SearchConstant.LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS;

@Service
public class LearningActivityUpdater {
    private final CourseRepository courseRepository;
    private final CourseDailyLogRepository courseDailyLogRepository;
    private final MemberRepository memberRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final CourseServiceHelper courseServiceHelper;

    public LearningActivityUpdater(CourseRepository courseRepository, CourseDailyLogRepository courseDailyLogRepository,
                                   MemberRepository memberRepository, CourseVideoRepository courseVideoRepository,
                                   CourseServiceHelper courseServiceHelper) {
        this.courseRepository = courseRepository;
        this.courseDailyLogRepository = courseDailyLogRepository;
        this.memberRepository = memberRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.courseServiceHelper = courseServiceHelper;
    }

    public void updateCourseDailyLog(Long memberId, Long courseId, VideoCompletionStatus status) {
        Optional<CourseDailyLogEntity> dailyLogOptional = courseDailyLogRepository.findByCourseIdAndDate(courseId,
                Date.valueOf(LocalDate.now()));
        int learningTimeToAdd = Math.max(status.getTimeGap(), 0);
        int lectureCountToAdd = status.isCompletedNow() ? 1 : 0;

        if (dailyLogOptional.isEmpty()) {
            CourseDailyLogEntity initialDailyLog = CourseDailyLogEntity.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .dailyLogDate(Date.valueOf(LocalDate.now()))
                    .learningTime(learningTimeToAdd)
                    .quizCount(0)
                    .lectureCount(lectureCountToAdd)
                    .build();
            courseDailyLogRepository.save(initialDailyLog);
        } else {
            CourseDailyLogEntity dailyLog = dailyLogOptional.get();
            dailyLog.setLearningTime(dailyLog.getLearningTime() + learningTimeToAdd);
            dailyLog.setLectureCount(dailyLog.getLectureCount() + lectureCountToAdd);
            courseDailyLogRepository.updateById(dailyLog.getCourseDailyLogId(), dailyLog);
        }
    }

    public void updateMemberLeaningTime(Long memberId, VideoCompletionStatus status) {
        MemberEntity member = memberRepository.getById(memberId);
        member.setTotalLearningTime(Math.max(status.getTimeGap(), 0) + member.getTotalLearningTime());
        memberRepository.updateById(memberId, member);
    }

    public void updateCourseProgress(Long memberId, Long courseId) {
        CourseEntity courseEntity = courseRepository.getById(courseId);
        courseEntity.setProgress(courseEntity.toCourse(courseServiceHelper.getCourseVideoList(courseId))
                .getCompletionRatio());
        courseRepository.updateById(courseId, courseEntity);

        if (courseEntity.getProgress() == 100) {
            MemberEntity member = memberRepository.getById(memberId);
            member.setCompletionRate(memberRepository.countCompletedCourseById(memberId) * 100
                    / memberRepository.countCourseById(memberId));
            memberRepository.updateById(memberId, member);
        }
    }

    public void updateCourseVideoStatus(CourseVideoEntity courseVideoEntity, int viewDuration, boolean completed) {
        courseVideoEntity.setMaxDuration(Math.max(viewDuration, courseVideoEntity.getMaxDuration()));
        courseVideoEntity.setStartTime(viewDuration);
        courseVideoEntity.setComplete(completed);
        courseVideoEntity.setLastViewTime(new Timestamp(System.currentTimeMillis()));
        courseVideoRepository.updateById(courseVideoEntity.getCourseVideoId(), courseVideoEntity);
    }

    public void updateCourseLastViewTime(Long courseId) {
        CourseEntity course = courseRepository.getById(courseId);
        course.setLastViewTime(new Timestamp(System.currentTimeMillis()));
        courseRepository.updateById(courseId, course);
    }

    public void updateLastViewVideoToNext(Long courseId, int videoIndex) {
        Optional<CourseVideoEntity> courseVideoOptional = courseVideoRepository
                .findByCourseIdAndPrevIndex(courseId, videoIndex);

        if (courseVideoOptional.isPresent()) {
            CourseVideoEntity courseVideo = courseVideoOptional.get();
            courseVideo.setLastViewTime(
                    Timestamp.valueOf(LocalDateTime.now().plusSeconds(LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS)));
            courseVideoRepository.updateById(courseVideo.getCourseVideoId(), courseVideo);
        }
    }

    public void updateDailyLogQuizCount(Long memberId, Long courseId, int submittedQuizCount) {
        Optional<CourseDailyLogEntity> courseDailyLogOptional = courseDailyLogRepository
                .findByCourseIdAndDate(courseId, Date.valueOf(LocalDate.now()));
        if (courseDailyLogOptional.isEmpty()) {
            courseDailyLogRepository.save(CourseDailyLogEntity.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .dailyLogDate(Date.valueOf(LocalDate.now()))
                    .learningTime(0)
                    .quizCount(submittedQuizCount)
                    .lectureCount(0)
                    .build());
        } else {
            CourseDailyLogEntity dailyLog = courseDailyLogOptional.get();
            dailyLog.setQuizCount(dailyLog.getQuizCount() + submittedQuizCount);
            courseDailyLogRepository.updateById(dailyLog.getCourseDailyLogId(), dailyLog);
        }
    }

    public void updateMemberQuizCount(Long memberId, int submittedQuizCount, int submittedCorrectQuizCount) {
        MemberEntity memberEntity = memberRepository.getById(memberId);
        memberEntity.setTotalSolvedCount(submittedQuizCount + memberEntity.getTotalSolvedCount());
        memberEntity.setTotalCorrectCount(submittedCorrectQuizCount + memberEntity.getTotalCorrectCount());
        memberRepository.updateById(memberEntity.getMemberId(), memberEntity);
    }
}
