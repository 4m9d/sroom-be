package com.m9d.sroom.lecture.dto.response;

import com.m9d.sroom.common.vo.PlaylistItem;
import com.m9d.sroom.common.vo.PlaylistWithItemList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "목차 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfo {

    @Schema(description = "목차 리스트")
    private List<Index> indexList;

    @Schema(description = "총 재생 시간", example = "245212")
    private int duration;

    @Schema(description = "영상 총 개수", example = "112")
    private int lectureCount;

    public IndexInfo(PlaylistWithItemList content) {
        List<Index> indexList = new ArrayList<>();
        for (PlaylistItem playlistItem : content.getPlaylistItemList()) {
            indexList.add(new Index(playlistItem));
        }
        this.indexList = indexList;
        this.duration = content.getPlaylistDuration();
        this.lectureCount = indexList.size();
    }
}
