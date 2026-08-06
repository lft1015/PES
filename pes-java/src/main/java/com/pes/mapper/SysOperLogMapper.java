package com.pes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pes.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 * 继承 MyBatis-Plus BaseMapper，自动拥有 CRUD 方法
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}