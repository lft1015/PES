/**
 * 本地存储工具
 *
 * 对 localStorage 的封装，提供 JSON 自动序列化/反序列化，
 * 用于 Vuex/Pinia Store 的状态持久化（如 Token、用户信息等）
 */
export const storage = {
  /**
   * 存储数据（自动 JSON 序列化）
   * @param {string} key   - 键名
   * @param {*}      value - 值（支持对象、数组等复杂类型）
   */
  set(key, value) {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('Storage set error:', error)
    }
  },

  /**
   * 读取数据（自动 JSON 反序列化）
   * @param {string} key - 键名
   * @returns {*} 解析后的值，不存在或解析失败返回 null
   */
  get(key) {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : null
    } catch (error) {
      console.error('Storage get error:', error)
      return null
    }
  },

  /**
   * 删除指定数据
   * @param {string} key - 键名
   */
  remove(key) {
    try {
      localStorage.removeItem(key)
    } catch (error) {
      console.error('Storage remove error:', error)
    }
  },

  /**
   * 清空所有本地存储数据
   */
  clear() {
    try {
      localStorage.clear()
    } catch (error) {
      console.error('Storage clear error:', error)
    }
  }
}
