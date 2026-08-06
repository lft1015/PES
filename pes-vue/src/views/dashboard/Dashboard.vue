<template>
  <div class="dora-dashboard">
    <!-- ===== 欢迎区 ===== -->
    <div class="welcome-section">
      <div class="welcome-text">
        <h1 class="welcome-title">
          👋 欢迎回来，{{ userStore.nickname || '管理员' }}！
        </h1>
      </div>
      <div class="doraemon-container">
        <img :src="dashboardImg" alt="哆啦A梦" class="doraemon-img" />
      </div>
    </div>

    <!-- ===== 统计卡片 ===== -->
    <div class="stats-grid">
      <div class="stats-card card-users">
        <div class="card-icon"><el-icon :size="22"><UserFilled /></el-icon></div>
        <div class="card-info">
          <span class="card-value">{{ stats.userCount }}</span>
          <span class="card-label">用户总数</span>
        </div>
      </div>
      <div class="stats-card card-roles">
        <div class="card-icon"><el-icon :size="22"><Avatar /></el-icon></div>
        <div class="card-info">
          <span class="card-value">{{ stats.roleCount }}</span>
          <span class="card-label">角色数量</span>
        </div>
      </div>
      <div class="stats-card card-menus">
        <div class="card-icon"><el-icon :size="22"><Menu /></el-icon></div>
        <div class="card-info">
          <span class="card-value">{{ stats.menuCount }}</span>
          <span class="card-label">菜单数量</span>
        </div>
      </div>
      <div class="stats-card card-online">
        <div class="card-icon"><el-icon :size="22"><Connection /></el-icon></div>
        <div class="card-info">
          <span class="card-value online">在线</span>
          <span class="card-label">系统运行中</span>
        </div>
      </div>
    </div>

    <!-- ===== 快捷操作 + 语录 ===== -->
    <div class="bottom-section">
      <div class="quick-actions">
        <div class="section-title">🚀 快捷操作</div>
        <div class="actions-grid">
          <div class="action-card" @click="$router.push('/system/user')">
            <div class="action-icon action-blue"><el-icon :size="18"><User /></el-icon></div>
            <span>用户管理</span>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>
          <div class="action-card" @click="$router.push('/system/role')">
            <div class="action-icon action-red"><el-icon :size="18"><Lock /></el-icon></div>
            <span>角色管理</span>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>
          <div class="action-card" @click="$router.push('/system/menu')">
            <div class="action-icon action-yellow"><el-icon :size="18"><Menu /></el-icon></div>
            <span>菜单管理</span>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>
          <div class="action-card" @click="$router.push('/profile')">
            <div class="action-icon action-green"><el-icon :size="18"><Setting /></el-icon></div>
            <span>个人中心</span>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <div class="dora-quotes">
        <div class="section-title">💬 哆啦A梦语录</div>
        <div class="quotes-list">
          <p class="quote-item" v-for="(q, i) in quotes" :key="i">"{{ q.text }}" — {{ q.from }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 仪表盘页面 — 逻辑层
 * 加载首页统计数据（用户数、角色数、菜单数）并展示哆啦A梦语录
 */
import { reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { getDashboardStats } from '@/api/dashboard'
/** Element Plus 图标 */
import { User, UserFilled, Lock, Menu, Avatar, Connection, Setting, ArrowRight } from '@element-plus/icons-vue'
/** 哆啦A梦插画 */
import dashboardImg from '@/img/dashboard.jpg'

const userStore = useUserStore()

/** 统计数据（响应式） */
const stats = reactive({ userCount: 0, roleCount: 0, menuCount: 0 })

/** 哆啦A梦经典语录（静态数据） */
const quotes = [
  { text: '这世界有七千个地方，但只有一个地方属于你', from: '大雄的恐龙' },
  { text: '有些事情我不看透，不是我笨，只是我太善良', from: '哆啦A梦' },
  { text: '你总是这样，遇到困难就想放弃', from: '大雄的宇宙漂流记' },
  { text: '用这份力量，去帮助需要帮助的人', from: '大雄与铁人兵团' },
  { text: '梦想是一个天真的词，实现梦想是一个残酷的词', from: '哆啦A梦：伴我同行' },
  { text: '就算没有我，你也能好好地活下去吗', from: '哆啦A梦：伴我同行' },
  { text: '把每天的小努力累积起来，就能改变未来', from: '大雄的秘密道具博物馆' },
  { text: '真正的强者不是没有眼泪，而是含着眼泪奔跑', from: '大雄与奇迹之岛' },
]

/** 加载仪表盘统计数据 */
const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    stats.userCount = res.userCount || 0
    stats.roleCount = res.roleCount || 0
    stats.menuCount = res.menuCount || 0
  } catch { /* 失败时使用默认值 0 */ }
}

