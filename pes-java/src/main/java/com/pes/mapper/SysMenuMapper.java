package com.pes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pes.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单 Mapper
 * 继承 MyBatis-Plus BaseMapper，提供菜单的 CRUD 及角色关联查询
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据角色 ID 查询该角色拥有的菜单列表
     *
     * @param roleId 角色 ID
     * @return 菜单列表
     */
    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId}")
    List<SysMenu> selectMenuByRoleId(Long roleId);
}