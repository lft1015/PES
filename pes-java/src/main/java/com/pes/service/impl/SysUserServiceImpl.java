package com.pes.service.impl;

import com.pes.dto.request.UserCreateReq;
import com.pes.entity.SysRole;
import com.pes.entity.SysUser;
import com.pes.entity.SysUserRole;
import com.pes.exception.BusinessException;
import com.pes.exception.ErrorCode;
import com.pes.mapper.SysRoleMapper;
import com.pes.mapper.SysUserMapper;
import com.pes.mapper.SysUserRoleMapper;
import com.pes.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现
 * 继承 MyBatis-Plus ServiceImpl，提供用户的 CRUD 及个人信息管理
 * 业务规则：一个用户对应一个角色
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /** 用户-角色关联 Mapper */
    private final SysUserRoleMapper sysUserRoleMapper;
    /** 角色 Mapper（用于列表展示角色名称） */
    private final SysRoleMapper sysRoleMapper;
    /** 密码编码器 */
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造器注入
     */
    public SysUserServiceImpl(SysUserRoleMapper sysUserRoleMapper, SysRoleMapper sysRoleMapper,
                              PasswordEncoder passwordEncoder) {
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<SysUser> search(String keyword) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(SysUser::getUsername, keyword)
              .or().like(SysUser::getNickname, keyword);
        }
        qw.orderByAsc(SysUser::getId);
        List<SysUser> users = baseMapper.selectList(qw);

        // 填充每个用户的角色信息（一个用户一个角色）
        for (SysUser user : users) {
            SysUserRole userRole = sysUserRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
                    .eq(SysUserRole::getUserId, user.getId())
                    .last("LIMIT 1"));
            if (userRole != null) {
                SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
                if (role != null) {
                    user.setRoleId(role.getId());
                    user.setRoleName(role.getName());
                }
            }
        }
        return users;
    }

    /**
     * 创建用户，同时分配角色
     */
    @Override
    @Transactional
    public SysUser create(UserCreateReq req) {
        if (baseMapper.exists(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, req.getUsername()))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setStatus(req.getStatus() != null ? req.getStatus() : 1);
        baseMapper.insert(user);

        if (req.getRoleId() != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(req.getRoleId());
            sysUserRoleMapper.insert(userRole);
        }

        return user;
    }

    /**
     * 更新用户信息及角色分配：先删旧角色关联，再插入新角色关联
     */
    @Override
    @Transactional
    public SysUser update(Long id, UserCreateReq req) {
        SysUser user = baseMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (!user.getUsername().equals(req.getUsername()) &&
                baseMapper.exists(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, req.getUsername())
                        .ne(SysUser::getId, id))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户名已存在");
        }

        user.setUsername(req.getUsername());
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        user.setNickname(req.getNickname());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }
        baseMapper.updateById(user);

        // 仅当显式传入 roleId 时才更新角色分配（先删旧角色，再插入新角色，一个用户一个角色）；
        // 否则保留原有角色关联，避免前端表单未携带角色字段时误删用户全部角色导致权限丢失
        if (req.getRoleId() != null) {
            sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(id);
            userRole.setRoleId(req.getRoleId());
            sysUserRoleMapper.insert(userRole);
        }

        return user;
    }

    /**
     * 删除用户，同时删除用户-角色关联
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (baseMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        baseMapper.deleteById(id);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Override
    public SysUser getByUsername(String username) {
        SysUser user = baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public Long getRoleId(Long userId) {
        SysUserRole userRole = sysUserRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .last("LIMIT 1"));
        return userRole != null ? userRole.getRoleId() : null;
    }

    @Override
    public SysUser updateProfile(String username, SysUser req) {
        SysUser user = baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (req.getNickname() != null) {
            user.setNickname(req.getNickname());
        }
        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }
        baseMapper.updateById(user);
        return user;
    }

    /**
     * 修改密码：先校验旧密码，再加密保存新密码
     */
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        SysUser user = baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "原密码不正确");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        baseMapper.updateById(user);
    }
}