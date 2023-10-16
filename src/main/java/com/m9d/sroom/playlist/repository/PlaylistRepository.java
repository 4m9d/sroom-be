package com.m9d.sroom.playlist.repository;
import com.m9d.sroom.playlist.PlaylistDto;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepository {

    PlaylistDto save(PlaylistDto playlistDto);

    PlaylistDto getById(Long playlistId);

    PlaylistDto getByCode(String playlistCode);

    Optional<PlaylistDto> findByCode(String code);

    PlaylistDto updateById(Long playlistId, PlaylistDto playlistDto);

    List<PlaylistDto> getTopRatedOrder(int limit);

    HashSet<String> getCodeSetByMemberId(Long memberId);

    List<PlaylistDto> getRandomByChannel(String channel, int limit);

    List<PlaylistDto> getViewCountOrderByChannel(String channel, int limit);

    List<PlaylistDto> getLatestOrderByChannel(String channel, int limit);
}
