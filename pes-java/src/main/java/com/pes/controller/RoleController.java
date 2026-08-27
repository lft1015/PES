package com.pes.controller;

import com.pes.annotation.LogOperation;
import com.pes.annotation.RequirePermission;
import com.pes.common.Result;
import com.pes.dto.request.RoleAssignReq;
import com.pes.entity.SysRole;
import com.pes.service.SysRoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     * 根据 ID 查询角色详情（含已分配的菜单 ID）
     * 该接口专供"分配权限"弹窗回显使用，故用 role:assign 权限而非 role:view，
     * 否则有分配权限但无查询权限的角色将无法打开分配权限弹窗
     *
     * @param id 角色 ID
     * @return 角色实体（含 menuIds）
     */
    @GetMapping("/{id}")
    @RequirePermission("role:assign")
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
    @LogOperation("新增角色")
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
    @LogOperation("修改角色")
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
    @LogOperation("删除角色")
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
    @LogOperation("分配角色权限")
    public Result<Void> assignMenu(@PathVariable("roleId") Long roleId, @RequestBody RoleAssignReq req) {
        sysRoleService.assignMenu(roleId, req.getMenuIds());
        return Result.ok();
    }

    /**
     * 角色下拉选项（轻量，无需 role:list 权限）
     * 供"新增/编辑用户"弹窗选择角色使用：有 user:add/user:edit 权限即可使用
     *
     * @return 角色选项列表，包含 id 和 name
     */
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> options() {
        List<SysRole> roles = sysRoleService.search(null);
        List<Map<String, Object>> opts = roles.stream().map(r -> {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", r.getId());
            m.put("name", r.getName());
            return m;
        }).collect(java.util.stream.Collectors.toList());
        return Result.ok(opts);
    }
}