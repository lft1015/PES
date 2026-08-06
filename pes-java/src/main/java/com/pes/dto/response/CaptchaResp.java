package com.pes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResp {

    /** 验证码唯一标识，登录时需回传 */
    private String key;

    /** Base64编码的验证码图片，前端可直接用于 <img src="data:image/png;base64,..."> */
    private String image;
}