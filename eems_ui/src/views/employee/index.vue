<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧考勤打卡和统计信息 -->
      <el-col :span="16">
        <el-card class="box-card">
          <h1 style="text-align: center;font-size: 30px;font-weight: 600;color: rgb(15, 15, 15);">欢迎使用企业无忧员工服务系统</h1>
          
          <!-- 考勤打卡区域 -->
          <div class="attendance-section">
            <div class="check-buttons">
              <div class="check-button-wrapper">
                <el-button 
                  class="check-button"
                  :disabled="todayAttendance.checkInTime" 
                  @click="handleCheckIn"
                  circle
                >
                  <div class="button-content">
                    <el-icon><Timer /></el-icon>
                    <span>上班打卡</span>
                  </div>
                </el-button>
              </div>
              <div class="check-button-wrapper">
                <el-button 
                  class="check-button"
                  :disabled="!todayAttendance.checkInTime || todayAttendance.checkOutTime" 
                  @click="handleCheckOut"
                  circle
                >
                  <div class="button-content">
                    <el-icon><Timer /></el-icon>
                    <span>下班打卡</span>
                  </div>
                </el-button>
              </div>
            </div>
            <div class="attendance-info">
              <div class="time-info">
                <div class="time-item">
                  <span class="label">上班打卡</span>
                  <span class="value" :class="{ 'not-checked': !todayAttendance.checkInTime }">
                    {{ todayAttendance.checkInTime ? formatTime(todayAttendance.checkInTime) : '未打卡' }}
                  </span>
                </div>
                <div class="time-item">
                  <span class="label">下班打卡</span>
                  <span class="value" :class="{ 'not-checked': !todayAttendance.checkOutTime }">
                    {{ todayAttendance.checkOutTime ? formatTime(todayAttendance.checkOutTime) : '未打卡' }}
                  </span>
                </div>
              </div>
              <div class="status-info">
                <span class="label">考勤状态</span>
                <el-tag :type="getStatusType(todayAttendance.status)" class="status-tag">
                  {{ formatStatus(todayAttendance.status) }}
                </el-tag>
              </div>
            </div>
          </div>

          <!-- 请假信息卡片 -->
          <div class="leave-section">
            <el-card class="leave-card" shadow="hover" @click="goToLeave">
              <div class="card-content">
                <el-icon class="quick-icon"><Timer /></el-icon>
                <div class="info-section">
                  <h3 class="section-title">请假申请</h3>
                  <div class="leave-info">
                    <div class="total-days">
                      <span class="label">本月已请假：</span>
                      <span class="value highlight">{{ leaveStats.days || 0 }}</span>
                      <span class="unit">天</span>
                    </div>
                    <div class="status-list">
                      <div v-for="(count, status_name) in leaveStats.statusCount" 
                           :key="status_name" 
                           class="status-item">
                        <span class="status-name">{{ status_name }}：</span>
                        <span class="count">{{ count }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧考勤日历 -->
      <el-col :span="8">
        <el-card class="calendar-card">
          <template #header>
            <div class="calendar-header">
              <span>本月考勤日历</span>
              <div class="legend">
                <div class="legend-item">
                  <span class="dot normal"></span>
                  <span>正常</span>
                </div>
                <div class="legend-item">
                  <span class="dot late"></span>
                  <span>迟到</span>
                </div>
                <div class="legend-item">
                  <span class="dot early"></span>
                  <span>早退</span>
                </div>
                <div class="legend-item">
                  <span class="dot absent"></span>
                  <span>缺勤</span>
                </div>
                <div class="legend-item">
                  <span class="dot leave"></span>
                  <span>请假</span>
                </div>
              </div>
            </div>
          </template>
          
          <el-calendar v-model="currentDate">
            <template #dateCell="{ data }">
              <div class="calendar-cell" :class="getAttendanceClass(data)">
                <span>{{ data.day.split('-').pop().replace(/^0/, '') }}</span>
                <div class="attendance-dot" v-if="getAttendanceStatus(data)"></div>
              </div>
            </template>
          </el-calendar>
          
          <!-- 添加考勤统计 -->
          <div class="attendance-stats">
            <div class="stats-grid">
              <div class="stats-item normal">
                <span class="stats-label">正常</span>
                <span class="stats-value">{{ monthlyStats.normal || 0 }}</span>
              </div>
              <div class="stats-item late">
                <span class="stats-label">迟到</span>
                <span class="stats-value">{{ monthlyStats.late || 0 }}</span>
              </div>
              <div class="stats-item early">
                <span class="stats-label">早退</span>
                <span class="stats-value">{{ monthlyStats.early || 0 }}</span>
              </div>
              <div class="stats-item absent">
                <span class="stats-label">缺勤</span>
                <span class="stats-value">{{ monthlyStats.absent || 0 }}</span>
              </div>
              <div class="stats-item leave">
                <span class="stats-label">请假</span>
                <span class="stats-value">{{ monthlyStats.leave || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { Stamp, Timer } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const currentDate = ref(new Date())
const currentMonth = computed(() => currentDate.value.getMonth() + 1)
const clockTime = ref('')
const clockDate = ref('')

// 考勤和请假统计数据
const todayAttendance = ref({
  checkInTime: null,
  checkOutTime: null,
  status: null
})

const leaveStats = ref({
  days: 0,
  statusCount: {}
})

// 月度考勤数据
const monthlyAttendance = ref([])

// 月度考勤统计数据
const monthlyStats = ref({
  normal: 0,
  late: 0,
  early: 0,
  absent: 0,
  leave: 0
})

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return '未打卡'
  const date = new Date(timeString)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    1: 'success',
    2: 'warning',
    3: 'warning',
    4: 'danger',
    5: 'info'
  }
  return statusMap[status] || 'info'
}

// 格式化状态
const formatStatus = (status) => {
  const statusMap = {
    1: '正常',
    2: '迟到',
    3: '早退',
    4: '缺勤',
    5: '请假'
  }
  return statusMap[status] || '未知'
}

// 处理上班打卡
const handleCheckIn = async () => {
  try {
    const now = new Date()
    const res = await request({
      url: '/attendance/checkIn',
      method: 'post',
      data: {
        checkTime: now.toISOString(),
        remark: ''
      }
    })
    if (res.code === 200) {
      ElMessage.success('上班打卡成功')
      getTodayAttendance()
      getMonthlyAttendance()  // 刷新月度考勤数据
    } else {
      ElMessage.error(res.msg || '打卡失败')
    }
  } catch (error) {
    console.error('上班打卡失败:', error)
    ElMessage.error(error.response?.data?.message || '打卡失败，请稍后重试')
  }
}

// 处理下班打卡
const handleCheckOut = async () => {
  try {
    const now = new Date()
    const res = await request({
      url: '/attendance/checkOut',
      method: 'post',
      data: {
        checkTime: now.toISOString(),
        remark: ''
      }
    })
    if (res.code === 200) {
      ElMessage.success('下班打卡成功')
      getTodayAttendance()
      getMonthlyAttendance()  // 刷新月度考勤数据
    } else {
      ElMessage.error(res.msg || '打卡失败')
    }
  } catch (error) {
    console.error('下班打卡失败:', error)
    ElMessage.error(error.response?.data?.message || '打卡失败，请稍后重试')
  }
}

// 获取今日考勤数据
const getTodayAttendance = async () => {
  try {
    const res = await request.get('/attendance/my')
    if (res.code === 200 && res.data && res.data.length > 0) {
      const attendanceData = res.data[0]
      todayAttendance.value = {
        checkInTime: attendanceData.checkInTime,
        checkOutTime: attendanceData.checkOutTime,
        status: attendanceData.status
      }
    }
  } catch (error) {
    console.error('获取今日考勤数据失败:', error)
  }
}

// 获取今日打卡记录
const getTodayRecord = async () => {
  try {
    const res = await request({
      url: '/attendance/today',
      method: 'get'
    })
    if (res.code === 200 && res.data) {
      console.log('今日打卡记录:', res.data)
      todayRecord.value = res.data
    }
  } catch (error) {
    console.error('获取今日打卡记录失败:', error)
    ElMessage.error(error.response?.data?.message || '获取今日打卡记录失败')
  }
}

// 获取月度考勤数据
const getMonthlyAttendance = async () => {
  try {
    const res = await request.get('/attendance/monthly')
    if (res.code === 200) {
      // 确保日期格式统一
      monthlyAttendance.value = (res.data.records || []).map(record => {
        // 处理可能的不同日期格式
        let formattedDate = record.attendanceDate
        if (typeof record.attendanceDate === 'string') {
          // 如果是字符串，处理可能的不同格式
          formattedDate = record.attendanceDate.includes('T') 
            ? record.attendanceDate.split('T')[0] 
            : record.attendanceDate
        }
        
        return {
          ...record,
          date: formattedDate // 添加一个 date 字段用于日历匹配
        }
      })
      
      // 更新统计数据
      monthlyStats.value = {
        normal: res.data.normal || 0,
        late: res.data.late || 0,
        early: res.data.early || 0,
        absent: res.data.absent || 0,
        leave: res.data.leave || 0
      }

      console.log('处理后的考勤数据：', monthlyAttendance.value)
    }
  } catch (error) {
    console.error('获取月度考勤数据失败:', error)
  }
}

// 获取考勤状态样式类
const getAttendanceClass = (data) => {
  const dateStr = data.day
  const attendance = monthlyAttendance.value.find(a => a.date === dateStr)
  if (!attendance) return ''
  
  const statusClass = {
    1: 'normal',  // 正常
    2: 'late',    // 迟到
    3: 'early',   // 早退
    4: 'absent',  // 缺勤
    5: 'leave'    // 请假
  }
  return statusClass[attendance.status] || ''
}

// 获取考勤状态
const getAttendanceStatus = (data) => {
  const dateStr = data.day
  const attendance = monthlyAttendance.value.find(a => a.date === dateStr)
  if (!attendance) return null
  return attendance.status
}

// 获取考勤统计数据
const getLeaveStats = async () => {
  try {
    const res = await request.get('/leave/leavesStatistics')
    if (res.code === 200) {
      const statusCount = {}
      if (res.data.list && Array.isArray(res.data.list)) {
        res.data.list.forEach(item => {
          statusCount[item.status_name] = item.count
        })
      }
      leaveStats.value = {
        days: res.data.days || 0,
        statusCount
      }
    }
  } catch (error) {
    console.error('获取考勤统计数据失败:', error)
  }
}

// 页面跳转
const goToLeave = () => {
  router.push('/employee/leave-apply')
}

let clockTimer = null

onMounted(() => {
  // 获取数据
  getTodayAttendance()
  getMonthlyAttendance()
  getLeaveStats()
})

onUnmounted(() => {
  if (clockTimer) {
    clearInterval(clockTimer)
  }
})
</script>

<style scoped>
.app-container {
  padding: 20px;
  height: calc(100vh - 84px);
  background-color: var(--bg-color);
  overflow: hidden;
}

.box-card {
  height: 100%;
  border-radius: 16px;
  box-shadow: var(--box-shadow);
  transition: all 0.3s ease;
  
  :deep(.el-card__body) {
    height: calc(100% - 55px);
    padding: 24px;
    overflow: hidden;
  }
}

.attendance-section {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: var(--box-shadow);
  border: 1px solid var(--border-color);
}

.check-buttons {
  display: flex;
  justify-content: center;
  gap: 80px;
  margin: 30px 0;
}

.check-button-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  
  .check-button {
    width: 160px !important;
    height: 160px !important;
    font-size: 32px !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
    border: none !important;
    color: #ffffff !important;
    background: linear-gradient(145deg, #409EFF 0%, #79bbff 100%) !important;
    box-shadow: 0 10px 20px rgba(64, 158, 255, 0.2) !important;
    
    .button-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      
      .el-icon {
        font-size: 64px;
        color: #ffffff !important;
      }
      
      span {
        font-size: 18px;
        font-weight: 500;
        color: #ffffff !important;
      }
    }
    
    &:hover:not(:disabled) {
      transform: translateY(-8px) !important;
      box-shadow: 0 15px 30px rgba(64, 158, 255, 0.3) !important;
      background: linear-gradient(145deg, #409EFF 0%, #a0cfff 100%) !important;
      
      .button-content {
        .el-icon {
          transform: scale(1.1);
        }
      }
    }
    
    &:active:not(:disabled) {
      transform: scale(0.95) !important;
      box-shadow: 0 5px 15px rgba(64, 158, 255, 0.2) !important;
    }
    
    &:disabled {
      opacity: 1 !important;
      background: linear-gradient(145deg, #e0e0e0 0%, #f5f5f5 100%) !important;
      border: none !important;
      box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1) !important;
      
      .button-content {
        .el-icon, span {
          color: #909399 !important;
        }
      }
    }
  }
  
  .button-label {
    display: none;
  }
}

.attendance-info {
  display: flex;
  justify-content: space-around;
  padding: 30px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-top: 40px;
  border: 1px solid #ebeef5;
  
  .time-info {
    display: flex;
    gap: 100px;
  }
  
  .time-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    
    .label {
      font-size: 16px;
      color: #606266;
      font-weight: 500;
      background: #f5f7fa;
      padding: 6px 16px;
      border-radius: 20px;
    }
    
    .value {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      
      &.not-checked {
        color: #F56C6C;
      }
    }
  }
  
  .status-info {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding-left: 40px;
    border-left: 1px solid #ebeef5;
    
    .label {
      font-size: 16px;
      color: #606266;
      font-weight: 500;
      background: #f5f7fa;
      padding: 6px 16px;
      border-radius: 20px;
    }
    
    .status-tag {
      padding: 8px 24px;
      font-size: 16px;
      font-weight: 600;
      border-radius: 24px;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    }
  }
}

