package com.swm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.common.entity.SwmOperationLog;

public interface OperationLogService {
    void save(SwmOperationLog log);
    Page<SwmOperationLog> queryPage(Integer page, Integer size, String username, String module, String level, String startDate, String endDate);
    int getRetentionLimit();
    void setRetentionLimit(int limit);
}
