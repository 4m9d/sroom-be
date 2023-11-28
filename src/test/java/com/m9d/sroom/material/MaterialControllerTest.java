package com.m9d.sroom.material;

import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.common.entity.MemberEntity;
import com.m9d.sroom.util.ControllerTest;
import com.m9d.sroom.util.TestConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@Transactional
public class MaterialControllerTest extends ControllerTest {

    @Test
    @DisplayName("생성이 완료된 강의자료를 받아오는데 성공합니다.")
    void getMaterials200() throws Exception {
        //given
        MemberEntity member = getNewMemberEntity();
        CourseDetail courseDetail = registerNewVideo(member.getMemberId(), TestConstant.VIDEO_CODE);
        Long videoId = courseDetail.getLastViewVideo().getVideoId();
        insertSummaryAndQuizzes(courseDetail.getCourseId(), videoId);

        //expected
        mockMvc.perform(get("/materials/lectures/{videoId}", videoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authrization", getNewLogin(member).getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.total_quiz_count", is(3)))
                .andExpect(jsonPath("$.quizzes[0].type", is(1)))
                .andExpect(jsonPath("$.quizzes[1].type", is(2)))
                .andExpect(jsonPath("$.quizzes[2].type", is(3)))
                .andExpect(jsonPath("$.quizzes[0].select_option_5").isNotEmpty())
                .andExpect(jsonPath("$.summary.is_modified", is(false)))
                .andExpect(jsonPath("$.summary.content").isNotEmpty());
    }

    @Test
    @DisplayName("생성이 미완료된 강의자료의 경우 status = 0 입니다.")
    void getMaterials202() throws Exception {
        //given
        MemberEntity member = getNewMemberEntity();
        CourseDetail courseDetail = registerNewVideo(member.getMemberId(), TestConstant.VIDEO_CODE);
        Long videoId = courseDetail.getLastViewVideo().getVideoId();

        //expected
        mockMvc.perform(get("/materials/lectures/{videoId}", videoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authrization", getNewLogin(member).getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(0)));
    }
}
