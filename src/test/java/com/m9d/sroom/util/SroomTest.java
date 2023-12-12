package com.m9d.sroom.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public static final String[] TABLE_NAMES = {
            "COURSE",
            "COURSE_DAILY_LOG",
            "COURSEQUIZ",
            "COURSEVIDEO",
            "LECTURE",
            "MATERIAL_FEEDBACK",
            "MEMBER",
            "PLAYLIST",
            "PLAYLISTVIDEO",
            "QUIZ",
            "QUIZ_OPTION",
            "RECOMMEND",
            "REVIEW",
            "SUMMARY",
            "VIDEO"
    };

    public static final String[] TABLE_IDS = {
            "course_id",
            "course_daily_log_id",
            "course_quiz_id",
            "course_video_id",
            "lecture_id",
            "feedback_id",
            "member_id",
            "playlist_id",
            "playlist_video_id",
            "quiz_id",
            "quiz_option_id",
            "recommend_id",
            "review_id",
            "summary_id",
            "video_id"
    };

    @AfterEach
    public void afterEach() {
        truncateTables(getTruncateQueries(), getResetIdQueries());
    }

    private List<String> getTruncateQueries() {
        List<String> queries = new ArrayList<>();
        for (String tableName : TABLE_NAMES) {
            queries.add("TRUNCATE TABLE " + tableName + ";");
        }
        return queries;
    }

    private List<String> getResetIdQueries() {
        List<String> queries = new ArrayList<>();
        for (int i = 0; i < TABLE_NAMES.length; i++) {
            queries.add("ALTER TABLE " + TABLE_NAMES[i] + " ALTER COLUMN " + TABLE_IDS[i] + " RESTART WITH 1;");
        }
        return queries;
    }

    private void truncateTables(final List<String> truncateQueries, final List<String> resetIdQueries) {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        truncateQueries.forEach(jdbcTemplate::execute);
        resetIdQueries.forEach(jdbcTemplate::execute);
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
