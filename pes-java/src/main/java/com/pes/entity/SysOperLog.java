package com.pes.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 * 记录用户操作的方法、耗时、IP 等信息，表名 sys_oper_log
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人用户名 */
    private String username;

    /** 操作描述 */
    private String operation;

    /** 目标类名 */
    private String className;

    /** 目标方法名 */
    private String methodName;

    /** 请求参数 */
    private String params;

    /** 耗时（毫秒） */
    private Long time;

    /** 操作 IP */
    private String ip;

    /** 状态：1-成功，0-失败 */
    private Integer status;

    /** 操作时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}