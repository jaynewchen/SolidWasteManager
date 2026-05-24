package com.swm.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.common.Result;
import com.swm.common.entity.SwmOperationLog;
import com.swm.system.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system/logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:log:list')")
    public Result<Page<SwmOperationLog>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<SwmOperationLog> result = operationLogService.queryPage(page, size, username, module, level, startDate, endDate);
        return Result.ok(result);
    }

    @GetMapping("/config")
    @PreAuthorize("hasAuthority('system:log:list')")
    public Result<Map<String, Object>> getConfig() {
        int limit = operationLogService.getRetentionLimit();
        Map<String, Object> data = new HashMap<>();
        data.put("retentionLimit", limit);
        return Result.ok(data);
    }

    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> setConfig(@RequestBody Map<String, Object> body) {
        int limit = Integer.parseInt(body.get("retentionLimit").toString());
        operationLogService.setRetentionLimit(limit);
        Map<String, Object> data = new HashMap<>();
        data.put("retentionLimit", limit);
        return Result.ok(data);
    }
}