.calendar-card {
  height: 100%;
  border-radius: 16px;
  box-shadow: var(--box-shadow);
  
  :deep(.el-card__header) {
    padding: 20px;
    border-bottom: 1px solid var(--border-color);
  }
  
  .calendar-header {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    span {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-color);
    }
  }
  
  .legend {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    padding: 12px;
    background: var(--hover-bg);
    border-radius: 8px;
    
    .legend-item {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      color: var(--text-color-secondary);
      padding: 4px 8px;
      border-radius: 4px;
      transition: all 0.3s;
      
      &:hover {
        background: var(--card-bg);
      }
      
      .dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        
        &.normal { background-color: #67C23A; }
        &.late { background-color: #fcc067; }
        &.early { background-color: #a96500; }
        &.absent { background-color: #F56C6C; }
        &.leave { background-color: #409EFF; }
      }
    }
  }
}

:deep(.el-calendar) {
  --el-calendar-cell-width: 40px;
  background: transparent;
  
  .el-calendar__header {
    padding: 12px 20px;
    border-bottom: 1px solid var(--border-color);
  }
  
  .el-calendar__body {
    padding: 16px;
  }
  
  .calendar-cell {
    height: 70px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    padding: 6px;
    border-radius: 8px;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }
    
    .date-number {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 4px;
    }
    
    .status-tag {
      font-size: 12px;
      padding: 2px 8px;
      border-radius: 12px;
      width: 100%;
      text-align: center;
      font-weight: 500;
      
      &.normal {
        background-color: rgba(103, 194, 58, 0.15);
        color: #67C23A;
      }
      
      &.late {
        background-color: rgba(252, 192, 103, 0.15);
        color: #fcc067;
      }
      
      &.early {
        background-color: rgba(169, 101, 0, 0.15);
        color: #a96500;
      }
      
      &.absent {
        background-color: rgba(245, 108, 108, 0.15);
        color: #F56C6C;
      }
      
      &.leave {
        background-color: rgba(64, 158, 255, 0.15);
        color: #409EFF;
      }
    }
  }
}

.attendance-stats {
  margin-top: 20px;
  padding: 20px;
  border-top: 1px solid var(--border-color);
  background: var(--hover-bg);
  border-radius: 0 0 16px 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.stats-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px;
  border-radius: 12px;
  background: var(--card-bg);
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
  }
  
  &.normal {
    border-left: 3px solid #67C23A;
    .stats-value { color: #67C23A; }
  }
  
  &.late {
    border-left: 3px solid #fcc067;
    .stats-value { color: #fcc067; }
  }
  
  &.early {
    border-left: 3px solid #a96500;
    .stats-value { color: #a96500; }
  }
  
  &.absent {
    border-left: 3px solid #F56C6C;
    .stats-value { color: #F56C6C; }
  }
  
  &.leave {
    border-left: 3px solid #409EFF;
    .stats-value { color: #409EFF; }
  }
  
  .stats-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-color-secondary);
  }
  
  .stats-value {
    font-size: 28px;
    font-weight: bold;
  }
}
</style> 