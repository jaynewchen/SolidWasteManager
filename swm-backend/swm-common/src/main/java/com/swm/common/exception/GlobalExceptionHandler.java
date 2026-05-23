package com.swm.common.exception;

import com.swm.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        StringBuilder msg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            if (msg.length() > 0) {
                msg.append("; ");
            }
            msg.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage());
        });
        return Result.fail(40001, msg.toString());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDeniedException(AccessDeniedException e) {
        return Result.fail(40300, "无操作权限");
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("服务器内部错误", e);
        return Result.fail(50000, "服务器内部错误");
    }
}
