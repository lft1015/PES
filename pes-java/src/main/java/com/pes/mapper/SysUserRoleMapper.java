
package com.pes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pes.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-角色关联 Mapper
 * 继承 MyBatis-Plus BaseMapper，自动拥有 CRUD 方法
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}