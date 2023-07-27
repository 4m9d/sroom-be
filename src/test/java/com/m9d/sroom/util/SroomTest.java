package com.m9d.sroom.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.course.service.CourseService;
import com.m9d.sroom.lecture.service.LectureService;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
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
    protected MemberRepository memberRepository;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected LectureService lectureService;

    @Autowired
    protected CourseRepository courseRepository;


    protected static final String VIDEO_CODE = "Z9dvM7qgN9s";
    protected static final String PLAYLIST_CODE = "PLv2d7VI9OotQ1F92Jp9Ce7ovHEsuRQB3Y";
}
