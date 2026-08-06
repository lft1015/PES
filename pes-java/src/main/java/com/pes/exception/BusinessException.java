package com.pes.exception;

import lombok.Getter;

/**
 * 业务异常
 * 用于抛出可预期的业务错误，由 GlobalExceptionHandler 统一处理返回前端
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 错误码枚举 */
    private final ErrorCode errorCode;

    /**
     * 使用 ErrorCode 中预定义的消息
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 使用自定义消息覆盖 ErrorCode 中的默认消息
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}