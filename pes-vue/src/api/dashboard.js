/**
 * 仪表盘 API
 * 提供首页统计数据查询接口
 */
import request from './request'

/**
 * 获取仪表盘统计数据
 * @returns {Promise} { userCount, roleCount, menuCount, loginLogCount, operLogCount }
 */
export const getDashboardStats = () => {
    return request({
        url: '/dashboard/stats',
        method: 'get'
    })
}
