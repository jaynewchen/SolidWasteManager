package com.swm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("swm_operation_log")
public class SwmOperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String module;
    private String operation;
    private String description;
    private String level;
    private String requestMethod;
    private String requestUrl;
    private String requestParams;
    private String ipAddress;
    private Long costTime;
    private LocalDateTime createTime;
}
