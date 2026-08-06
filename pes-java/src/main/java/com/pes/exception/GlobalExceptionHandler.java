package com.pes.exception;

import com.pes.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一将异常转换为 { code, msg } 格式返回前端。
 * 关键原则：返回给前端的 msg 包含真实的错误原因，方便排查问题。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 业务异常 ====================

    /**
     * 业务异常处理
     * 根据 ErrorCode 中定义的 httpStatus 返回对应的 HTTP 状态码
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("[业务异常] {} {} — code={}, httpStatus={}, msg={}",
                request.getMethod(), request.getRequestURI(),
                e.getErrorCode().getCode(), e.getErrorCode().getHttpStatus(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(Result.fail(e.getErrorCode().getCode(), e.getMessage()));
    }

    // ==================== 参数校验异常 ====================

    /**
     * @Valid 校验失败（JSON 请求体）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("[参数校验失败] {} {} — {}", request.getMethod(), request.getRequestURI(), msg);
        return ResponseEntity.badRequest()
                .body(Result.fail(ErrorCode.BAD_REQUEST.getCode(), msg));
    }

    /**
     * @Valid 校验失败（表单 / Query 参数）
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e, HttpServletRequest request) {
        String msg = e.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("[参数绑定失败] {} {} — {}", request.getMethod(), request.getRequestURI(), msg);
        return ResponseEntity.badRequest()
                .body(Result.fail(ErrorCode.BAD_REQUEST.getCode(), msg));
    }

    // ==================== 认证 / 授权异常 ====================

    /**
     * 权限不足异常处理
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        log.warn("[权限不足] {} {} — {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.fail(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage()));
    }

    /**
     * 密码错误异常处理
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Result<Void>> handleBadCredentialsException(
            BadCredentialsException e, HttpServletRequest request) {
        log.warn("[密码错误] {} {}", request.getMethod(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.fail(ErrorCode.USERNAME_PASSWORD_ERROR.getCode(),
                        ErrorCode.USERNAME_PASSWORD_ERROR.getMessage()));
    }

    /**
     * 用户已禁用异常处理
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Result<Void>> handleDisabledException(
            DisabledException e, HttpServletRequest request) {
        log.warn("[用户已禁用] {} {} — {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.fail(ErrorCode.USER_DISABLED.getCode(), ErrorCode.USER_DISABLED.getMessage()));
    }

    /**
     * 认证服务内部异常处理（如 UserDetailsService 抛出异常）
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<Result<Void>> handleInternalAuthException(
            InternalAuthenticationServiceException e, HttpServletRequest request) {
        log.error("[认证服务异常] {} {}", request.getMethod(), request.getRequestURI(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.fail(ErrorCode.USERNAME_PASSWORD_ERROR.getCode(),
                        ErrorCode.USERNAME_PASSWORD_ERROR.getMessage()));
    }

    /**
     * 通用认证异常兜底处理
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(
            AuthenticationException e, HttpServletRequest request) {
        log.warn("[认证失败] {} {} — {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.fail(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage()));
    }

    // ==================== 常见运行时异常 ====================

    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        log.warn("[非法参数] {} {} — {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(Result.fail(ErrorCode.BAD_REQUEST.getCode(), e.getMessage()));
    }

    // ==================== 兜底异常 ====================

    /**
     * 兜底：所有未被上面捕获的异常
     * 关键修正：返回真实异常信息，而不是写死的"服务器内部错误"
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        // 完整堆栈记录到后端日志
        log.error("[系统异常] {} {} — {}: {}",
                request.getMethod(), request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage(), e);
        // 前端收到具体异常类型和原因，而非笼统的"服务器内部错误"
        String frontMsg = e.getClass().getSimpleName() + ": " +
                (e.getMessage() != null ? e.getMessage() : "无详细信息");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.fail(ErrorCode.INTERNAL_ERROR.getCode(), frontMsg));
    }
}