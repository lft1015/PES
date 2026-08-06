package com.pes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pes.dto.request.UserCreateReq;
import com.pes.entity.SysUser;

import java.util.List;

/**
 * 用户服务接口
 * 继承 MyBatis-Plus IService，提供用户的 CRUD 及个人信息管理
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据关键字搜索用户
     *
     * @param keyword 搜索关键字（用户名/昵称）
     * @return 用户列表
     */
    List<SysUser> search(String keyword);

    /**
     * 创建用户
     *
     * @param req 用户创建请求
     * @return 创建后的用户
     */
    SysUser create(UserCreateReq req);

    /**
     * 更新用户
     *
     * @param id  用户 ID
     * @param req 用户更新请求
     * @return 更新后的用户
     */
    SysUser update(Long id, UserCreateReq req);

    /**
     * 删除用户
     *
     * @param id 用户 ID
     */
    void delete(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUser getByUsername(String username);

    /**
     * 更新用户个人信息（昵称、邮箱、手机号）
     *
     * @param username 用户名
     * @param req      用户信息
     * @return 更新后的用户
     */
    SysUser updateProfile(String username, SysUser req);

    /**
     * 修改密码
     *
     * @param username    用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(String username, String oldPassword, String newPassword);
}