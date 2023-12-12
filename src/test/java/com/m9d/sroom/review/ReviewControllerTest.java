package com.m9d.sroom.review;

import com.m9d.sroom.common.entity.jdbctemplate.ReviewEntity;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.review.dto.ReviewSubmitRequest;
import com.m9d.sroom.util.ControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static com.m9d.sroom.util.constant.ContentConstant.VIDEO_TITLE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class ReviewControllerTest extends ControllerTest {

    @Test
    @DisplayName("리뷰 작성에 성공합니다.")
    public void postReview200() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);

        int submittedRating = 4;
        String reviewContent = "재미있어요";

        //when  
        MvcResult mvcResult = mockMvc.perform(post("/reviews/lectures/{lecture_id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .content(objectMapper.writeValueAsString(
                        ReviewSubmitRequest.createReview(submittedRating,reviewContent))))
                .andReturn();

        //then
        String getReviewQuery = "SELECT * FROM REVIEW";
        ReviewEntity review = jdbcTemplate.queryForObject(getReviewQuery, ReviewEntity.getRowmapper());
        Assertions.assertEquals(submittedRating, review.getSubmittedRating());
        Assertions.assertEquals(reviewContent, review.getContent());
    }

    @Test
    @DisplayName("코스 리뷰 작성 가능 강의 리스트를 조회합니다.")
    public void lectureList200() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login); //course 1
        enrollNewCourseWithVideo(login); //course 2


        //when
        String updateMaxDurationQuery = "UPDATE COURSEVIDEO SET max_duration = ? WHERE course_video_id = ?";
        jdbcTemplate.update(updateMaxDurationQuery, 3000, 1); //course1 만 50% 이상 수강

        //expected
        mockMvc.perform(get("/reviews/courses/{course_id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", login.getAccessToken()))
                .andExpect(jsonPath("$.lectures[0].title").value(VIDEO_TITLE))
                .andExpect(jsonPath("$.lectures[0].is_review_allowed").value(true));

        mockMvc.perform(get("/reviews/courses/{course_id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(jsonPath("$.lectures[0].title").value(VIDEO_TITLE))
                .andExpect(jsonPath("$.lectures[0].is_review_allowed").value(false));
    }
}
