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
import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint point, OperationLog opLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result;
        try {
            result = point.proceed();
        } catch (Throwable e) {
            throw e;
        }

        long costTime = System.currentTimeMillis() - startTime;

        try {
            SwmOperationLog log = new SwmOperationLog();
            log.setModule(opLog.module());
            log.setOperation(opLog.operation());
            log.setLevel(opLog.level());
            log.setCreateTime(LocalDateTime.now());
            log.setCostTime(costTime);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Long) {
                log.setUserId((Long) auth.getPrincipal());
            }
            if (auth != null && auth.getDetails() instanceof String) {
                log.setUsername((String) auth.getDetails());
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.setRequestMethod(request.getMethod());
                log.setRequestUrl(request.getRequestURI());
                log.setIpAddress(request.getRemoteAddr());
            }

            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                try {
                    String params = objectMapper.writeValueAsString(args);
                    if (params != null && params.length() > 2000) {
                        params = params.substring(0, 2000);
                    }
                    log.setRequestParams(params);
                } catch (Exception e) {
                    log.setRequestParams("[serialization error]");
                }
            }

            operationLogService.save(log);
        } catch (Exception e) {
            // Logging should never break business logic
        }

        return result;
    }
}
