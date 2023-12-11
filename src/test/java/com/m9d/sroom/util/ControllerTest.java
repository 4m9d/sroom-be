package com.m9d.sroom.util;

import com.m9d.sroom.course.CourseService;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.member.MemberService;
import com.m9d.sroom.search.dto.response.KeywordSearchResponse;
import com.m9d.sroom.common.entity.MemberEntity;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.constant.ContentConstant;
import com.m9d.sroom.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ControllerTest extends SroomTest {

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected CourseService courseService;

    @Autowired
    protected VideoService videoService;

    @Test
    @DisplayName("AWS health test를 통과합니다.")
    void healthTest() throws Exception {

        //expected
        mockMvc.perform(get("/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    protected MemberEntity getNewMemberEntity() {
        String memberCode = UUID.randomUUID().toString();
        return memberService.findOrCreateMember(memberCode);
    }

    protected Login getNewLogin() {
        MemberEntity memberEntity = getNewMemberEntity();
        return memberService.generateLogin(memberEntity, TestConstant.MEMBER_PROFILE);
    }

    protected Login getNewLogin(MemberEntity member) {
        return memberService.generateLogin(member, TestConstant.MEMBER_PROFILE);
    }

    protected KeywordSearchResponse getKeywordSearch(Login login, String keyword) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("keyword", keyword))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String jsonContent = response.getContentAsString();
        return objectMapper.readValue(jsonContent, KeywordSearchResponse.class);
    }

    protected void enrollNewCourseWithVideo(Login login) throws Exception {
        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .queryParam("use_schedule", "false")
                .content(objectMapper.writeValueAsString(
                        NewLecture.createWithoutSchedule(ContentConstant.VIDEO_CODE_LIST[0]))));
    }

    protected void enrollNewCourseWithPlaylistSchedule(Login login, NewLecture newLecture) throws Exception {
        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .queryParam("use_schedule", "true")
                .content(objectMapper.writeValueAsString(newLecture)));
    }

    protected void enrollNewCourseWithPlaylist(Login login) throws Exception {
        MvcResult postResult = mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("use_schedule", "false")
                        .content(objectMapper.writeValueAsString(
                                NewLecture.createWithoutSchedule(ContentConstant.PLAYLIST_CODE))))
                .andReturn();
    }

    protected CourseDetail registerNewVideo(Long memberId, String videoCode) {
        EnrolledCourseInfo courseInfo = courseService.enroll(memberId, getNewLectureWithoutSchedule(videoCode),
                false, null);

        CourseDetail courseDetail = courseService.getCourseDetail(courseInfo.getCourseId());

        return courseDetail;
    }

    protected NewLecture getNewLectureWithoutSchedule(String lectureCode) {
        return NewLecture.builder()
                .lectureCode(lectureCode)
                .build();
    }

    protected void saveContents() {
        savePlaylist();
        saveVideoList();
        savePlaylistVideo();
        saveSummaryList();
        saveQuizList();
        saveQuizOptionList();
        saveRecommend();
    }

    private void savePlaylist() {
        jdbcTemplate.update(ContentConstant.PLAYLIST_INSERT_SQL, ContentConstant.PLAYLIST_CODE,
                new Timestamp(System.currentTimeMillis() - 60 * 1000));
    }

    private void saveVideoList() {
        for (int i = 0; i < ContentConstant.VIDEO_CODE_LIST.length; i++) {
            jdbcTemplate.update(ContentConstant.VIDEO_INSERT_SQL[i], ContentConstant.VIDEO_CODE_LIST[i],
                    new Timestamp(System.currentTimeMillis()));
        }
    }

    private void savePlaylistVideo() {
        for (int i = 0; i < ContentConstant.VIDEO_CODE_LIST.length; i++) {
            jdbcTemplate.update(ContentConstant.PLAYLIST_VIDEO_INSERT_SQL, 1, i + 1, i);
        }
    }

    private void saveSummaryList() {
        for (String summaryInsertSql : ContentConstant.SUMMARY_INSERT_SQL) {
            jdbcTemplate.update(summaryInsertSql);
        }
    }

    private void saveQuizList() {
        for (String quizInsertSql : ContentConstant.QUIZ_INSERT_SQL) {
            jdbcTemplate.update(quizInsertSql);
        }
    }

    private void saveQuizOptionList() {
        for (String quizOptionInsertSql : ContentConstant.QUIZ_OPTION_INSERT_SQL) {
            jdbcTemplate.update(quizOptionInsertSql);
        }
    }

    private void saveRecommend() {
        for (int i = 1; i <=4 ; i++) {
            jdbcTemplate.update(ContentConstant.RECOMMEND_INSERT_SQL, i);
        }
    }
}
