package com.pes.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 * 记录用户每次登录/IP/状态，表名 sys_login_log
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录用户名 */
    private String username;

    /** 登录 IP */
    private String ip;

    /** 状态：1-成功，0-失败 */
    private Integer status;

    /** 登录时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime loginTime;
}