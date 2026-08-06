package com.pes.security.service;

import com.pes.entity.SysMenu;
import com.pes.entity.SysRole;
import com.pes.entity.SysUser;
import com.pes.entity.SysUserRole;
import com.pes.mapper.SysMenuMapper;
import com.pes.mapper.SysRoleMapper;
import com.pes.mapper.SysUserMapper;
import com.pes.mapper.SysUserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详情服务实现
 * Spring Security 核心服务，从数据库加载用户信息、角色和权限
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /** 用户 Mapper */
    private final SysUserMapper sysUserMapper;
    /** 用户-角色关联 Mapper */
    private final SysUserRoleMapper sysUserRoleMapper;
    /** 角色 Mapper */
    private final SysRoleMapper sysRoleMapper;
    /** 菜单 Mapper（用于获取权限标识） */
    private final SysMenuMapper sysMenuMapper;

    /**
     * 构造器注入所有 Mapper
     */
    public UserDetailsServiceImpl(SysUserMapper sysUserMapper, SysUserRoleMapper sysUserRoleMapper,
                                  SysRoleMapper sysRoleMapper, SysMenuMapper sysMenuMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysMenuMapper = sysMenuMapper;
    }

    /**
     * 根据用户名加载用户详情
     * 查询流程：用户 → 用户角色关联 → 角色 + 权限
     * 权限格式：角色用 ROLE_xxx，菜单权限用 menu.permission 原始值
     *
     * @param username 用户名
     * @return UserDetails 包含用户名、密码、权限集合
     * @throws UsernameNotFoundException 用户不存在时抛出
     * @throws DisabledException         用户已禁用时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() == 0) {
            throw new DisabledException("用户已禁用: " + username);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getId()));

        for (SysUserRole userRole : userRoles) {
            SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));

                List<SysMenu> menus = sysMenuMapper.selectMenuByRoleId(role.getId());
                for (SysMenu menu : menus) {
                    if (menu.getPermission() != null && !menu.getPermission().isEmpty()) {
                        authorities.add(new SimpleGrantedAuthority(menu.getPermission()));
                    }
                }
            }
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}