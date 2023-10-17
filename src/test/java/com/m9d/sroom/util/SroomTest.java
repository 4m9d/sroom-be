package com.m9d.sroom.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.m9d.sroom.course.CourseService;
import com.m9d.sroom.dashboard.DashboardService;
import com.m9d.sroom.lecture.LectureService;
import com.m9d.sroom.member.service.MemberService;
import com.m9d.sroom.youtube.api.YoutubeApi;
import com.m9d.sroom.youtube.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SroomTest {


    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected CourseService courseService;

    @Autowired
    protected DashboardService dashboardService;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected LectureService lectureService;

    @Autowired
    protected DateUtil dateUtil;

    @Autowired
    protected YoutubeApi youtubeApi;

    @Autowired
    protected YoutubeService youtubeService;

    protected static final String VIDEO_CODE = "Z9dvM7qgN9s";
    protected static final String PLAYLIST_CODE = "PLv2d7VI9OotQUUsgcTBHuy5vJkSgzVHL0";
    protected static final int PLAYLIST_VIDEO_COUNT = 5;
    protected static final String LECTURETITLE = "손경식의 브이로그 - 소마센터 출퇴근편";
    protected static final String CHANNEL = "수경식";
    protected static final String LECTURE_DESCRIPTION = "잠실을 거쳐 선릉 소마센터까지";
    protected static final String THUMBNAIL = "https://i.ytimg.com/vi/Pc6n6HgWU5c/mqdefault.jpg";
}
