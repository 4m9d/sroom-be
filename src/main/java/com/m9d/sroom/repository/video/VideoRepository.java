package com.m9d.sroom.repository.video;

import com.m9d.sroom.course.dto.SchedulingVideo;
import com.m9d.sroom.global.model.Video;
import com.m9d.sroom.lecture.dto.response.VideoBrief;

import java.util.List;
import java.util.Optional;

public interface VideoRepository {

    Long save(Video video);

    Optional<Video> findByCode(String videoCode);

    Long findVideoIdByCode(String videoCode);

    void update(Video video);

    List<VideoBrief> getVideoBriefByCourseIdAndSection(Long courseId, int section);

    Integer getMaterialStatusByCode(String videoCode);


}
