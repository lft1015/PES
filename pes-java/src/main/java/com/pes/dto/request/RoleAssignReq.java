package com.pes.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 角色菜单分配请求 DTO
 */
@Data
public class RoleAssignReq {

    /** 要分配的菜单 ID 列表 */
    private List<Long> menuIds;
}