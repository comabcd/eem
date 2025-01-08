<template>
  <div class="layout">
    <!-- 顶部栏 -->
    <el-header class="header">
      <div class="header-left" style="margin-left: 36px;">
        <div class="system-title">
          <span class="title">企业无忧</span>
          <span class="role-tag">{{ getRoleTitle(userRole) }}</span>
        </div>
        <div class="time-display">
          <div class="date-section">
            <span class="year">{{ dateInfo.year }}年</span>
            <span class="month-day">{{ dateInfo.month }}月{{ dateInfo.day }}日</span>
            <span class="week">星期{{ dateInfo.week }}</span>
          </div>
          <div class="time-section">
            <span class="time">{{ dateInfo.time }}</span>
          </div>
        </div>
      </div>
      <div class="header-right">
        <el-switch
         v-model="isDark"
         @change="toggleDark"
         inline-prompt
        >
          <template #active-action>
            <el-icon><Moon /></el-icon>
          </template>
          <template #inactive-action>
            <el-icon><Sunny /></el-icon>
          </template>
        </el-switch>
      </div>
    </el-header>

    <el-container class="main-container">
      <!-- 侧边菜单 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
        <div class="menu-toggle" @click="toggleCollapse">
          <el-icon>
            <Fold v-if="!isCollapse"/>
            <Expand v-else/>
          </el-icon>
        </div>
        <el-menu
          :default-active="route.path"
          class="el-menu-vertical"
          :collapse="isCollapse"
          router
          background-color="#ffffff"
          text-color="#666666"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <!-- 系统管理员菜单 -->
          <template v-if="userRole === 'admin'">
            <el-menu-item index="/system/user">
              <el-icon><User /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            
            <el-menu-item index="/system/post">
              <el-icon><Briefcase /></el-icon>
              <template #title>岗位管理</template>
            </el-menu-item>
            
            <el-menu-item index="/system/dept">
              <el-icon><OfficeBuilding /></el-icon>
              <template #title>部门管理</template>
            </el-menu-item>
            
            <el-menu-item index="/system/dict">
              <el-icon><Files /></el-icon>
              <template #title>基础数据管理</template>
            </el-menu-item>
          </template>
          
          <!-- 部门管理员菜单 -->
          <template v-if="userRole === 'dept_admin'">
            <el-menu-item index="/dept/employee">
              <el-icon><UserFilled /></el-icon>
              <template #title>员工管理</template>
            </el-menu-item>
            
            <el-menu-item index="/dept/leave">
              <el-icon><Timer /></el-icon>
              <template #title>请假管理</template>
            </el-menu-item>
            
            <el-menu-item index="/dept/attendance">
              <el-icon><Calendar /></el-icon>
              <template #title>考勤管理</template>
            </el-menu-item>
          </template>
          
          <!-- 人力资源管理员菜单 -->
          <template v-if="userRole === 'hr_admin'">
            <el-menu-item index="/hr/employee">
              <el-icon><UserFilled /></el-icon>
              <template #title>员工管理</template>
            </el-menu-item>

            <el-menu-item index="/hr/onboard">
              <el-icon><Plus /></el-icon>
              <template #title>入职办理</template>
            </el-menu-item>

            <el-menu-item index="/hr/resign">
              <el-icon><DocumentAdd /></el-icon>
              <template #title>离职办理</template>
            </el-menu-item>
          </template>
          
          <!-- 普通员工菜单 -->
          <template v-if="userRole === 'employee'">
            <el-menu-item index="/employee/leave-apply">
              <el-icon><DocumentAdd /></el-icon>
              <template #title>请假申请</template>
            </el-menu-item>
            
            <el-menu-item index="/employee/attendance-record">
              <el-icon><Stamp /></el-icon>
              <template #title>考勤打卡</template>
            </el-menu-item>
          </template>

          <!-- 添加分隔线和退出按钮 -->
          <div class="menu-divider"></div>
          <el-menu-item @click="handleLogout" index="logout" style="margin-top: auto;">
            <el-icon><SwitchButton /></el-icon>
            <template #title>Log out</template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主要内容区域 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useDark, useToggle } from '@vueuse/core'
import {
  House,
  User,
  UserFilled,
  Briefcase,
  OfficeBuilding,
  Files,
  Timer,
  Calendar,
  DocumentAdd,
  Stamp,
  Plus,
  Fold,
  Expand,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

const isDark = useDark()
const toggleDark = useToggle(isDark)

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapse = ref(false)
const userRole = computed(() => localStorage.getItem('userRole'))
const username = computed(() => localStorage.getItem('userName'))

const dateInfo = ref({
  year: '',
  month: '',
  day: '',
  week: '',
  time: ''
})

// 切换菜单折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 获取角色标题
const getRoleTitle = (role) => {
  const roleMap = {
    'admin': '系统管理员',
    'dept_admin': '部门管理员',
    'hr_admin': '人力资源管理员',
    'employee': '员工'
  }
  return roleMap[role] || '未知角色'
}

// 退出登录
const handleLogout = async () => {
  try {
    await userStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
  }
}

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
  
  dateInfo.value = {
    year,
    month,
    day,
    week,
    time: `${hours}:${minutes}:${seconds}`
  }
}

onMounted(() => {
  updateTime()
  const timer = setInterval(updateTime, 1000)
  onUnmounted(() => {
    clearInterval(timer)
  })
})
</script>

<style scoped>
.layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.header {
  background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%);
  color: #fff;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.system-title {
  display: flex;
  align-items: center;
  gap: 10px;

  .title {
    font-size: 22px;
    font-weight: bold;
    color: #fff;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  }

  .role-tag {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.9);
    background-color: rgba(255, 255, 255, 0.2);
    padding: 2px 8px;
    border-radius: 4px;
  }
}

.header-right {
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    color: #fff;
    padding: 4px 12px;
    border-radius: 4px;
    transition: background-color 0.3s;
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.1);
    }
    
    .el-icon {
      margin-left: 5px;
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  background-color: #f5f7fa;
}

.aside {
  background-color: #ffffff;
  transition: width 0.3s;
  position: relative;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.menu-toggle {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666666;
  font-size: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s;

  &:hover {
    background-color: #f5f7fa;
  }
}

.el-menu {
  border-right: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

.el-menu-item {
  &:hover {
    background-color: #f5f7fa !important;
  }

  &.is-active {
    background-color: #ecf5ff !important;
    border-right: 3px solid #409EFF;
  }
}

.main {
  background-color: #f5f7fa;
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.time-display {
  background-color: rgba(255, 255, 255, 0.1);
  padding: 4px 12px;
  border-radius: 4px;
  text-align: center;
  min-width: 300px;
}

.date-section {
  font-size: 14px;
  margin-bottom: 2px;
  color: rgba(255, 255, 255, 0.9);
}

.year,
.month-day,
.week {
  margin: 0 4px;
}

.time-section {
  font-size: 20px;
  font-weight: 300;
  color: #fff;
  font-family: "Arial", sans-serif;
}

.el-menu-vertical {
  height: calc(100% - 50px);  /* 减去折叠按钮的高度 */
  display: flex;
  flex-direction: column;
}

.menu-divider {
  margin: auto 0 0;  /* 将分隔线推到底部 */
  height: 1px;
  background-color: #dcdfe6;
}

/* 确保退出按钮在底部 */
.el-menu-item:last-child {
  margin-top: 0 !important;
  border-top: 1px solid #dcdfe6;
}
</style>
