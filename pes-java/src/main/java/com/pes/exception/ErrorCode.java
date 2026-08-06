
package com.pes.exception;

/**
 * 统一错误码枚举
 * <p>
 * 编码规则：
 * <ul>
 *   <li>200：成功</li>
 *   <li>4xx：HTTP 标准错误</li>
 *   <li>5xx：服务器错误</li>
 *   <li>1xxx：用户模块</li>
 *   <li>2xxx：角色模块</li>
 *   <li>3xxx：菜单模块</li>
 * </ul>
 */
public enum ErrorCode {

    /** 成功 */
    SUCCESS(200, "成功", 200),
    /** 请求参数错误 */
    BAD_REQUEST(400, "请求参数错误", 400),
    /** 未登录或 Token 过期 */
    UNAUTHORIZED(401, "未登录或Token过期", 401),
    /** 权限不足 */
    FORBIDDEN(403, "权限不足", 403),
    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在", 404),
    /** 服务器内部错误 */
    INTERNAL_ERROR(500, "服务器内部错误", 500),

    /** 用户不存在 */
    USER_NOT_FOUND(1001, "用户不存在", 404),
    /** 用户名或密码错误 */
    USERNAME_PASSWORD_ERROR(1002, "用户名或密码错误", 401),
    /** 用户已禁用 */
    USER_DISABLED(1003, "用户已禁用", 403),
    /** 验证码错误 */
    CAPTCHA_ERROR(1004, "验证码错误", 400),
    /** 用户名已存在 */
    USERNAME_EXISTS(1005, "用户名已存在", 409),

    /** 角色不存在 */
    ROLE_NOT_FOUND(2001, "角色不存在", 404),
    /** 菜单不存在 */
    MENU_NOT_FOUND(3001, "菜单不存在", 404);

    /** 错误码 */
    private final int code;
    /** 错误消息 */
    private final String message;
    /** 对应的 HTTP 状态码 */
    private final int httpStatus;

    ErrorCode(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}