
package com.pes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResp {

    /** JWT 访问令牌 */
    private String token;

    /** 令牌类型，固定为 Bearer */
    private String tokenType;

    /** 令牌过期时间（秒） */
    private Long expiresIn;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 角色标识列表 */
    private List<String> roles;

    /** 角色名称列表 */
    private List<String> roleNames;

    /** 权限标识列表 */
    private List<String> permissions;
}