/** 组件挂载后加载数据 */
onMounted(() => loadStats())
</script>

<style scoped>
.dora-dashboard {
  --dora-blue: #4BA3E3;
  --dora-red: #E74C3C;
  --dora-bell: #F9A825;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 8px;
  flex: none;
  height: calc(100vh - 60px - 32px - 24px);
  background: linear-gradient(160deg, #E3F2FD 0%, #BBDEFB 40%, #90CAF9 100%);
}

/* ===== 欢迎区 ===== */
.welcome-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #4BA3E3 0%, #1E6CB8 100%);
  border-radius: 16px;
  padding: 20px 32px;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(75, 163, 227, 0.25);
}
.welcome-title {
  font-size: 22px; font-weight: 700; color: #fff; margin: 0;
}
.doraemon-container { flex-shrink: 0; }
.doraemon-img {
  width: 80px; height: auto;
  animation: dora-float 3s ease-in-out infinite;
}
@keyframes dora-float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

/* ===== 统计卡片 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  flex-shrink: 0;
}
.stats-card {
  background: #fff;
  border-radius: 14px;
  padding: 16px 18px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.card-icon {
  width: 42px; height: 42px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.card-users .card-icon { background: linear-gradient(135deg, #E3F2FD, #BBDEFB); color: var(--dora-blue); }
.card-roles .card-icon { background: linear-gradient(135deg, #FFEBEE, #FFCDD2); color: var(--dora-red); }
.card-menus .card-icon { background: linear-gradient(135deg, #FFF8E1, #FFECB3); color: var(--dora-bell); }
.card-online .card-icon { background: linear-gradient(135deg, #E8F5E9, #C8E6C9); color: #4CAF50; }
.card-info { display: flex; flex-direction: column; }
.card-value { font-size: 22px; font-weight: 800; color: #1a1a2e; line-height: 1.1; }
.card-value.online { font-size: 16px; color: #4CAF50; }
.card-label { font-size: 12px; color: #999; }

/* ===== 底部双栏 ===== */
.bottom-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
.quick-actions, .dora-quotes {
  background: #fff;
  border-radius: 14px;
  padding: 16px 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.section-title {
  font-size: 15px; font-weight: 700; color: #1a1a2e;
  margin-bottom: 10px; flex-shrink: 0;
}
.actions-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  flex: 1;
}
.action-card {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f8fafc;
  cursor: pointer;
  transition: all 0.2s;
  border: 1.5px solid transparent;
}
.action-card:hover { background: #E3F2FD; border-color: var(--dora-blue); transform: translateY(-1px); }
.action-card span { flex: 1; font-size: 13px; font-weight: 600; color: #333; }
.action-arrow { color: #ccc; font-size: 13px; flex-shrink: 0; transition: transform 0.2s; }
.action-card:hover .action-arrow { transform: translateX(3px); color: var(--dora-blue); }
.action-icon {
  width: 34px; height: 34px;
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; flex-shrink: 0;
}
.action-blue  { background: linear-gradient(135deg, #4BA3E3, #1E6CB8); }
.action-red   { background: linear-gradient(135deg, #E74C3C, #C0392B); }
.action-yellow{ background: linear-gradient(135deg, #F9A825, #F57F17); }
.action-green { background: linear-gradient(135deg, #4CAF50, #2E7D32); }

/* ===== 语录 ===== */
.quotes-list {
  flex: 1;
  display: flex; flex-direction: column; gap: 8px;
  overflow-y: auto;
}
.quote-item {
  font-size: 12.5px; color: #666; margin: 0;
  line-height: 1.5; font-style: italic;
  padding: 10px 12px;
  background: linear-gradient(135deg, #f0f7ff, #e8f4fd);
  border-radius: 10px;
}

@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .dora-dashboard { padding: 4px; gap: 6px; }
  .welcome-section { padding: 16px 20px; }
  .welcome-title { font-size: 18px; }
  .doraemon-img { width: 60px; }
  .stats-grid { grid-template-columns: 1fr 1fr; }
  .bottom-section { grid-template-columns: 1fr; }
}
</style>