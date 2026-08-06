package com.pes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pes.entity.SysLoginLog;
import com.pes.mapper.SysLoginLogMapper;
import com.pes.service.SysLoginLogService;
import org.springframework.stereotype.Service;

/**
 * 登录日志服务实现
 * 继承 MyBatis-Plus ServiceImpl，自动拥有 CRUD 方法
 */
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {
}