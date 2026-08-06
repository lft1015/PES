package com.pes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pes.dto.response.MenuTreeResp;
import com.pes.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务接口
 * 继承 MyBatis-Plus IService，提供菜单的 CRUD 及树形查询
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 根据关键字搜索菜单
     *
     * @param keyword 搜索关键字
     * @return 菜单列表
     */
    List<SysMenu> search(String keyword);

    /**
     * 创建菜单
     *
     * @param menu 菜单实体
     * @return 创建后的菜单
     */
    SysMenu create(SysMenu menu);

    /**
     * 更新菜单
     *
     * @param id   菜单 ID
     * @param menu 菜单实体
     * @return 更新后的菜单
     */
    SysMenu update(Long id, SysMenu menu);

    /**
     * 删除菜单（级联删除子菜单及角色关联）
     *
     * @param id 菜单 ID
     */
    void delete(Long id);

    /**
     * 获取菜单树（前端 Sidebar 使用）
     *
     * @param keyword 搜索关键字，null 时返回全部
     * @return 菜单树列表
     */
    List<MenuTreeResp> getMenuTree(String keyword);
}