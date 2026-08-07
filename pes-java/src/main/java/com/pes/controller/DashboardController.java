package com.pes.controller;

import com.pes.common.Result;
import com.pes.entity.SysMenu;
import com.pes.mapper.SysMenuMapper;
import com.pes.mapper.SysRoleMapper;
import com.pes.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘控制器
 * 提供系统首页的统计数据概览
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    /** 用户 Mapper */
    private final SysUserMapper sysUserMapper;

    /** 角色 Mapper */
    private final SysRoleMapper sysRoleMapper;

    /** 菜单 Mapper */
    private final SysMenuMapper sysMenuMapper;

    /**
     * 构造器注入各 Mapper
     */
    public DashboardController(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper,
                               SysMenuMapper sysMenuMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysMenuMapper = sysMenuMapper;
    }

    /**
     * 获取仪表盘统计数据
     * 返回用户数、角色数、菜单数
     *
     * @return 包含 userCount、roleCount、menuCount 的统计结果
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", sysUserMapper.selectCount(null));
        data.put("roleCount", sysRoleMapper.selectCount(null));
        // 仅统计可点击进入页面的菜单：type=1（菜单）且 component 非空、非 Layout（目录仅作分组，无独立页面）
        // 排除按钮（type=2）权限节点和目录（component=Layout）
        data.put("menuCount", sysMenuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getType, 1)
                        .isNotNull(SysMenu::getComponent)
                        .ne(SysMenu::getComponent, "")
                        .ne(SysMenu::getComponent, "Layout")));
        return Result.ok(data);
    }
}