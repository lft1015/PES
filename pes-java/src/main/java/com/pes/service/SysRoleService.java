package com.pes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pes.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 * 继承 MyBatis-Plus IService，提供角色的 CRUD 及菜单分配
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 根据关键字搜索角色
     *
     * @param keyword 搜索关键字
     * @return 角色列表
     */
    List<SysRole> search(String keyword);

    /**
     * 创建角色
     *
     * @param role 角色实体
     * @return 创建后的角色
     */
    SysRole create(SysRole role);

    /**
     * 更新角色
     *
     * @param id   角色 ID
     * @param role 角色实体
     * @return 更新后的角色
     */
    SysRole update(Long id, SysRole role);

    /**
     * 删除角色（同时删除用户-角色关联、角色-菜单关联）
     *
     * @param id 角色 ID
     */
    void delete(Long id);

    /**
     * 为角色分配菜单权限
     *
     * @param roleId  角色 ID
     * @param menuIds 菜单 ID 列表
     */
    void assignMenu(Long roleId, List<Long> menuIds);
}