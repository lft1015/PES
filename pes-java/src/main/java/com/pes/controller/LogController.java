package com.pes.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pes.annotation.RequirePermission;
import com.pes.common.Result;
import com.pes.entity.SysLoginLog;
import com.pes.entity.SysOperLog;
import com.pes.service.SysLoginLogService;
import com.pes.service.SysOperLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 日志管理控制器
 * 提供操作日志和登录日志的分页查询、删除、批量删除、清空功能
 * 需要 log:list 或 log:delete 权限
 */
@RestController
@RequestMapping("/logs")
public class LogController {

    /** 操作日志服务 */
    private final SysOperLogService sysOperLogService;

    /** 登录日志服务 */
    private final SysLoginLogService sysLoginLogService;

    /**
     * 构造器注入日志服务
     */
    public LogController(SysOperLogService sysOperLogService, SysLoginLogService sysLoginLogService) {
        this.sysOperLogService = sysOperLogService;
        this.sysLoginLogService = sysLoginLogService;
    }

    // ==================== 操作日志 ====================

    /**
     * 分页查询操作日志
     */
    @GetMapping("/operation")
    @RequirePermission("log:list")
    public Result<Page<SysOperLog>> listOperLog(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "operation", required = false) String operation) {
        QueryWrapper<SysOperLog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        if (operation != null && !operation.isEmpty()) {
            wrapper.like("operation", operation);
        }
        Page<SysOperLog> result = sysOperLogService.page(new Page<>(page, pageSize), wrapper);
        return Result.ok(result);
    }

    /**
     * 删除操作日志
     */
    @DeleteMapping("/operation/{id}")
    @RequirePermission("log:delete")
    public Result<Void> deleteOperLog(@PathVariable Long id) {
        sysOperLogService.removeById(id);
        return Result.ok();
    }

    /**
     * 批量删除操作日志
     */
    @DeleteMapping("/operation/batch")
    @RequirePermission("log:delete")
    public Result<Void> batchDeleteOperLog(@RequestBody List<Long> ids) {
        sysOperLogService.removeByIds(ids);
        return Result.ok();
    }

    /**
     * 清空所有操作日志
     */
    @DeleteMapping("/operation/clear")
    @RequirePermission("log:delete")
    public Result<Void> clearOperLog() {
        sysOperLogService.remove(new QueryWrapper<>());
        return Result.ok();
    }

    // ==================== 登录日志 ====================

    /**
     * 分页查询登录日志
     */
    @GetMapping("/login")
    @RequirePermission("log:list")
    public Result<Page<SysLoginLog>> listLoginLog(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "status", required = false) Integer status) {
        QueryWrapper<SysLoginLog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("login_time");
        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        Page<SysLoginLog> result = sysLoginLogService.page(new Page<>(page, pageSize), wrapper);
        return Result.ok(result);
    }

    /**
     * 删除登录日志
     */
    @DeleteMapping("/login/{id}")
    @RequirePermission("log:delete")
    public Result<Void> deleteLoginLog(@PathVariable Long id) {
        sysLoginLogService.removeById(id);
        return Result.ok();
    }

    /**
     * 批量删除登录日志
     */
    @DeleteMapping("/login/batch")
    @RequirePermission("log:delete")
    public Result<Void> batchDeleteLoginLog(@RequestBody List<Long> ids) {
        sysLoginLogService.removeByIds(ids);
        return Result.ok();
    }

    /**
     * 清空所有登录日志
     */
    @DeleteMapping("/login/clear")
    @RequirePermission("log:delete")
    public Result<Void> clearLoginLog() {
        sysLoginLogService.remove(new QueryWrapper<>());
        return Result.ok();
    }
}