package com.pes.service.impl;

import com.pes.dto.response.MenuTreeResp;
import com.pes.entity.SysMenu;
import com.pes.exception.BusinessException;
import com.pes.exception.ErrorCode;
import com.pes.mapper.SysMenuMapper;
import com.pes.mapper.SysRoleMenuMapper;
import com.pes.service.SysMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 * 继承 MyBatis-Plus ServiceImpl，提供菜单的 CRUD 及树形构建
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    /** 角色-菜单关联 Mapper，用于级联删除 */
    private final SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 构造器注入
     */
    public SysMenuServiceImpl(SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    @Override
    public List<SysMenu> search(String keyword) {
        LambdaQueryWrapper<SysMenu> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(SysMenu::getName, keyword);
        }
        qw.orderByAsc(SysMenu::getSort);
        return baseMapper.selectList(qw);
    }

    @Override
    public SysMenu create(SysMenu menu) {
        baseMapper.insert(menu);
        return menu;
    }

    @Override
    public SysMenu update(Long id, SysMenu menu) {
        if (baseMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        menu.setId(id);
        baseMapper.updateById(menu);
        return menu;
    }

    /**
     * 递归删除菜单及其所有子菜单，同时删除角色-菜单关联
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (baseMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }

        List<SysMenu> children = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, id));
        for (SysMenu child : children) {
            delete(child.getId());
        }

        baseMapper.deleteById(id);
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<com.pes.entity.SysRoleMenu>().eq(com.pes.entity.SysRoleMenu::getMenuId, id));
    }

    @Override
    public List<MenuTreeResp> getMenuTree(String keyword) {
        LambdaQueryWrapper<SysMenu> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(SysMenu::getName, keyword);
        }
        qw.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = baseMapper.selectList(qw);

        Map<Long, List<SysMenu>> menuMap = menus.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        return buildTree(0L, menuMap);
    }

    /**
     * 递归构建菜单树
     *
     * @param parentId 父菜单 ID
     * @param menuMap  按 parentId 分组的菜单 Map
     * @return 菜单树列表
     */
    private List<MenuTreeResp> buildTree(Long parentId, Map<Long, List<SysMenu>> menuMap) {
        List<MenuTreeResp> tree = new ArrayList<>();
        List<SysMenu> children = menuMap.getOrDefault(parentId, new ArrayList<>());

        for (SysMenu menu : children) {
            MenuTreeResp node = new MenuTreeResp();
            node.setId(menu.getId());
            node.setParentId(menu.getParentId());
            node.setName(menu.getName());
            node.setPath(menu.getPath());
            node.setComponent(menu.getComponent());
            node.setIcon(menu.getIcon());
            node.setPermission(menu.getPermission());
            node.setSort(menu.getSort());
            node.setType(menu.getType());
            node.setStatus(menu.getStatus());
            node.setCreateTime(menu.getCreateTime());
            node.setChildren(buildTree(menu.getId(), menuMap));
            tree.add(node);
        }

        return tree;
    }
}