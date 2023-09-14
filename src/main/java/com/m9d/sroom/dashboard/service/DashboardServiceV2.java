package com.m9d.sroom.dashboard.service;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.dashboard.dto.response.Dashboard;
import com.m9d.sroom.dashboard.dto.response.DashboardMemberData;
import com.m9d.sroom.dashboard.dto.response.LearningHistory;
import com.m9d.sroom.dashboard.repository.DashboardRepository;
import com.m9d.sroom.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.m9d.sroom.dashboard.constant.DashboardConstant.*;

@Slf4j
@RequiredArgsConstructor
public class DashboardServiceV2 {

    private final DashboardRepository dashboardRepository;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    public Dashboard getDashboard(Long memberId) {

        List<CourseInfo> latestCourses = dashboardRepository.getLatestCourseListByMemberId(memberId);
        List<LearningHistory> learningHistories = dashboardRepository.getLearningHistoryListByMemberId(memberId);
        DashboardMemberData dashboardMemberData = dashboardRepository.getDashboardMemberDataByMemberId(memberId);
        String motivation = getMotivation(learningHistories, dashboardMemberData, memberId);

        int correctnessRate = (int)(((float) dashboardMemberData.getTotalCorrectCount() / dashboardMemberData.getTotalSolvedCount()) * 100);

        for(int i = 0; i < latestCourses.size(); i++) {

            Long courseId = latestCourses.get(i).getCourseId();
            HashSet<String> channels = courseRepository.getChannelSetByCourseId(courseId);
            int lectureCount = courseRepository.getTotalLectureCountByCourseId(courseId);
            int completedLectureCount = courseRepository.getCompletedVideoCountByCourseId(courseId);


            latestCourses.get(i).setChannels(String.join(", ", channels));
            latestCourses.get(i).setTotalVideoCount(lectureCount);
            latestCourses.get(i).setCompletedVideoCount(completedLectureCount);
        }



        Dashboard dashboardInfo = Dashboard.builder()
                .correctnessRate(correctnessRate)
                .completionRate(dashboardMemberData.getCompletionRate())
                .totalLearningTime(dashboardMemberData.getTotalLearningTime())
                .motivation(motivation)
                .latestLectures(latestCourses)
                .learningHistories(learningHistories)
                .build();

        return dashboardInfo;
    }

    public String getMotivation(List<LearningHistory> learningHistories, DashboardMemberData dashboardMemberData, Long memberId) {
        List<String> motivationList = new ArrayList<>();
        motivationList.addAll(MOTIVATION_GENERAL);

        String memberName = memberRepository.getMemberNameById(memberId);
        int consecutiveLearningDays = getConsecutiveLearningDay(learningHistories);
        int totalLearningTime = dashboardMemberData.getTotalLearningTime() / SECONDS_PER_HOUR;
        int leftTargetTime = TARGET_TIME_INTERVAL - (totalLearningTime % TARGET_TIME_INTERVAL);
        int targetTime = TARGET_TIME_INTERVAL * (totalLearningTime / TARGET_TIME_INTERVAL + 1);

        motivationList.add(memberName + MOTIVATION_INDUCE_REVIEW);

        motivationList.add(leftTargetTime + MOTIVATION_TOTAL_LEARNING_TIME_PREFIX + targetTime + MOTIVATION_TOTAL_LEARNING_TIME_SUFFIX);

        if(consecutiveLearningDays > 0) {
            motivationList.add(consecutiveLearningDays + MOTIVATION_CONSECUTIVE_LEARNING);
        }
        else {
            motivationList.add(MOTIVATION_RESTART_LEARNING);
        }

        Collections.shuffle(motivationList);

        return motivationList.get(0);
    }

    public int getConsecutiveLearningDay(List<LearningHistory> learningHistories) {

        int consecutiveCount = 0;
        LocalDate beforeDate = LocalDate.now();

        for(int i = 0; i < learningHistories.size(); i++) {

            LocalDate nowDate = LocalDate.parse(learningHistories.get(i).getDate());

            if(isConsecutive(beforeDate, nowDate)) {
                consecutiveCount++;
                beforeDate = nowDate;
            }
            else {
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
