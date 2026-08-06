package com.pes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pes.entity.SysOperLog;
import com.pes.mapper.SysOperLogMapper;
import com.pes.service.SysOperLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现
 * 继承 MyBatis-Plus ServiceImpl，自动拥有 CRUD 方法
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {
}