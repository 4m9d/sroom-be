package com.m9d.sroom.material.service;

import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.global.mapper.*;
import com.m9d.sroom.gpt.exception.QuizTypeNotMatchException;
import com.m9d.sroom.gpt.model.MaterialVaildStatus;
import com.m9d.sroom.gpt.vo.MaterialResultsVo;
import com.m9d.sroom.gpt.vo.QuizVo;
import com.m9d.sroom.material.dto.request.SubmittedQuiz;
import com.m9d.sroom.material.dto.response.*;
import com.m9d.sroom.material.exception.*;
import com.m9d.sroom.material.model.*;
import com.m9d.sroom.repository.course.CourseRepository;
import com.m9d.sroom.repository.coursedailylog.CourseDailyLogRepository;
import com.m9d.sroom.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.repository.member.MemberRepository;
import com.m9d.sroom.repository.quiz.QuizRepository;
import com.m9d.sroom.repository.quizoption.QuizOptionRepository;
import com.m9d.sroom.repository.summary.SummaryRepository;
import com.m9d.sroom.repository.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.m9d.sroom.material.constant.MaterialConstant.DEFAULT_QUIZ_OPTION_COUNT;

@Slf4j
@Service
public class MaterialService {

    private final CourseVideoRepository courseVideoRepository;
    private final QuizRepository quizRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final CourseQuizRepository courseQuizRepository;
    private final SummaryRepository summaryRepository;
    private final CourseDailyLogRepository courseDailyLogRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final VideoRepository videoRepository;

    public MaterialService(CourseVideoRepository courseVideoRepository, QuizRepository quizRepository, QuizOptionRepository quizOptionRepository, CourseQuizRepository courseQuizRepository, SummaryRepository summaryRepository, CourseDailyLogRepository courseDailyLogRepository, MemberRepository memberRepository, CourseRepository courseRepository, VideoRepository videoRepository) {
        this.courseVideoRepository = courseVideoRepository;
        this.quizRepository = quizRepository;
        this.quizOptionRepository = quizOptionRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.summaryRepository = summaryRepository;
        this.courseDailyLogRepository = courseDailyLogRepository;
        this.memberRepository = memberRepository;
        this.courseRepository = courseRepository;
        this.videoRepository = videoRepository;
    }

    @Transactional
    public Material getMaterials(Long memberId, Long courseVideoId) {
        CourseVideo courseVideo = validateCourseVideoForMember(memberId, courseVideoId);

        if (courseVideo.getSummaryId() == MaterialStatus.CREATING.getValue()) {
            return Material.builder()
                    .status(MaterialStatus.CREATING.getValue())
                    .build();
        } else if (courseVideo.getSummaryId() == MaterialStatus.CREATION_FAILED.getValue()) {
            return Material.builder()
                    .status(MaterialStatus.CREATION_FAILED.getValue())
                    .build();
        } else {
            List<QuizRes> quizResList = getQuizResList(courseVideo.getVideoId(), courseVideoId);
            return Material.builder()
                    .status(MaterialStatus.CREATED.getValue())
                    .summaryBrief(new SummaryBrief(summaryRepository.getById(courseVideo.getSummaryId())))
                    .quizzes(quizResList)
                    .totalQuizCount(quizResList.size())
                    .build();
        }
    }

    private List<QuizRes> getQuizResList(Long videoId, Long courseVideoId) {
        List<QuizRes> quizResList = new ArrayList<>();
        for (Quiz quiz : quizRepository.getListByVideoId(videoId)) {
            quizResList.add(getQuizRes(courseVideoId, quiz));
        }

        return quizResList;
    }

    private QuizRes getQuizRes(Long courseVideoId, Quiz quiz) {
        QuizRes quizRes = QuizRes.builder()
                .id(quiz.getId())
                .type(quiz.getType())
                .question(quiz.getQuestion())
                .options(getOptionsStr(quiz.getId()))
                .answer(getQuizAnswer(quiz))
                .build();

        courseQuizRepository.findByQuizIdAndCourseVideoId(quiz.getId(), courseVideoId)
                .ifPresentOrElse(
                        courseQuiz -> setQuizResWithCourseQuiz(quizRes, courseQuiz),
                        () -> quizRes.setSubmitted(false)
                );
        return quizRes;
    }

    private List<String> getOptionsStr(Long quizId) {
        return quizOptionRepository.getListByQuizId(quizId).stream()
                .sorted(Comparator.comparingInt(QuizOption::getOptionIndex))
                .map(QuizOption::getOptionText)
                .collect(Collectors.toList());
    }

    private String getQuizAnswer(Quiz quiz) {
        switch (QuizType.fromValue(quiz.getType())) {
            case MULTIPLE_CHOICE:
                return String.valueOf(quiz.getChoiceAnswer());
            case SUBJECTIVE:
                return quiz.getSubjectiveAnswer();
            case TRUE_FALSE:
                return quiz.getChoiceAnswer().equals(0) ? "false" : "true";
            default:
                throw new QuizTypeNotMatchException(quiz.getType());
        }
    }

