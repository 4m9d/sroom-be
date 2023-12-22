package com.m9d.sroom.dashboard;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.common.repository.member.MemberJpaRepository;
import com.m9d.sroom.course.CourseMapper;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.dashboard.dto.response.Dashboard;
import com.m9d.sroom.dashboard.dto.response.DashboardQuizData;
import com.m9d.sroom.dashboard.dto.response.LearningHistory;
import groovy.util.logging.Slf4j;
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
public class DashboardServiceVJpa {

    private final MemberJpaRepository memberRepository;

    public DashboardServiceVJpa(MemberJpaRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    public Dashboard getDashboard(Long memberId) {

        MemberEntity member = memberRepository.getById(memberId);
        List<CourseEntity> courseList = member.getCoursesByLatestOrder();
        List<CourseInfo> latestCourses = new ArrayList<>();

        for (CourseEntity course: courseList) {
            latestCourses.add(CourseMapper.getInfoByEntity(course));
        }

        if (latestCourses.size() >= 2) {
            latestCourses = latestCourses.subList(0, 2);
        }

        List<CourseDailyLogEntity> courseDailyLogList = member.getDailyLogs();
        String motivation = getMotivation(courseDailyLogList, member);

        int correctnessRate = (int) (((float) member.getStats().getTotalCorrectCount() / member.getStats().getTotalSolvedCount()) * 100);

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
                .completionRate(member.getStats().getCompletionRate())
                .totalLearningTime(member.getStats().getTotalLearningTime())
                .motivation(motivation)
                .latestLectures(latestCourses)
                .learningHistories(learningHistoryList)
                .wrongQuizzes(getWrongQuizzes(member))
                .build();

        return dashboardInfo;
    }

    public List<DashboardQuizData> getWrongQuizzes(MemberEntity member) {
        List<DashboardQuizData> wrongQuizzes = new ArrayList<>();
        List<CourseQuizEntity> courseQuizzes = member.getWrongQuizList(10);

        for (CourseQuizEntity courseQuiz : courseQuizzes) {
            VideoEntity video = courseQuiz.getVideo();
            QuizEntity quiz = courseQuiz.getQuiz();
            List<QuizOptionEntity> quizOptions = courseQuiz.getQuiz().getQuizOptions();

            wrongQuizzes.add(DashboardQuizData.builder()
                    .quizQuestion(quiz.getQuestion())
                    .quizAnswer(quizOptions.get(quiz.getChoiceAnswer() - 1).getOptionText())
                    .videoTitle(video.getContentInfo().getTitle())
                    .submittedAt(new SimpleDateFormat("yyyy-MM-dd").format(courseQuiz.getGrading().getSubmittedTime()))
                    .build());
        }
        return wrongQuizzes;
    }

    public String getMotivation(List<CourseDailyLogEntity> learningHistories, MemberEntity member) {
        List<String> motivationList = new ArrayList<>();
        motivationList.addAll(MOTIVATION_GENERAL);

        int consecutiveLearningDays = getConsecutiveLearningDay(learningHistories);
        int totalLearningTime = member.getStats().getTotalLearningTime() / SECONDS_PER_HOUR;
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
