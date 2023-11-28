package com.m9d.sroom.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.m9d.sroom.course.CourseService;
import com.m9d.sroom.dashboard.DashboardService;
import com.m9d.sroom.lecture.LectureService;
import com.m9d.sroom.member.MemberService;
import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.search.SearchService;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.video.VideoService;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Disabled
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SroomTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
