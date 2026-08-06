/**
 * Element Plus 全量图标映射
 * 用户填写图标名 → 匹配到 Element Plus 图标则渲染，否则显示文字
 */
import * as ElementPlusIcons from '@element-plus/icons-vue'

const iconMap = {}

// 直接遍历所有导出，Vue SFC 编译后的组件有 render 或 __name
for (const [name, value] of Object.entries(ElementPlusIcons)) {
  if (value != null && typeof value !== 'string' && typeof value !== 'boolean') {
    iconMap[name.toLowerCase()] = value
  }
}

/**
 * @param {string} name 图标名（PascalCase / kebab-case 均可）
 * @returns 图标组件 或 null
 */
export function getIcon(name) {
  if (!name) return null
  return iconMap[name.toLowerCase().replace(/[-_\s]/g, '')] || null
}
