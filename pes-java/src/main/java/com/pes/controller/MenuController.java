package com.pes.controller;

import com.pes.annotation.LogOperation;
import com.pes.annotation.RequirePermission;
import com.pes.common.Result;
import com.pes.dto.response.MenuTreeResp;
import com.pes.entity.SysMenu;
import com.pes.service.SysMenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 * 提供菜单的树形结构查询、CRUD 操作
 * 需要对应的 menu:* 权限
 */
@RestController
@RequestMapping("/menus")
public class MenuController {

    /** 菜单服务 */
    private final SysMenuService sysMenuService;

    /**
     * 构造器注入菜单服务
     */
    public MenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    /**
     * 获取菜单树（用于前端渲染侧边栏/权限树）
     * 无需权限，所有登录用户均可访问
     *
     * @param keyword 可选的菜单名称关键字过滤
     * @return 菜单树形结构列表
     */
    @GetMapping("/tree")
    public Result<List<MenuTreeResp>> getMenuTree(@RequestParam(name = "keyword", required = false) String keyword) {
        return Result.ok(sysMenuService.getMenuTree(keyword));
    }

    /**
     * 查询菜单列表
     *
     * @param keyword 可选的菜单名称关键字过滤
     * @return 菜单列表
     */
    @GetMapping
    @RequirePermission("menu:list")
    public Result<List<SysMenu>> list(@RequestParam(name = "keyword", required = false) String keyword) {
        return Result.ok(sysMenuService.search(keyword));
    }

    /**
     * 根据 ID 查询菜单详情
     *
     * @param id 菜单 ID
     * @return 菜单实体
     */
    @GetMapping("/{id}")
    @RequirePermission("menu:view")
    public Result<SysMenu> getById(@PathVariable("id") Long id) {
        return Result.ok(sysMenuService.getById(id));
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单实体
     * @return 新增后的菜单
     */
    @PostMapping
    @RequirePermission("menu:add")
    @LogOperation("新增菜单")
    public Result<SysMenu> create(@RequestBody SysMenu menu) {
        return Result.ok(sysMenuService.create(menu));
    }

    /**
     * 修改菜单
     *
     * @param id   菜单 ID
     * @param menu 菜单实体
     * @return 修改后的菜单
     */
    @PutMapping("/{id}")
    @RequirePermission("menu:edit")
    @LogOperation("修改菜单")
    public Result<SysMenu> update(@PathVariable("id") Long id, @RequestBody SysMenu menu) {
        return Result.ok(sysMenuService.update(id, menu));
    }

    /**
     * 删除菜单
     *
     * @param id 菜单 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @RequirePermission("menu:delete")
    @LogOperation("删除菜单")
    public Result<Void> delete(@PathVariable("id") Long id) {
        sysMenuService.delete(id);
        return Result.ok();
    }
}