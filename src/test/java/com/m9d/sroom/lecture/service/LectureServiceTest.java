package com.m9d.sroom.lecture.service;

import com.m9d.sroom.util.ServiceTest;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LectureServiceTest extends ServiceTest {

    @Test
    void shouldReturnUnescapeHtml() {
        //given
        String escapeHtml = "Blaze Transformations &amp; Rescues! w/ AJ | 90 Minute Compilation | B";

        //when
        String unescapeHtml = lectureService.unescapeHtml(escapeHtml);

        //when
        System.out.println(escapeHtml);
        System.out.println(unescapeHtml);
        assertTrue(unescapeHtml.equals("Blaze Transformations & Rescues! w/ AJ | 90 Minute Compilation | B"));
    }
}
