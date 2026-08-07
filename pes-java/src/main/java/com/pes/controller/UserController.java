package com.pes.controller;

import com.pes.annotation.RequirePermission;
import com.pes.common.Result;
import com.pes.dto.request.UserCreateReq;
import com.pes.entity.SysUser;
import com.pes.service.SysUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * 提供用户的 CRUD 操作、个人信息查看/修改、密码修改
 * 需要对应的 user:* 权限
 */
@RestController
@RequestMapping("/users")
public class UserController {

    /** 用户服务 */
    private final SysUserService sysUserService;

    /**
     * 构造器注入用户服务
     */
    public UserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 查询用户列表
     *
     * @param keyword 可选的用户名/昵称关键字过滤
     * @return 用户列表
     */
    @GetMapping
    @RequirePermission("user:list")
    public Result<List<SysUser>> list(@RequestParam(name = "keyword", required = false) String keyword) {
        return Result.ok(sysUserService.search(keyword));
    }

    /**
     * 根据 ID 查询用户详情（含分配的角色 ID，用于编辑回显，一个用户一个角色）
     *
     * @param id 用户 ID
     * @return { user: 用户信息, roleId: 角色 ID }
     */
    @GetMapping("/{id}")
    @RequirePermission("user:view")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        user.setPassword(null);
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("user", user);
        data.put("roleId", sysUserService.getRoleId(id));
        return Result.ok(data);
    }

    /**
     * 新增用户
     *
     * @param req 用户创建请求
     * @return 新增后的用户
     */
    @PostMapping
    @RequirePermission("user:add")
    public Result<SysUser> create(@RequestBody UserCreateReq req) {
        return Result.ok(sysUserService.create(req));
    }

    /**
     * 修改用户
     *
     * @param id  用户 ID
     * @param req 用户修改请求
     * @return 修改后的用户
     */
    @PutMapping("/{id}")
    @RequirePermission("user:edit")
    public Result<SysUser> update(@PathVariable Long id, @RequestBody UserCreateReq req) {
        return Result.ok(sysUserService.update(id, req));
    }

    /**
     * 删除用户
     *
     * @param id 用户 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @RequirePermission("user:delete")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.ok();
    }

    /**
     * 用户下拉选项（轻量，无需 user:list 权限，供日志等页面使用）
     *
     * @return 用户选项列表，包含 username 和 nickname
     */
    @GetMapping("/options")
    public Result<List<Map<String, String>>> options() {
        List<SysUser> users = sysUserService.search(null);
        List<Map<String, String>> opts = users.stream().map(u -> {
            Map<String, String> m = new java.util.HashMap<>();
            m.put("username", u.getUsername());
            m.put("nickname", u.getNickname() != null ? u.getNickname() : u.getUsername());
            return m;
        }).collect(java.util.stream.Collectors.toList());
        return Result.ok(opts);
    }

    /**
     * 获取当前登录用户的个人信息
     *
     * @return 用户信息（密码已脱敏）
     */
    @GetMapping("/profile")
    public Result<SysUser> profile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = sysUserService.getByUsername(authentication.getName());
        user.setPassword(null);
        return Result.ok(user);
    }

    /**
     * 修改当前登录用户的个人信息
     *
     * @param req 用户信息
     * @return 修改后的用户信息（密码已脱敏）
     */
    @PutMapping("/profile")
    public Result<SysUser> updateProfile(@RequestBody SysUser req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = sysUserService.updateProfile(authentication.getName(), req);
        user.setPassword(null);
        return Result.ok(user);
    }

    /**
     * 修改当前登录用户的密码
     *
     * @param req 包含 oldPassword 和 newPassword 的请求体
     * @return 操作结果
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        sysUserService.changePassword(authentication.getName(),
                req.get("oldPassword"), req.get("newPassword"));
        return Result.ok();
    }
}