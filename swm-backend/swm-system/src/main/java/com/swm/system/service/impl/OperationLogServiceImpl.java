package com.swm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swm.common.entity.SwmOperationLog;
import com.swm.common.entity.SysConfig;
import com.swm.system.mapper.SwmOperationLogMapper;
import com.swm.system.mapper.SysConfigMapper;
import com.swm.system.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private SwmOperationLogMapper swmOperationLogMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public void save(SwmOperationLog log) {
        swmOperationLogMapper.insert(log);
        // Clean up if exceeding retention limit
        int limit = getRetentionLimit();
        Long total = swmOperationLogMapper.countAll();
        if (total > limit) {
            int toDelete = (int) (total - limit);
            swmOperationLogMapper.deleteOldest(toDelete);
        }
    }

    @Override
    public Page<SwmOperationLog> queryPage(Integer page, Integer size, String username, String module, String level, String startDate, String endDate) {
        Page<SwmOperationLog> p = new Page<>(page, size);
        QueryWrapper<SwmOperationLog> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like("username", username);
        }
        if (StringUtils.hasText(module)) {
            wrapper.like("module", module);
        }
        if (StringUtils.hasText(level)) {
            wrapper.eq("level", level);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge("create_time", startDate);
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le("create_time", endDate + " 23:59:59");
        }
        wrapper.orderByDesc("create_time");
        return swmOperationLogMapper.selectPage(p, wrapper);
    }

    @Override
    public int getRetentionLimit() {
        SysConfig config = sysConfigMapper.selectByKey("LOG_RETENTION_LIMIT");
        if (config != null && StringUtils.hasText(config.getConfigValue())) {
            try {
                return Integer.parseInt(config.getConfigValue());
            } catch (NumberFormatException e) {
                return 5000;
            }
        }
        return 5000;
    }

    @Override
    public void setRetentionLimit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("保留上限必须大于0");
        }
        SysConfig config = sysConfigMapper.selectByKey("LOG_RETENTION_LIMIT");
        if (config != null) {
            config.setConfigValue(String.valueOf(limit));
            config.setUpdateTime(LocalDateTime.now());
            sysConfigMapper.updateById(config);
        } else {
            config = new SysConfig();
            config.setConfigKey("LOG_RETENTION_LIMIT");
            config.setConfigValue(String.valueOf(limit));
            config.setDescription("操作日志保留上限条数");
            config.setUpdateTime(LocalDateTime.now());
            sysConfigMapper.insert(config);
        }
    }
}
