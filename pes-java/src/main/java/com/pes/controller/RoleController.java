package com.pes.controller;

import com.pes.annotation.RequirePermission;
import com.pes.common.Result;
import com.pes.dto.request.RoleAssignReq;
import com.pes.entity.SysRole;
import com.pes.service.SysRoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 * 提供角色的 CRUD 操作及菜单权限分配
 * 需要对应的 role:* 权限
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    /** 角色服务 */
    private final SysRoleService sysRoleService;

    /**
     * 构造器注入角色服务
     */
    public RoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    /**
     * 查询角色列表
     *
     * @param keyword 可选的角色名称关键字过滤
     * @return 角色列表
     */
    @GetMapping
    @RequirePermission("role:list")
    public Result<List<SysRole>> list(@RequestParam(name = "keyword", required = false) String keyword) {
        return Result.ok(sysRoleService.search(keyword));
    }

    /**
     * 根据 ID 查询角色详情
     *
     * @param id 角色 ID
     * @return 角色实体
     */
    @GetMapping("/{id}")
    @RequirePermission("role:view")
    public Result<SysRole> getById(@PathVariable("id") Long id) {
        return Result.ok(sysRoleService.getById(id));
    }

    /**
     * 新增角色
     *
     * @param role 角色实体
     * @return 新增后的角色
     */
    @PostMapping
    @RequirePermission("role:add")
    public Result<SysRole> create(@RequestBody SysRole role) {
        return Result.ok(sysRoleService.create(role));
    }

    /**
     * 修改角色
     *
     * @param id   角色 ID
     * @param role 角色实体
     * @return 修改后的角色
     */
    @PutMapping("/{id}")
    @RequirePermission("role:edit")
    public Result<SysRole> update(@PathVariable("id") Long id, @RequestBody SysRole role) {
        return Result.ok(sysRoleService.update(id, role));
    }

    /**
     * 删除角色
     *
     * @param id 角色 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @RequirePermission("role:delete")
    public Result<Void> delete(@PathVariable("id") Long id) {
        sysRoleService.delete(id);
        return Result.ok();
    }

    /**
     * 为角色分配菜单权限
     *
     * @param roleId 角色 ID
     * @param req    包含 menuIds 的请求体
     * @return 操作结果
     */
    @PostMapping("/{roleId}/assign")
    @RequirePermission("role:assign")
    public Result<Void> assignMenu(@PathVariable("roleId") Long roleId, @RequestBody RoleAssignReq req) {
        sysRoleService.assignMenu(roleId, req.getMenuIds());
        return Result.ok();
    }
}