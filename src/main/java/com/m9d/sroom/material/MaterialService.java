package com.m9d.sroom.material;

import com.m9d.sroom.common.dto.*;
import com.m9d.sroom.common.dto.CourseVideoDto;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.ai.exception.QuizTypeNotMatchException;
import com.m9d.sroom.ai.model.MaterialVaildStatus;
import com.m9d.sroom.ai.vo.MaterialResultsVo;
import com.m9d.sroom.ai.vo.QuizVo;
import com.m9d.sroom.material.dto.request.SubmittedQuiz;
import com.m9d.sroom.material.dto.response.*;
import com.m9d.sroom.material.exception.*;
import com.m9d.sroom.material.model.*;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.common.repository.coursedailylog.CourseDailyLogRepository;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.member.MemberDto;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.common.repository.quiz.QuizRepository;
import com.m9d.sroom.common.repository.quizoption.QuizOptionRepository;
import com.m9d.sroom.common.repository.summary.SummaryRepository;
import com.m9d.sroom.video.VideoDto;
import com.m9d.sroom.video.repository.VideoRepository;
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
        CourseVideoDto courseVideoDto = validateCourseVideoForMember(memberId, courseVideoId);

        if (courseVideoDto.getSummaryId() == MaterialStatus.CREATING.getValue()) {
            return Material.builder()
                    .status(MaterialStatus.CREATING.getValue())
                    .build();
        } else if (courseVideoDto.getSummaryId() == MaterialStatus.CREATION_FAILED.getValue()) {
            return Material.builder()
                    .status(MaterialStatus.CREATION_FAILED.getValue())
                    .build();
        } else {
            List<QuizRes> quizResList = getQuizResList(courseVideoDto.getVideoId(), courseVideoId);
            return Material.builder()
                    .status(MaterialStatus.CREATED.getValue())
                    .summaryBrief(new SummaryBrief(summaryRepository.getById(courseVideoDto.getSummaryId())))
                    .quizzes(quizResList)
                    .totalQuizCount(quizResList.size())
                    .build();
        }
    }

    private List<QuizRes> getQuizResList(Long videoId, Long courseVideoId) {
        List<QuizRes> quizResList = new ArrayList<>();
        for (QuizDto quizDto : quizRepository.getListByVideoId(videoId)) {
            quizResList.add(getQuizRes(courseVideoId, quizDto));
        }

        return quizResList;
    }

    private QuizRes getQuizRes(Long courseVideoId, QuizDto quizDto) {
        QuizRes quizRes = QuizRes.builder()
                .id(quizDto.getId())
                .type(quizDto.getType())
                .question(quizDto.getQuestion())
                .options(getOptionsStr(quizDto.getId()))
                .answer(getQuizAnswer(quizDto))
                .build();

        courseQuizRepository.findByQuizIdAndCourseVideoId(quizDto.getId(), courseVideoId)
                .ifPresentOrElse(
                        courseQuizDto -> setQuizResWithCourseQuiz(quizRes, courseQuizDto),
                        () -> quizRes.setSubmitted(false)
                );
        return quizRes;
    }

    private List<String> getOptionsStr(Long quizId) {
        return quizOptionRepository.getListByQuizId(quizId).stream()
                .sorted(Comparator.comparingInt(QuizOptionDto::getOptionIndex))
                .map(QuizOptionDto::getOptionText)
                .collect(Collectors.toList());
    }

    private String getQuizAnswer(QuizDto quizDto) {
        switch (QuizType.fromValue(quizDto.getType())) {
            case MULTIPLE_CHOICE:
                return String.valueOf(quizDto.getChoiceAnswer());
            case SUBJECTIVE:
                return quizDto.getSubjectiveAnswer();
            case TRUE_FALSE:
                return quizDto.getChoiceAnswer().equals(0) ? "false" : "true";
            default:
                throw new QuizTypeNotMatchException(quizDto.getType());
        }
    }

    private void setQuizResWithCourseQuiz(QuizRes quizRes, CourseQuizDto courseQuizDto) {
        quizRes.setSubmitted(true);
        quizRes.setCorrect(courseQuizDto.getCorrect());
        quizRes.setScrapped(courseQuizDto.getScrapped());
        quizRes.setSubmittedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(courseQuizDto.getSubmittedTime()));

        if (quizRes.getType() == QuizType.TRUE_FALSE.getValue()) {
            quizRes.setSubmittedAnswer(courseQuizDto.getSubmittedAnswer().equals("0") ? "false" : "true");
        } else {
            quizRes.setSubmittedAnswer(courseQuizDto.getSubmittedAnswer());
        }
    }

    @Transactional
    public SummaryId updateSummary(Long memberId, Long courseVideoId, String content) {
        CourseVideoDto courseVideoDto = validateCourseVideoForMember(memberId, courseVideoId);

        SummaryDto summaryDto = summaryRepository.findById(courseVideoDto.getSummaryId())
                .orElseThrow(SummaryNotFoundException::new);

        if (summaryDto.isModified()) {
            summaryDto.setContent(content);
            summaryRepository.updateById(summaryDto.getId(), summaryDto);
        } else {
            summaryDto = summaryRepository.save(SummaryDto.builder()
                    .videoId(courseVideoDto.getVideoId())
                    .content(content)
                    .modified(true)
                    .build());

            courseVideoDto.setSummaryId(summaryDto.getId());
            courseVideoRepository.updateById(courseVideoId, courseVideoDto);
        }

        return SummaryId.builder()
                .summaryId(summaryDto.getId())
                .build();
    }

    private CourseVideoDto validateCourseVideoForMember(Long memberId, Long courseVideoId) {
        CourseVideoDto courseVideoDto = courseVideoRepository.findById(courseVideoId)
                .orElseThrow(CourseVideoNotFoundException::new);

        if (!courseVideoDto.getMemberId().equals(memberId)) {
            throw new CourseNotMatchException();
        }
        return courseVideoDto;
    }

    @Transactional
    public List<SubmittedQuizInfo> submitQuizResults(Long memberId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        CourseVideoDto courseVideoDto = validateCourseVideoForMember(memberId, courseVideoId);

        validateSubmittedQuizzes(courseVideoDto.getVideoId(), courseVideoId, submittedQuizList);

        updateDailyLogQuizCount(memberId, courseVideoDto.getCourseId(), submittedQuizList.size());
        updateMemberQuizCount(memberRepository.getById(memberId), submittedQuizList);

        List<SubmittedQuizInfo> quizInfoList = new ArrayList<>();
        for (SubmittedQuiz submittedQuiz : submittedQuizList) {
            QuizDto quizDto = quizRepository.getById(submittedQuiz.getQuizId());

            CourseQuizDto courseQuizDto = courseQuizRepository.save(CourseQuizDto.builder()
                    .courseId(courseVideoDto.getCourseId())
                    .quizId(quizDto.getId())
                    .videoId(courseVideoDto.getVideoId())
                    .submittedAnswer(alterSubmittedAnswer(quizDto.getType(), submittedQuiz.getSubmittedAnswer()))
                    .correct(submittedQuiz.getIsCorrect())
                    .courseVideoId(courseVideoId)
                    .build());

            quizInfoList.add(new SubmittedQuizInfo(quizDto.getId(), courseQuizDto.getId()));
        }
        return quizInfoList;
    }

    private void validateSubmittedQuizzes(Long videoId, Long courseVideoId, List<SubmittedQuiz> submittedQuizList) {
        QuizDto quizDto = quizRepository.findById(submittedQuizList.get(0).getQuizId())
                .orElseThrow(QuizNotFoundException::new);

        if (!quizDto.getVideoId().equals(videoId)) {
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
        Optional<CourseDailyLogDto> courseDailyLogOptional = courseDailyLogRepository.findByCourseIdAndDate(courseId, Date.valueOf(LocalDate.now()));
        if (courseDailyLogOptional.isEmpty()) {
            courseDailyLogRepository.save(CourseDailyLogDto.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .dailyLogDate(Date.valueOf(LocalDate.now()))
                    .learningTime(0)
                    .quizCount(submittedQuizCount)
                    .lectureCount(0)
                    .build());
        } else {
            CourseDailyLogDto dailyLog = courseDailyLogOptional.get();
            dailyLog.setQuizCount(dailyLog.getQuizCount() + submittedQuizCount);
            courseDailyLogRepository.updateById(dailyLog.getCourseDailyLogId(), dailyLog);
        }
    }

    private void updateMemberQuizCount(MemberDto memberDto, List<SubmittedQuiz> quizList) {
        memberDto.setTotalSolvedCount(quizList.size() + memberDto.getTotalSolvedCount());
        memberDto.setTotalCorrectCount((int) quizList.stream()
                .filter(SubmittedQuiz::getIsCorrect)
                .count() + memberDto.getTotalCorrectCount());
        memberRepository.updateById(memberDto.getMemberId(), memberDto);
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
        CourseQuizDto courseQuizDto = validateCourseQuizForMember(memberId, courseQuizId);

        courseQuizDto.setScrapped(!courseQuizDto.getScrapped());
        courseQuizRepository.updateById(courseQuizDto.getId(), courseQuizDto);

        return ScrapResult.builder()
                .courseQuizId(courseQuizId)
                .scrapped(courseQuizDto.getScrapped())
                .build();
    }

    private CourseQuizDto validateCourseQuizForMember(Long memberId, Long courseQuizId) {
        CourseQuizDto courseQuizDto = courseQuizRepository.findById(courseQuizId)
                .orElseThrow(CourseQuizNotFoundException::new);

        Long memberIdByCourse = courseRepository.getById(courseQuizDto
                        .getCourseId())
                .getMemberId();
        if (!memberId.equals(memberIdByCourse)) {
            throw new CourseNotMatchException();
        }
        return courseQuizDto;
    }

    @Transactional
    public void saveMaterials(MaterialResultsVo materialVo) throws Exception {
        VideoDto videoDto = videoRepository.findByCode(materialVo.getVideoId())
                .orElseThrow(() -> {
                    log.warn("can't find video information from db. video code = {}", materialVo.getVideoId());
                    return new VideoNotFoundFromDBException();
                });

        Long summaryId;
        if (materialVo.getIsValid() == MaterialVaildStatus.IN_VALID.getValue()) {
            videoDto.setMaterialStatus(MaterialStatus.CREATION_FAILED.getValue());
            summaryId = (long) MaterialStatus.CREATION_FAILED.getValue();
        } else {
            videoDto.setMaterialStatus(MaterialStatus.CREATED.getValue());
            summaryId = summaryRepository.save(SummaryDto.builder()
                            .videoId(videoDto.getVideoId())
                            .content(materialVo.getSummary())
                            .modified(false)
                            .build())
                    .getId();
        }
        videoDto.setSummaryId(summaryId);
        videoRepository.updateById(videoDto.getVideoId(), videoDto);
        courseVideoRepository.updateSummaryId(videoDto.getVideoId(), summaryId);

        for (QuizVo quizVo : materialVo.getQuizzes()) {
            saveQuiz(videoDto.getVideoId(), quizVo);
        }
    }

    private void saveQuiz(Long videoId, QuizVo quizVo) throws NumberFormatException, QuizTypeNotMatchException {
        QuizDto quizDto = QuizDto.builder()
                .videoId(videoId)
                .type(quizVo.getQuizType())
                .question(quizVo.getQuizQuestion())
                .build();

        switch (QuizType.fromValue(quizVo.getQuizType())) {
            case MULTIPLE_CHOICE:
            case TRUE_FALSE:
                quizDto.setChoiceAnswer(Integer.parseInt(quizVo.getAnswer()));
                break;
            case SUBJECTIVE:
                quizDto.setSubjectiveAnswer(quizVo.getAnswer());
                break;
            default:
                throw new QuizTypeNotMatchException(quizVo.getQuizType());
        }
        quizDto = quizRepository.save(quizDto);

        if (quizVo.getQuizType() == QuizType.MULTIPLE_CHOICE.getValue()) {
            saveQuizOptions(quizDto.getId(), quizVo.getOptions());
        }
    }

    private void saveQuizOptions(Long quizId, List<String> quizOptionList) throws IndexOutOfBoundsException {
        int optionCount = Math.min(DEFAULT_QUIZ_OPTION_COUNT, quizOptionList.size());

        for (int optionIndex = 0; optionIndex < optionCount; optionIndex++) {
            quizOptionRepository.save(QuizOptionDto.builder()
                    .quizId(quizId)
                    .optionText(quizOptionList.get(optionIndex))
                    .optionIndex(optionIndex + 1)
                    .build());
        }
    }
}
