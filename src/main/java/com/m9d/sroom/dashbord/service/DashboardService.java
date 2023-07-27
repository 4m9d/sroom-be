package com.m9d.sroom.dashbord.service;

import com.m9d.sroom.dashbord.dto.response.DashboardInfo;
import com.m9d.sroom.dashbord.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    public DashboardInfo getDashboard() {
        return null;
    }
}
