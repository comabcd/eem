<template>
  <div class="welcome-container">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-header">
          <el-icon class="logo" :size="80"><ElementPlus /></el-icon>
          <h1>系统管理员端</h1>
        </div>
        <div class="feature-grid">
          <el-card v-for="(feature, index) in features" :key="index" class="feature-card" shadow="hover" @click="handleFeatureClick(feature)">
            <el-icon class="feature-icon" :size="32">
              <component :is="feature.icon" />
            </el-icon>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import {
  User,
  Briefcase,
  OfficeBuilding,
  Files,
  ElementPlus
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const userInfo = ref({
  name: userStore.realName
})

console.log(userInfo.value)

const currentTime = ref('')
let timer = null

// 更新时间
const updateTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  const week = ['日', '一', '二', '三', '四', '五', '六'][now.getDay()]
  
  currentTime.value = `${year}年${month}月${day}日 星期${week} ${hours}:${minutes}:${seconds}`
}

// 功能模块
const features = [
  {
    icon: 'User',
    title: '用户管理',
    description: '管理系统用户账号，分配用户角色和权限',
    path: '/system/user'
  },
  {
    icon: 'Briefcase',
    title: '岗位管理',
    description: '维护企业岗位信息，管理岗位设置',
    path: '/system/post'
  },
  {
    icon: 'OfficeBuilding',
    title: '部门管理',
    description: '维护企业组织架构，管理部门信息',
    path: '/system/dept'
  },
  {
    icon: 'Files',
    title: '基础数据管理',
    description: '维护系统基础数据，包括数据字典等',
    path: '/system/dict'
  }
]

// 功能模块点击
const handleFeatureClick = (feature) => {
  router.push(feature.path)
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.welcome-container {
  height: calc(100vh - 60px);
  background-color: #fff;
  position: relative;
  margin: -20px;
}

.welcome-card {
  height: 100%;
  border-radius: 0;
  border: none;
  
  :deep(.el-card__body) {
    height: 100%;
    padding: 20px;
  }
}

.welcome-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.welcome-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  font-size: 80px;
  color: #409EFF;
  margin-bottom: 20px;
}

.welcome-header h1 {
  font-size: 28px;
  color: #303133;
  margin: 0;
}

.welcome-info {
  text-align: center;
  margin-bottom: 40px;
}

.system-time {
  font-size: 18px;
  color: #606266;
  margin-bottom: 10px;
}

.welcome-text {
  font-size: 16px;
  color: #409EFF;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  width: 100%;
  max-width: 1200px;
  margin-top: 70px;
}

.feature-card {
  text-align: center;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  color: #409EFF;
  margin-bottom: 15px;
}

.feature-card h3 {
  font-size: 18px;
  color: #303133;
  margin: 10px 0;
}

.feature-card p {
  font-size: 14px;
  color: #606266;
  margin: 0;
  line-height: 1.5;
}
</style> 