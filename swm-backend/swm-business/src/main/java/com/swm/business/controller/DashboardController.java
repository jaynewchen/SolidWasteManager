package com.swm.business.controller;

import com.swm.business.mapper.DashboardMapper;
import com.swm.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardMapper dashboardMapper;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('receiving:list','transfer:list','storage:list','treatment:list','testing:list','system:user:list')")
    public Result<Map<String, Object>> stats() {
        Long todayReceivingCount = dashboardMapper.countTodayReceiving();
        Long monthNetWeightKg = dashboardMapper.sumMonthNetWeight();
        Long pendingDisposalCount = dashboardMapper.countPendingDisposal();
        Long pendingCheckCount = dashboardMapper.countPendingCheck();

        // Convert kg to tons, 2 decimal places
        BigDecimal monthTotalWeight = BigDecimal.valueOf(monthNetWeightKg)
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("todayReceivingCount", todayReceivingCount);
        data.put("monthTotalWeight", monthTotalWeight);
        data.put("pendingDisposalCount", pendingDisposalCount);
        data.put("pendingCheckCount", pendingCheckCount);
        return Result.ok(data);
    }
}
