package com.m9d.sroom.material.service;

import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.global.model.CourseDailyLog;
import com.m9d.sroom.global.model.CourseVideo;
import com.m9d.sroom.global.model.QuizOption;
import com.m9d.sroom.gpt.exception.QuizTypeNotMatchException;
import com.m9d.sroom.gpt.model.MaterialVaildStatus;
import com.m9d.sroom.gpt.vo.MaterialResultsVo;
import com.m9d.sroom.gpt.vo.QuizVo;
import com.m9d.sroom.lecture.repository.LectureRepository;
import com.m9d.sroom.material.dto.request.SubmittedQuiz;
import com.m9d.sroom.material.dto.response.*;
import com.m9d.sroom.material.exception.*;
import com.m9d.sroom.material.model.*;
import com.m9d.sroom.global.model.Summary;
import com.m9d.sroom.material.model.SubmittedQuizInfo;
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

import static com.m9d.sroom.material.constant.MaterialConstant.*;

@Service
@Slf4j
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    public MaterialService(MaterialRepository materialRepository, CourseRepository courseRepository, MemberRepository memberRepository, LectureRepository lectureRepository) {
        this.materialRepository = materialRepository;
        this.courseRepository = courseRepository;
        this.memberRepository = memberRepository;
        this.lectureRepository = lectureRepository;
    }

    @Transactional
    public Material getMaterials(Long memberId, Long courseVideoId) {
        Material material;
        List<Quiz> quizList;
        SummaryBrief summaryBrief;

        CourseVideo courseVideo = getCourseVideo(courseVideoId);

        validateCourseVideoIdForMember(memberId, courseVideo);
        log.debug("courseVideo.summaryId = {}", courseVideo.getSummaryId());

        Long summaryId = (courseVideo.getSummaryId() == 0) ? null : courseVideo.getSummaryId();

        if (summaryId == null) {
            material = Material.builder()
                    .status(MaterialStatus.CREATING.getValue())
                    .build();
        } else if (summaryId == MaterialStatus.CREATION_FAILED.getValue()) {
            material = Material.builder()
                    .status(MaterialStatus.CREATION_FAILED.getValue())
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

            Optional<SubmittedQuizInfo> courseQuizOpt = materialRepository.findCourseQuizInfo(quiz.getId(), courseVideoId);
            if (courseQuizOpt.isPresent()) {
                SubmittedQuizInfo submittedQuizInfo = courseQuizOpt.get();
                quiz.setSubmitted(true);
                quiz.setSubmittedAnswer(submittedQuizInfo.getSubmittedAnswer());
                quiz.setCorrect(submittedQuizInfo.isCorrect());
                quiz.setScrapped(submittedQuizInfo.isScrapped());
                quiz.setSubmittedAt(DateUtil.dateFormat.format(submittedQuizInfo.getSubmittedTime()));
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
            optionList.add(option.getIndex() - 1, option.getOptionText());
        }
        quiz.setOptions(optionList);
    }

    private void translateNumToTF(Quiz quiz, Optional<SubmittedQuizInfo> courseQuizOpt) {
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
            summaryId = materialRepository.saveSummary(originalSummary.getVideoId(), content, true);
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
    public List<com.m9d.sroom.material.dto.response.SubmittedQuizInfo> submitQuizResults(Long memberId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        CourseVideoKey courseVideoKey = courseRepository.getCourseAndVideoId(courseVideoId);
        Long courseId = courseVideoKey.getCourseId();
        Long videoId = courseVideoKey.getVideoId();

        validateQuizzesForMemberAndVideo(memberId, courseId, videoId, courseVideoId, submittedQuizList);

        updateDailyLogQuizCount(memberId, courseId, submittedQuizList);
        updateMemberQuizCount(memberId, submittedQuizList);

        List<com.m9d.sroom.material.dto.response.SubmittedQuizInfo> quizInfoList = new ArrayList<>();

        for (SubmittedQuiz submittedQuiz : submittedQuizList) {

            Long quizId = submittedQuiz.getQuizId();
            Long courseQuizId = materialRepository.saveCourseQuiz(courseId, videoId, courseVideoId, submittedQuiz);
            quizInfoList.add(new com.m9d.sroom.material.dto.response.SubmittedQuizInfo(quizId, courseQuizId));
        }

        return quizInfoList;
    }

    private void validateQuizzesForMemberAndVideo(Long memberId, Long courseId, Long videoId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        Long originalMemberId = courseRepository.getMemberIdByCourseId(courseId);

        if (!originalMemberId.equals(memberId)) {
            throw new CourseNotMatchException();
        }

        for (SubmittedQuiz quiz : submittedQuizList) {
            Optional<SubmittedQuizInfo> courseQuizOptional = materialRepository.findCourseQuizInfo(quiz.getQuizId(), courseVideoId);

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

    @Transactional
    public ScrapResult switchScrapFlag(Long memberId, Long courseQuizId) {
        validateCourseQuizForMember(memberId, courseQuizId);

        materialRepository.switchQuizScrapFlag(courseQuizId);

        boolean scrapStatus = materialRepository.isScrappedById(courseQuizId);

        return ScrapResult.builder()
                .courseQuizId(courseQuizId)
                .scrapped(scrapStatus)
                .build();
    }

    private void validateCourseQuizForMember(Long memberId, Long courseQuizId) {
        Optional<CourseQuizInfo> courseQuizInfoOptional = materialRepository.findCourseQuizInfoById(courseQuizId);

        if (courseQuizInfoOptional.isEmpty()) {
            throw new CourseQuizNotFoundException();
        }
        CourseQuizInfo quizInfo = courseQuizInfoOptional.get();

        Long memberIdByCourse = courseRepository.getMemberIdByCourseId(quizInfo.getCourseId());

        if (!memberId.equals(memberIdByCourse)) {
            throw new CourseNotMatchException();
        }
    }

    @Transactional
    public void saveMaterials(MaterialResultsVo materialVo) throws Exception {
        String videoCode = materialVo.getVideoId();


        Long videoId = courseRepository.findVideoIdByCode(videoCode);

        if (videoId == null) {
            log.warn("can't find video information from db. video code = {}", videoCode);
            throw new VideoNotFoundFromDBException();
        }

        if (materialVo.getIsValid() == MaterialVaildStatus.IN_VALID.getValue()) {
            log.debug("no valid materials. videoCode = {}", videoCode);
            materialRepository.updateMaterialStatusByCode(videoCode, MaterialStatus.CREATION_FAILED.getValue());
            courseRepository.updateSummaryId(videoId, (long) MaterialStatus.CREATION_FAILED.getValue());
            return;
        }

        Long summaryId = materialRepository.saveSummary(videoId, materialVo.getSummary(), false);
        courseRepository.updateSummaryId(videoId, summaryId);
        materialRepository.updateMaterialStatusByCode(videoCode, MaterialStatus.CREATED.getValue());
        log.info("videoCode = {}, videoId = {}, summaryId = {}, ", videoCode, videoId, summaryId);

        for (QuizVo quizVo : materialVo.getQuizzes()) {
            saveQuiz(videoId, quizVo);
        }
    }

    private void saveQuiz(Long videoId, QuizVo quizVo) throws NumberFormatException, QuizTypeNotMatchException {
        if (quizVo.getQuizType() == QuizType.SUBJECTIVE.getValue()) {
            materialRepository.saveSubjectiveQuiz(videoId, quizVo.getQuizType(), quizVo.getQuizQuestion(), quizVo.getAnswer());
        } else if (quizVo.getQuizType() == QuizType.MULTIPLE_CHOICE.getValue()) {
            Long quizId = materialRepository.saveMultipleChoiceQuiz(videoId, quizVo.getQuizType(), quizVo.getQuizQuestion(), Integer.parseInt(quizVo.getAnswer()));
            saveQuizOptions(quizId, quizVo);
        } else if (quizVo.getQuizType() == QuizType.TRUE_FALSE.getValue()) {
            materialRepository.saveMultipleChoiceQuiz(videoId, quizVo.getQuizType(), quizVo.getQuizQuestion(), Integer.parseInt(quizVo.getAnswer()));
        } else {
            throw new QuizTypeNotMatchException(quizVo.getQuizType());
        }
    }

    private void saveQuizOptions(Long quizId, QuizVo quizVo) throws IndexOutOfBoundsException {
        int optionCount = Math.min(DEFAULT_QUIZ_OPTION_COUNT, quizVo.getOptions().size());

        for (int optionIndex = 0; optionIndex < optionCount; optionIndex++) {
            materialRepository.saveQuizOption(quizId, quizVo.getOptions().get(optionIndex), optionIndex + 1);
        }
    }
}
