
package com.pes.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户创建/修改请求 DTO
 */
@Data
public class UserCreateReq {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（新增时必填，修改时可不填） */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 角色 ID（一个用户对应一个角色） */
    private Long roleId;
}