    private void setQuizResWithCourseQuiz(QuizRes quizRes, CourseQuiz courseQuiz) {
        quizRes.setSubmitted(true);
        quizRes.setCorrect(courseQuiz.getCorrect());
        quizRes.setScrapped(courseQuiz.getScrapped());
        quizRes.setSubmittedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(courseQuiz.getSubmittedTime()));

        if (quizRes.getType() == QuizType.TRUE_FALSE.getValue()) {
            quizRes.setSubmittedAnswer(courseQuiz.getSubmittedAnswer().equals("0") ? "false" : "true");
        } else {
            quizRes.setSubmittedAnswer(courseQuiz.getSubmittedAnswer());
        }
    }

    @Transactional
    public SummaryId updateSummary(Long memberId, Long courseVideoId, String content) {
        CourseVideo courseVideo = validateCourseVideoForMember(memberId, courseVideoId);

        Summary summary = summaryRepository.findById(courseVideo.getSummaryId())
                .orElseThrow(SummaryNotFoundException::new);

        if (summary.isModified()) {
            summary.setContent(content);
            summaryRepository.updateById(summary.getId(), summary);
        } else {
            summary = summaryRepository.save(Summary.builder()
                    .videoId(courseVideo.getVideoId())
                    .content(content)
                    .modified(true)
                    .build());

            courseVideo.setSummaryId(summary.getId());
            courseVideoRepository.updateById(courseVideoId, courseVideo);
        }

        return SummaryId.builder()
                .summaryId(summary.getId())
                .build();
    }

    private CourseVideo validateCourseVideoForMember(Long memberId, Long courseVideoId) {
        CourseVideo courseVideo = courseVideoRepository.findById(courseVideoId)
                .orElseThrow(CourseVideoNotFoundException::new);

        if (!courseVideo.getMemberId().equals(memberId)) {
            throw new CourseNotMatchException();
        }
        return courseVideo;
    }

    @Transactional
    public List<SubmittedQuizInfo> submitQuizResults(Long memberId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        CourseVideo courseVideo = validateCourseVideoForMember(memberId, courseVideoId);

        validateSubmittedQuizzes(courseVideo.getVideoId(), courseVideoId, submittedQuizList);

        updateDailyLogQuizCount(memberId, courseVideo.getCourseId(), submittedQuizList.size());
        updateMemberQuizCount(memberRepository.getById(memberId), submittedQuizList);

        List<SubmittedQuizInfo> quizInfoList = new ArrayList<>();
        for (SubmittedQuiz submittedQuiz : submittedQuizList) {
            Quiz quiz = quizRepository.getById(submittedQuiz.getQuizId());

            CourseQuiz courseQuiz = courseQuizRepository.save(CourseQuiz.builder()
                    .courseId(courseVideo.getCourseId())
                    .quizId(quiz.getId())
                    .videoId(courseVideo.getVideoId())
                    .submittedAnswer(alterSubmittedAnswer(quiz.getType(), submittedQuiz.getSubmittedAnswer()))
                    .correct(submittedQuiz.getIsCorrect())
                    .courseVideoId(courseVideoId)
                    .memberId(memberId)
                    .build());

            quizInfoList.add(new SubmittedQuizInfo(quiz.getId(), courseQuiz.getId()));
        }
        return quizInfoList;
    }

    private void validateSubmittedQuizzes(Long videoId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        Quiz quiz = quizRepository.findById(submittedQuizList.get(0).getQuizId())
                .orElseThrow(QuizNotFoundException::new);

        if (!quiz.getVideoId().equals(videoId)) {
            throw new QuizIdNotMatchException();
        }

        for (SubmittedQuiz submittedQuiz : submittedQuizList) {
            if (courseQuizRepository.findByQuizIdAndCourseVideoId(submittedQuiz.getQuizId(), courseVideoId).isPresent()) {
                throw new CourseQuizDuplicationException();
            }

            if (submittedQuiz.getIsCorrect() == null) {
                throw new QuizAnswerFormatNotValidException();
            }
        }
    }

    private void updateDailyLogQuizCount(Long memberId, Long courseId, int submittedQuizCount) {
        Optional<CourseDailyLog> courseDailyLogOptional = courseDailyLogRepository.findByCourseIdAndDate(courseId, Date.valueOf(LocalDate.now()));
        if (courseDailyLogOptional.isEmpty()) {
            courseDailyLogRepository.save(CourseDailyLog.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .dailyLogDate(Date.valueOf(LocalDate.now()))
                    .learningTime(0)
                    .quizCount(submittedQuizCount)
                    .lectureCount(0)
                    .build());
        } else {
            CourseDailyLog dailyLog = courseDailyLogOptional.get();
            dailyLog.setQuizCount(dailyLog.getQuizCount() + submittedQuizCount);
            courseDailyLogRepository.updateById(dailyLog.getCourseDailyLogId(), dailyLog);
        }
    }

    private void updateMemberQuizCount(Member member, List<SubmittedQuiz> quizList) {
        member.setTotalSolvedCount(quizList.size() + member.getTotalSolvedCount());
        member.setTotalCorrectCount((int) quizList.stream()
                .filter(SubmittedQuiz::getIsCorrect)
                .count() + member.getTotalCorrectCount());
        memberRepository.updateById(member.getMemberId(), member);
    }

    private String alterSubmittedAnswer(int quizType, String submittedAnswerReq) {
        switch (QuizType.fromValue(quizType)) {
            case MULTIPLE_CHOICE:
                validateSubmittedAnswerTypeOne(submittedAnswerReq);
                return submittedAnswerReq;
            case SUBJECTIVE:
                return submittedAnswerReq;
            case TRUE_FALSE:
                return alterTrueOrFalseToInt(submittedAnswerReq);
            default:
                throw new QuizTypeNotMatchException(quizType);
        }
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
        switch (submittedAnswerReq) {
            case "true":
                return String.valueOf(1);
            case "false":
                return String.valueOf(0);
            default:
                throw new QuizAnswerFormatNotValidException();
        }
    }

    @Transactional
    public ScrapResult switchScrapFlag(Long memberId, Long courseQuizId) {
        CourseQuiz courseQuiz = validateCourseQuizForMember(memberId, courseQuizId);

        courseQuiz.setScrapped(!courseQuiz.getScrapped());
        courseQuizRepository.updateById(courseQuiz.getId(), courseQuiz);

        return ScrapResult.builder()
                .courseQuizId(courseQuizId)
                .scrapped(courseQuiz.getScrapped())
                .build();
    }

    private CourseQuiz validateCourseQuizForMember(Long memberId, Long courseQuizId) {
        CourseQuiz courseQuiz = courseQuizRepository.findById(courseQuizId)
                .orElseThrow(CourseQuizNotFoundException::new);

        Long memberIdByCourse = courseRepository.getById(courseQuiz
                        .getCourseId())
                .getMemberId();
        if (!memberId.equals(memberIdByCourse)) {
            throw new CourseNotMatchException();
        }
        return courseQuiz;
    }

    @Transactional
    public void saveMaterials(MaterialResultsVo materialVo) throws Exception {
        Video video = videoRepository.findByCode(materialVo.getVideoId())
                .orElseThrow(() -> {
                    log.warn("can't find video information from db. video code = {}", materialVo.getVideoId());
                    return new VideoNotFoundFromDBException();
                });

        Long summaryId;
        if (materialVo.getIsValid() == MaterialVaildStatus.IN_VALID.getValue()) {
            video.setMaterialStatus(MaterialStatus.CREATION_FAILED.getValue());
            summaryId = (long) MaterialStatus.CREATION_FAILED.getValue();
        } else {
            video.setMaterialStatus(MaterialStatus.CREATED.getValue());
            summaryId = summaryRepository.save(Summary.builder()
                            .videoId(video.getVideoId())
                            .content(materialVo.getSummary())
                            .modified(false)
                            .build())
                    .getId();
        }
        video.setSummaryId(summaryId);
        videoRepository.updateById(video.getVideoId(), video);
        courseVideoRepository.updateSummaryId(video.getVideoId(), summaryId);

        for (QuizVo quizVo : materialVo.getQuizzes()) {
            saveQuiz(video.getVideoId(), quizVo);
        }
    }

    private void saveQuiz(Long videoId, QuizVo quizVo) throws NumberFormatException, QuizTypeNotMatchException {
        Quiz quiz = Quiz.builder()
                .videoId(videoId)
                .type(quizVo.getQuizType())
                .question(quizVo.getQuizQuestion())
                .build();

        switch (QuizType.fromValue(quizVo.getQuizType())) {
            case MULTIPLE_CHOICE:
            case TRUE_FALSE:
                quiz.setChoiceAnswer(Integer.parseInt(quizVo.getAnswer()));
                break;
            case SUBJECTIVE:
                quiz.setSubjectiveAnswer(quizVo.getAnswer());
                break;
            default:
                throw new QuizTypeNotMatchException(quizVo.getQuizType());
        }
        quiz = quizRepository.save(quiz);

        if (quizVo.getQuizType() == QuizType.MULTIPLE_CHOICE.getValue()) {
            saveQuizOptions(quiz.getId(), quizVo.getOptions());
        }
    }

    private void saveQuizOptions(Long quizId, List<String> quizOptionList) throws IndexOutOfBoundsException {
        int optionCount = Math.min(DEFAULT_QUIZ_OPTION_COUNT, quizOptionList.size());

        for (int optionIndex = 0; optionIndex < optionCount; optionIndex++) {
            quizOptionRepository.save(QuizOption.builder()
                    .quizId(quizId)
                    .optionText(quizOptionList.get(optionIndex))
                    .optionIndex(optionIndex + 1)
                    .build());
        }
    }
}
