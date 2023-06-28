package com.m9d.sroom.lecture.service;

import com.m9d.sroom.lecture.dto.response.KeywordSearchRes;
import com.m9d.sroom.lecture.dto.response.VideoDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LectureService {

    private final YoutubeService youtubeService;
}
