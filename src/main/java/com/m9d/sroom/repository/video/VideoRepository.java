package com.m9d.sroom.repository.video;

import com.m9d.sroom.global.model.Video;
import com.m9d.sroom.lecture.dto.PlaylistInfoInSearch;
import com.m9d.sroom.lecture.dto.response.RecommendLecture;
import com.m9d.sroom.lecture.dto.response.VideoBrief;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VideoRepository {

    Long save(Video video);

    Optional<Video> findByCode(String videoCode);

    Optional<Video> findById(Long videoId);

    Long findIdByCode(String videoCode);

    void update(Video video);

    List<VideoBrief> getVideoBriefByCourseIdAndSection(Long courseId, int section);

    Integer getMaterialStatusByCode(String videoCode);

    void updateMaterialStatusByCode(String videoCode);

    Set<String> getCodeListByMemberId(Long memberId);

    List<RecommendLecture> getListSortedByRating();

    List<RecommendLecture> getRandomListByChannel(String channel, int limit);

    List<RecommendLecture> getMostViewedListByChannel(String channel, int limit);

    List<RecommendLecture> getLatestListByChannel(String channel, int limit);

    

}
