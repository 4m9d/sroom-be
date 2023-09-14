package com.m9d.sroom.material.service;

import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.global.mapper.*;
import com.m9d.sroom.gpt.exception.QuizTypeNotMatchException;
import com.m9d.sroom.gpt.model.MaterialVaildStatus;
import com.m9d.sroom.gpt.vo.MaterialResultsVo;
import com.m9d.sroom.gpt.vo.QuizVo;
import com.m9d.sroom.lecture.repository.LectureRepository;
import com.m9d.sroom.material.dto.request.SubmittedQuiz;
import com.m9d.sroom.material.dto.response.*;
import com.m9d.sroom.material.exception.*;
import com.m9d.sroom.material.model.*;
import com.m9d.sroom.material.repository.MaterialRepository;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.m9d.sroom.material.constant.MaterialConstant.DEFAULT_QUIZ_OPTION_COUNT;

@Slf4j
public class MaterialServiceV2 {

    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    public MaterialServiceV2(MaterialRepository materialRepository, CourseRepository courseRepository, MemberRepository memberRepository, LectureRepository lectureRepository) {
        this.materialRepository = materialRepository;
        this.courseRepository = courseRepository;
        this.memberRepository = memberRepository;
        this.lectureRepository = lectureRepository;
    }

    @Transactional
    public Material getMaterials(Long memberId, Long courseVideoId) {
        Material material;
        List<QuizRes> quizResList;
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
            quizResList = getQuizList(courseVideo.getVideoId(), courseVideoId);
            summaryBrief = materialRepository.getSummaryById(courseVideo.getSummaryId());

            material = Material.builder()
                    .status(MaterialStatus.CREATED.getValue())
                    .summaryBrief(summaryBrief)
                    .quizzes(quizResList)
                    .totalQuizCount(quizResList.size())
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

    private List<QuizRes> getQuizList(Long videoId, Long courseVideoId) {
        List<QuizRes> quizResList = materialRepository.getQuizListByVideoId(videoId);

        for (QuizRes quizRes : quizResList) {
            List<QuizOption> options = materialRepository.getQuizOptionListByQuizId(quizRes.getId());
            setQuizOptions(quizRes, options);

            Optional<SubmittedQuizInfoRes> courseQuizOpt = materialRepository.findCourseQuizInfo(quizRes.getId(), courseVideoId);
            if (courseQuizOpt.isPresent()) {
                SubmittedQuizInfoRes submittedQuizInfoRes = courseQuizOpt.get();
                quizRes.setSubmitted(true);
                quizRes.setSubmittedAnswer(submittedQuizInfoRes.getSubmittedAnswer());
                quizRes.setCorrect(submittedQuizInfoRes.isCorrect());
                quizRes.setScrapped(submittedQuizInfoRes.isScrapped());
                quizRes.setSubmittedAt(DateUtil.dateFormat.format(submittedQuizInfoRes.getSubmittedTime()));
            } else {
                quizRes.setSubmitted(false);
            }
            translateNumToTF(quizRes, courseQuizOpt);
        }

        return quizResList;
    }

    private void setQuizOptions(QuizRes quizRes, List<QuizOption> options) {
        List<String> optionList = new ArrayList<>(5);
        for (QuizOption option : options) {
            optionList.add(option.getIndex() - 1, option.getOptionText());
        }
        quizRes.setOptions(optionList);
    }

    private void translateNumToTF(QuizRes quizRes, Optional<SubmittedQuizInfoRes> courseQuizOpt) {
        if (quizRes.getType() != QuizType.TRUE_FALSE.getValue()) {
            return;
        }

        if (courseQuizOpt.isPresent()) {
            String submittedAnswer = courseQuizOpt.get().getSubmittedAnswer().equals("0") ? "false" : "true";
            quizRes.setSubmittedAnswer(submittedAnswer);
        }

        String answer = quizRes.getAnswer().equals("0") ? "false" : "true";
        quizRes.setAnswer(answer);
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
    public List<SubmittedQuizInfo> submitQuizResults(Long memberId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        CourseAndVideoId courseAndVideoId = courseRepository.getCourseAndVideoId(courseVideoId);
        Long courseId = courseAndVideoId.getCourseId();
        Long videoId = courseAndVideoId.getVideoId();

        validateQuizzesForMemberAndVideo(memberId, courseId, videoId, courseVideoId, submittedQuizList);

        updateDailyLogQuizCount(memberId, courseId, submittedQuizList.size());
        updateMemberQuizCount(memberId, submittedQuizList);

        List<SubmittedQuizInfo> quizInfoList = new ArrayList<>();

        for (SubmittedQuiz submittedQuiz : submittedQuizList) {
            Quiz quiz = materialRepository.getQuizById(submittedQuiz.getQuizId());

            String submittedAnswer = alterSubmittedAnswer(quiz.getType(), submittedQuiz.getSubmittedAnswer());

            Long courseQuizId = materialRepository.saveCourseQuiz(courseId, quiz.getId(), videoId, courseVideoId, submittedQuiz.getIsCorrect(), submittedAnswer);
            quizInfoList.add(new SubmittedQuizInfo(quiz.getId(), courseQuizId));
        }

        return quizInfoList;
    }

    private void validateQuizzesForMemberAndVideo(Long memberId, Long courseId, Long videoId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        Long originalMemberId = courseRepository.getMemberIdByCourseId(courseId);

        if (!originalMemberId.equals(memberId)) {
            throw new CourseNotMatchException();
        }

        for (SubmittedQuiz quiz : submittedQuizList) {
            Optional<SubmittedQuizInfoRes> courseQuizOptional = materialRepository.findCourseQuizInfo(quiz.getQuizId(), courseVideoId);

            if (courseQuizOptional.isPresent()) {
                throw new CourseQuizDuplicationException();
            }

            Long videoIdByQuizId = materialRepository.getVideoIdByQuizId(quiz.getQuizId());

            if (!videoIdByQuizId.equals(videoId)) {
                throw new QuizIdNotMatchException();
            }

            if (quiz.getIsCorrect() == null) {
                throw new QuizAnswerFormatNotValidException();
            }
        }
    }

    private void updateDailyLogQuizCount(Long memberId, Long courseId, int submittedQuizCount) {
        Integer quizCountByDailyLog = courseRepository.findQuizCountByDailyLog(courseId, Date.valueOf(LocalDate.now()));

        if (quizCountByDailyLog == null) {
            CourseDailyLog dailyLog = CourseDailyLog.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .dailyLogDate(Date.valueOf(LocalDate.now()))
                    .learningTime(0)
                    .quizCount(submittedQuizCount)
                    .lectureCount(0)
                    .build();
            courseRepository.saveCourseDailyLog(dailyLog);
        } else {
            courseRepository.updateCourseDailyLogQuizCount(courseId, Date.valueOf(LocalDate.now()), quizCountByDailyLog + submittedQuizCount);
        }
    }

    private void updateMemberQuizCount(Long memberId, List<SubmittedQuiz> quizList) {
        int correctCount = (int) quizList.stream()
                .filter(SubmittedQuiz::getIsCorrect)
                .count();

        memberRepository.addQuizCount(memberId, quizList.size(), correctCount);
    }

    private String alterSubmittedAnswer(int quizType, String submittedAnswerReq) {
        String submittedAnswer;

        if (quizType == QuizType.MULTIPLE_CHOICE.getValue()) {
            validateSubmittedAnswerTypeOne(submittedAnswerReq);
            submittedAnswer = submittedAnswerReq;
        } else if (quizType == QuizType.SUBJECTIVE.getValue()) {
            submittedAnswer = submittedAnswerReq;
        } else if (quizType == QuizType.TRUE_FALSE.getValue()) {
            submittedAnswer = alterTrueOrFalseToInt(submittedAnswerReq);
        } else {
            throw new QuizTypeNotMatchException(quizType);
        }
        return submittedAnswer;
    }

    private void validateSubmittedAnswerTypeOne(String submittedAnswerReq) {
        try {
            int submittedAnswerInt = Integer.parseInt(submittedAnswerReq);
            if (submittedAnswerInt <= 0 || submittedAnswerInt > DEFAULT_QUIZ_OPTION_COUNT) {
                throw new QuizAnswerFormatNotValidException();
            }
        } catch (NumberFormatException e) {
            throw new QuizAnswerFormatNotValidException();
        }
    }

    private String alterTrueOrFalseToInt(String submittedAnswerReq) {
        String submittedAnswer;
        if (submittedAnswerReq.equals("true")) {
            submittedAnswer = String.valueOf(1);
        } else if (submittedAnswerReq.equals("false")) {
            submittedAnswer = String.valueOf(0);
        } else {
            throw new QuizAnswerFormatNotValidException();
        }
        return submittedAnswer;
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
