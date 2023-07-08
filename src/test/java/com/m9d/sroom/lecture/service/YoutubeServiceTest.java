package com.m9d.sroom.lecture.service;

import com.m9d.sroom.util.ServiceTest;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class YoutubeServiceTest extends ServiceTest {

    @Test
    void shouldReturnPrevPageToken() {
        String prevPageToken = "prevPageToken";
        String result = youtubeService.chooseTokenOrNull(null, prevPageToken);
        assertEquals(prevPageToken, result);
    }
    @Test
    void shouldReturnNull() {
        String result = youtubeService.chooseTokenOrNull(null, null);
        assertNull(result);
    }
}
