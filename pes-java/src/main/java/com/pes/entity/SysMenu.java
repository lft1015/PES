package com.pes.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单/权限实体
 * 表名 sys_menu，支持树形结构（parentId），含乐观锁
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父菜单 ID，0 表示顶级 */
    private Long parentId;

    /** 菜单名称 */
    private String name;

    /** 路由路径 */
    private String path;

    /** 前端组件路径 */
    private String component;

    /** 菜单图标 */
    private String icon;

    /** 权限标识（如 user:list） */
    private String permission;

    /** 排序号 */
    private Integer sort;

    /** 类型：0-目录(分组)，1-菜单(页面)，2-按钮(操作权限) */
    private Integer type;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 乐观锁版本号 */
    @Version
    private Integer version;
}