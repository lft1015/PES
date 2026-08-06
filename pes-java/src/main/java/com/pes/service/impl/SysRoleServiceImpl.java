
package com.pes.service.impl;

import com.pes.entity.SysRole;
import com.pes.entity.SysRoleMenu;
import com.pes.exception.BusinessException;
import com.pes.exception.ErrorCode;
import com.pes.mapper.SysRoleMapper;
import com.pes.mapper.SysRoleMenuMapper;
import com.pes.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现
 * 继承 MyBatis-Plus ServiceImpl，提供角色的 CRUD 及菜单分配
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    /** 角色-菜单关联 Mapper，用于菜单分配和级联删除 */
    private final SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 构造器注入
     */
    public SysRoleServiceImpl(SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    @Override
    public List<SysRole> search(String keyword) {
        LambdaQueryWrapper<SysRole> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(SysRole::getName, keyword)
              .or().like(SysRole::getCode, keyword);
        }
        qw.orderByAsc(SysRole::getId);
        return baseMapper.selectList(qw);
    }

    @Override
    public SysRole create(SysRole role) {
        if (baseMapper.exists(new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, role.getCode()))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "角色编码已存在");
        }
        baseMapper.insert(role);
        return role;
    }

    @Override
    public SysRole update(Long id, SysRole role) {
        SysRole existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        if (!existing.getCode().equals(role.getCode()) &&
                baseMapper.exists(new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getCode, role.getCode())
                        .ne(SysRole::getId, id))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "角色编码已存在");
        }

        role.setId(id);
        baseMapper.updateById(role);
        return role;
    }

    /**
     * 删除角色，同时删除角色-菜单关联
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (baseMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        baseMapper.deleteById(id);
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
    }

    /**
     * 为角色分配菜单权限：先清空原有关联，再批量插入新关联
     */
    @Override
    @Transactional
    public void assignMenu(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }
}