package com.m9d.sroom.material.service;

import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.global.model.CourseDailyLog;
import com.m9d.sroom.global.model.CourseVideo;
import com.m9d.sroom.global.model.QuizOption;
import com.m9d.sroom.material.dto.request.SubmittedQuiz;
import com.m9d.sroom.material.dto.response.*;
import com.m9d.sroom.material.exception.CourseQuizDuplicationException;
import com.m9d.sroom.material.exception.QuizIdNotMatchException;
import com.m9d.sroom.material.exception.SummaryNotFoundException;
import com.m9d.sroom.material.model.*;
import com.m9d.sroom.global.model.Summary;
import com.m9d.sroom.material.repository.MaterialRepository;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;

    public MaterialService(MaterialRepository materialRepository, CourseRepository courseRepository, MemberRepository memberRepository) {
        this.materialRepository = materialRepository;
        this.courseRepository = courseRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Material getMaterials(Long memberId, Long courseVideoId) {
        Material material;
        List<Quiz> quizList;
        SummaryBrief summaryBrief;

        CourseVideo courseVideo = getCourseVideo(courseVideoId);

        validateCourseVideoIdForMember(memberId, courseVideo);

        if (courseVideo.getSummaryId() == null) {
            material = Material.builder()
                    .status(MaterialStatus.CREATING.getValue())
                    .build();
        } else {
            quizList = getQuizList(courseVideo.getVideoId(), courseVideoId);
            summaryBrief = materialRepository.getSummaryById(courseVideo.getSummaryId());

            material = Material.builder()
                    .status(MaterialStatus.CREATED.getValue())
                    .summaryBrief(summaryBrief)
                    .quizzes(quizList)
                    .totalQuizCount(quizList.size())
                    .build();
        }

        return material;
    }

    private CourseVideo getCourseVideo(Long courseVideoId) {
        Optional<CourseVideo> courseVideoOpt = courseRepository.findCourseVideoById(courseVideoId);

        if (courseVideoOpt.isEmpty()) {
            throw new CourseVideoNotFoundException();
        }

        return courseVideoOpt.get();
    }

    private void validateCourseVideoIdForMember(Long memberId, CourseVideo courseVideo) {
        if (!courseVideo.getMemberId().equals(memberId)) {
            throw new CourseNotMatchException();
        }
    }

    private List<Quiz> getQuizList(Long videoId, Long courseVideoId) {
        List<Quiz> quizzes = materialRepository.getQuizListByVideoId(videoId);

        for (Quiz quiz : quizzes) {
            List<QuizOption> options = materialRepository.getQuizOptionListByQuizId(quiz.getId());
            setQuizOptions(quiz, options);

            Optional<CourseQuiz> courseQuizOpt = materialRepository.findCourseQuizInfo(quiz.getId(), courseVideoId);
            if (courseQuizOpt.isPresent()) {
                CourseQuiz courseQuiz = courseQuizOpt.get();
                quiz.setSubmitted(true);
                quiz.setSubmittedAnswer(courseQuiz.getSubmittedAnswer());
                quiz.setCorrect(courseQuiz.isCorrect());
                quiz.setSubmittedAt(DateUtil.dateFormat.format(courseQuiz.getSubmittedTime()));

            } else {
                quiz.setSubmitted(false);
            }
            translateNumToTF(quiz, courseQuizOpt);
        }

        return quizzes;
    }

    private void setQuizOptions(Quiz quiz, List<QuizOption> options) {
        List<String> optionList = new ArrayList<>(5);
        for (QuizOption option : options) {
            optionList.add(option.getIndex(), option.getOptionText());
        }
        quiz.setOptions(optionList);
    }

    private void translateNumToTF(Quiz quiz, Optional<CourseQuiz> courseQuizOpt) {
        if (quiz.getType() != QuizType.TRUE_FALSE.getValue()) {
            return;
        }

        if (courseQuizOpt.isPresent()) {
            String submittedAnswer = courseQuizOpt.get().getSubmittedAnswer().equals("0") ? "false" : "true";
            quiz.setSubmittedAnswer(submittedAnswer);
        }

        String answer = quiz.getAnswer().equals("0") ? "false" : "true";
        quiz.setAnswer(answer);
    }

    @Transactional
    public SummaryId updateSummary(Long memberId, Long courseVideoId, String content) {
        validateCourseVideoForMember(memberId, courseVideoId);

        Summary originalSummary = getSummary(courseVideoId);
        long summaryId;

        if (originalSummary.isModified()) {
            summaryId = originalSummary.getId();
            materialRepository.updateSummary(summaryId, content);
        } else {
            summaryId = materialRepository.saveSummaryModified(originalSummary.getVideoId(), content);
            materialRepository.updateSummaryIdByCourseVideoId(courseVideoId, summaryId);
        }

        return SummaryId.builder()
                .summaryId(summaryId)
                .build();
    }

    private void validateCourseVideoForMember(Long memberId, Long courseVideoId) {
        Optional<CourseVideo> courseVideoOptional = courseRepository.findCourseVideoById(courseVideoId);
        if (courseVideoOptional.isEmpty()) {
            throw new CourseVideoNotFoundException();
        }

        CourseVideo courseVideo = courseVideoOptional.get();
        if (!courseVideo.getMemberId().equals(memberId)) {
            throw new CourseNotMatchException();
        }
    }

    private Summary getSummary(Long courseVideoId) {
        Optional<Summary> originalSummaryOpt = materialRepository.findSummaryByCourseVideoId(courseVideoId);
        if (originalSummaryOpt.isEmpty()) {
            throw new SummaryNotFoundException();
        }

        Summary originalSummary = originalSummaryOpt.get();
        return originalSummary;
    }

    @Transactional
    public CourseQuizIdList submitQuizResults(Long memberId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        CourseAndVideoId courseAndVideoId = courseRepository.getCourseAndVideoId(courseVideoId);
        Long courseId = courseAndVideoId.getCourseId();
        Long videoId = courseAndVideoId.getVideoId();

        validateQuizzesForMemberAndVideo(memberId, courseId, videoId, courseVideoId, submittedQuizList);

        updateDailyLogQuizCount(memberId, courseId, submittedQuizList);
        updateMemberQuizCount(memberId, submittedQuizList);

        List<Long> quizIdList = new ArrayList<>();

        for (SubmittedQuiz submittedQuiz : submittedQuizList) {

            Long quizId = materialRepository.saveCourseQuiz(courseId, videoId, courseVideoId, submittedQuiz);
            quizIdList.add(quizId);
        }

        return CourseQuizIdList.builder()
                .courseQuizIdList(quizIdList)
                .build();
    }

    private void validateQuizzesForMemberAndVideo(Long memberId, Long courseId, Long videoId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        Long originalMemberId = courseRepository.getMemberIdByCourseId(courseId);

        if (!originalMemberId.equals(memberId)) {
            throw new CourseNotMatchException();
        }

        for (SubmittedQuiz quiz : submittedQuizList) {
            Optional<CourseQuiz> courseQuizOptional = materialRepository.findCourseQuizInfo(quiz.getQuizId(), courseVideoId);

            if (courseQuizOptional.isPresent()) {
                throw new CourseQuizDuplicationException();
            }

            Long videoIdByQuizId = materialRepository.getVideoIdByQuizId(quiz.getQuizId());

            if (!videoIdByQuizId.equals(videoId)) {
                throw new QuizIdNotMatchException();
            }
        }
    }

    private void updateDailyLogQuizCount(Long memberId, Long courseId, List<SubmittedQuiz> quizList) {
        Integer quizCountByDailyLog = courseRepository.findQuizCountByDailyLog(courseId, Date.valueOf(LocalDate.now()));

        if (quizCountByDailyLog == null) {
            CourseDailyLog dailyLog = CourseDailyLog.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .dailyLogDate(Date.valueOf(LocalDate.now()))
                    .learningTime(0)
                    .quizCount(quizList.size())
                    .lectureCount(0)
                    .build();
            courseRepository.saveCourseDailyLog(dailyLog);
        } else {
            courseRepository.updateCourseDailyLogQuizCount(courseId, Date.valueOf(LocalDate.now()), quizCountByDailyLog + quizList.size());
        }
    }

    private void updateMemberQuizCount(Long memberId, List<SubmittedQuiz> quizList) {
        int correctCount = (int) quizList.stream()
                .filter(SubmittedQuiz::getIsCorrect)
                .count();

        memberRepository.addQuizCount(memberId, quizList.size(), correctCount);
    }
}
