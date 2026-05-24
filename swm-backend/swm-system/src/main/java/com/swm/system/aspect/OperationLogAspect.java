package com.swm.system.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swm.common.annotation.OperationLog;
import com.swm.common.entity.SwmOperationLog;
import com.swm.system.service.OperationLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint point, OperationLog opLog) throws Throwable {
        Object result = point.proceed();

        try {
            SwmOperationLog log = new SwmOperationLog();
            log.setModule(opLog.module());
            log.setOperation(opLog.operation());
            log.setLevel(opLog.level());
            log.setDescription(buildDescription(opLog, result, point.getArgs()));
            log.setCreateTime(LocalDateTime.now());
            log.setCostTime(0L);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                if (auth.getPrincipal() instanceof Long) {
                    log.setUserId((Long) auth.getPrincipal());
                }
                if (auth.getDetails() instanceof String) {
                    log.setUsername((String) auth.getDetails());
                }
            }

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                log.setRequestMethod(request.getMethod());
                log.setRequestUrl(request.getRequestURI());
                log.setIpAddress(request.getRemoteAddr());
            }

            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                try {
                    String params = objectMapper.writeValueAsString(args);
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000);
                    }
                    log.setRequestParams(params);
                } catch (Exception ignored) {
                }
            }

            operationLogService.save(log);
        } catch (Exception ignored) {
        }

        return result;
    }

    private String buildDescription(OperationLog opLog, Object result, Object[] args) {
        String operation = opLog.operation();

        if (operation.contains("删除")) {
            if (args != null && args.length > 0 && args[0] instanceof Number) {
                return operation + " - ID: " + args[0];
            }
            return operation;
        }

        Object data = extractResultData(result);
        if (data == null) {
            return operation;
        }

        String username = getFieldValue(data, "username");
        if (username != null) {
            return operation + " - 用户: " + username;
        }

        String roleName = getFieldValue(data, "roleName");
        if (roleName != null) {
            return operation + " - 角色: " + roleName;
        }

        String batchNo = getFieldValue(data, "batchNo");
        if (batchNo != null) {
            return operation + " - 批次号: " + batchNo;
        }

        return operation;
    }

    private Object extractResultData(Object result) {
        if (result == null) return null;
        try {
            Method getData = result.getClass().getMethod("getData");
            return getData.invoke(result);
        } catch (Exception e) {
            return null;
        }
    }

    private String getFieldValue(Object obj, String fieldName) {
        if (obj == null) return null;
        try {
            String getter = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method m = obj.getClass().getMethod(getter);
            Object value = m.invoke(obj);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
