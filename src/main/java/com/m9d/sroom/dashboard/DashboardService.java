package com.m9d.sroom.dashboard;

import com.m9d.sroom.common.entity.jdbctemplate.*;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.common.repository.quiz.QuizRepository;
import com.m9d.sroom.common.repository.quizoption.QuizOptionRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.course.CourseServiceHelper;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.dashboard.dto.response.Dashboard;
import com.m9d.sroom.dashboard.dto.response.DashboardQuizData;
import com.m9d.sroom.dashboard.dto.response.LearningHistory;
import com.m9d.sroom.common.repository.course.CourseRepository;
import com.m9d.sroom.common.repository.coursedailylog.CourseDailyLogRepository;
import com.m9d.sroom.common.repository.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.m9d.sroom.dashboard.constant.DashboardConstant.*;

@Service
@Slf4j
public class DashboardService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final CourseServiceHelper courseServiceHelper;
    private final CourseDailyLogRepository courseDailyLogRepository;
    private final CourseQuizRepository courseQuizRepository;
    private final QuizRepository quizRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final VideoRepository videoRepository;

    public DashboardService(MemberRepository memberRepository, CourseRepository courseRepository,
                            CourseServiceHelper courseServiceHelper, CourseDailyLogRepository courseDailyLogRepository,
                            CourseQuizRepository courseQuizRepository, QuizRepository quizRepository,
                            QuizOptionRepository quizOptionRepository, VideoRepository videoRepository) {
        this.memberRepository = memberRepository;
        this.courseRepository = courseRepository;
        this.courseServiceHelper = courseServiceHelper;
        this.courseDailyLogRepository = courseDailyLogRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.quizRepository = quizRepository;
        this.quizOptionRepository = quizOptionRepository;
        this.videoRepository = videoRepository;
    }


    public Dashboard getDashboard(Long memberId) {

        List<CourseEntity> courseList = courseRepository.getLatestOrderByMemberId(memberId);
        List<CourseInfo> latestCourses = courseServiceHelper.getCourseInfoList(courseList);
        List<CourseInfo> latestLectures = new ArrayList<>();

        if (latestCourses.size() >= 2) {
            latestLectures = latestCourses.subList(0, 2);
        } else {
            latestLectures.addAll(latestCourses);
        }

        List<CourseDailyLogEntity> courseDailyLogList = courseDailyLogRepository.getDateDataByMemberId(memberId);
        MemberEntity member = memberRepository.getById(memberId);
        String motivation = getMotivation(courseDailyLogList, member);

        int correctnessRate = (int) (((float) member.getTotalCorrectCount() / member.getTotalSolvedCount()) * 100);

        List<LearningHistory> learningHistoryList = new ArrayList<>();
        for (CourseDailyLogEntity courseDailyLog : courseDailyLogList) {
            learningHistoryList.add(LearningHistory.builder()
                    .date(new SimpleDateFormat("yyyy-MM-dd").format(courseDailyLog.getDailyLogDate()))
                    .lectureCount(courseDailyLog.getLectureCount())
                    .learningTime(courseDailyLog.getLearningTime())
                    .quizCount(courseDailyLog.getQuizCount())
                    .build());
        }


        Dashboard dashboardInfo = Dashboard.builder()
                .correctnessRate(correctnessRate)
                .completionRate(member.getCompletionRate())
                .totalLearningTime(member.getTotalLearningTime())
                .motivation(motivation)
                .latestLectures(latestLectures)
                .learningHistories(learningHistoryList)
                .wrongQuizzes(getWrongQuizzes(memberId))
                .build();

        return dashboardInfo;
    }

    public List<DashboardQuizData> getWrongQuizzes(Long memberId) {
        List<DashboardQuizData> wrongQuizzes = new ArrayList<>();
        List<CourseQuizEntity> courseQuizzes = courseQuizRepository.getWrongQuizListByMemberId(memberId, WRONG_QUIZZES_COUNT);

        for (CourseQuizEntity courseQuiz : courseQuizzes) {
            VideoEntity video = videoRepository.getById(courseQuiz.getVideoId());
            QuizEntity quiz = quizRepository.getById(courseQuiz.getQuizId());
            List<QuizOptionEntity> quizOptions = quizOptionRepository.getListByQuizId(courseQuiz.getQuizId());

            wrongQuizzes.add(DashboardQuizData.builder()
                    .quizQuestion(quiz.getQuestion())
                    .quizAnswer(quizOptions.get(quiz.getChoiceAnswer() - 1).getOptionText())
                    .videoTitle(video.getTitle())
                    .submittedAt(new SimpleDateFormat("yyyy-MM-dd").format(courseQuiz.getSubmittedTime()))
                    .build());
        }
        return wrongQuizzes;
    }

    public String getMotivation(List<CourseDailyLogEntity> learningHistories, MemberEntity member) {
        List<String> motivationList = new ArrayList<>();
        motivationList.addAll(MOTIVATION_GENERAL);

        int consecutiveLearningDays = getConsecutiveLearningDay(learningHistories);
        int totalLearningTime = member.getTotalLearningTime() / SECONDS_PER_HOUR;
        int leftTargetTime = TARGET_TIME_INTERVAL - (totalLearningTime % TARGET_TIME_INTERVAL);
        int targetTime = TARGET_TIME_INTERVAL * (totalLearningTime / TARGET_TIME_INTERVAL + 1);

        motivationList.add(member.getMemberName() + MOTIVATION_INDUCE_REVIEW);

        motivationList.add(leftTargetTime + MOTIVATION_TOTAL_LEARNING_TIME_PREFIX + targetTime + MOTIVATION_TOTAL_LEARNING_TIME_SUFFIX);

        if (consecutiveLearningDays > 0) {
            motivationList.add(consecutiveLearningDays + MOTIVATION_CONSECUTIVE_LEARNING);
        } else {
            motivationList.add(MOTIVATION_RESTART_LEARNING);
        }

        Collections.shuffle(motivationList);

        return motivationList.get(0);
    }

    public int getConsecutiveLearningDay(List<CourseDailyLogEntity> courseDailyLogList) {

        int consecutiveCount = 0;
        LocalDate beforeDate = LocalDate.now();

        for (int i = 0; i < courseDailyLogList.size(); i++) {

            LocalDate nowDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(courseDailyLogList.get(i).getDailyLogDate()));

            if (isConsecutive(beforeDate, nowDate)) {
                consecutiveCount++;
                beforeDate = nowDate;
            } else {
                break;
            }

        }
        return consecutiveCount;
    }

    public boolean isConsecutive(LocalDate beforeDate, LocalDate nowDate) {
        Period dateDiff = Period.between(nowDate, beforeDate);
        return (dateDiff.getYears() == 0 && dateDiff.getMonths() == 0 && dateDiff.getDays() == 1);
    }

}
