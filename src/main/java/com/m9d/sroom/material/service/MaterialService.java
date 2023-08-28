package com.m9d.sroom.material.service;

import com.m9d.sroom.material.dto.response.Material;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MaterialService {
    public Material getMaterials(Long memberId, Long videoId) {
        return Material.builder().build();
    }
}
