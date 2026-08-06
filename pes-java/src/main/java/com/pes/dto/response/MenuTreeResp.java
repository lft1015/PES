package com.pes.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单树响应 DTO
 * 用于前端渲染侧边栏菜单和权限树
 */
@Data
public class MenuTreeResp {

    /** 菜单 ID */
    private Long id;

    /** 父菜单 ID，0 表示顶级菜单 */
    private Long parentId;

    /** 菜单名称 */
    private String name;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 菜单图标 */
    private String icon;

    /** 权限标识 */
    private String permission;

    /** 排序号 */
    private Integer sort;

    /** 菜单类型：M-目录，C-菜单，F-按钮 */
    private Integer type;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 子菜单列表 */
    private List<MenuTreeResp> children;
}