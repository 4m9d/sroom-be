package com.m9d.sroom.course.service;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    public MyCourses getMyCourses(Long memberId) {
        List<CourseInfo> courseInfoList = courseRepository.getCourseListByMemberId(memberId);
        int unfinishedCourseCount = getUnfinishedCourseCount(courseInfoList);

        int courseCount = courseInfoList.size();

        int completionRate = (int) ((float)(courseCount - unfinishedCourseCount) / courseCount * 100);

        for(int i = 0; i < courseInfoList.size(); i++) {

            Long courseId = courseInfoList.get(i).getCourseId();
            HashSet<String> channels = courseRepository.getChannelSetByCourseId(courseId);
            int lectureCount = courseRepository.getTotalLectureCountByCourseId(courseId);
            int completedLectureCount = courseRepository.getCompletedLectureCountByCourseId(courseId);

            System.out.println(lectureCount);
            System.out.println(completedLectureCount);

            courseInfoList.get(i).setChannels(String.join(", ", channels));
            courseInfoList.get(i).setTotalVideoCount(lectureCount);
            courseInfoList.get(i).setCompletedVideoCount(completedLectureCount);
        }

        MyCourses myCourses = MyCourses.builder()
                    .unfinishedCourse(unfinishedCourseCount)
                    .completionRate(completionRate)
                    .courses(courseInfoList)
                    .build();

        return myCourses;
    }

    public int getUnfinishedCourseCount(List<CourseInfo> courseInfoList) {

        int unfinishedCourseCount = 0;

        for(int i = 0; i < courseInfoList.size(); i++) {
            if(courseInfoList.get(i).getProgress() < 100) {
                unfinishedCourseCount++;
            }
        }

        return unfinishedCourseCount;
    }

    @Transactional
    public EnrolledCourseInfo enrollCourse(Long memberId, String LectureId) {
        return null;
    }

    public void requestToFastApi(String lectureCode, String defaultLanguage){

    }
}
