<template>
  <div class="dashboard-container">
    <!-- 统计卡片行 -->
    <el-row :gutter="24">
      <el-col :span="6" v-for="(item, index) in statistics" :key="index">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon class="stat-icon" :class="item.type">
              <component :is="item.icon" />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表行 -->
    <el-row :gutter="24" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">本月考勤统计</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="attendanceChartRef" style="height: 350px"></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">本月请假统计</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="leaveChartRef" style="height: 350px"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  User,
  Calendar,
  Timer,
  Warning
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const attendanceChartRef = ref(null)
const leaveChartRef = ref(null)
let attendanceChart = null
let leaveChart = null

// 统计数据
const statistics = ref([
  { label: '部门人数', value: 0, icon: 'User', type: 'primary' },
  { label: '今日出勤', value: 0, icon: 'Calendar', type: 'success' },
  { label: '请假人数', value: 0, icon: 'Timer', type: 'warning' },
  { label: '异常考勤', value: 0, icon: 'Warning', type: 'danger' }
])

// 获取统计数据
const getStatistics = async () => {
  try {
    const res = await request.get('/department/statistics')
    if (res.code === 200) {
      console.log('获取统计数据', res.data)
      statistics.value[0].value = res.data.totalEmployees
      statistics.value[1].value = res.data.todayAttendance
      statistics.value[2].value = res.data.onLeave
      statistics.value[3].value = res.data.abnormalAttendance
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 初始化考勤统计图表
const initAttendanceChart = async () => {
  try {
    const res = await request.get('/attendance/stats')
    if (res.code === 200) {
      console.log('获取考勤统计数据', res.data)
      const { normal, late, early, absent } = res.data
      attendanceChart = echarts.init(attendanceChartRef.value)
      attendanceChart.setOption({
        title: {
          text: '考勤状态分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            type: 'pie',
            radius: '50%',
            data: [
              { value: normal, name: '正常', itemStyle: { color: '#67C23A' } },
              { value: late, name: '迟到', itemStyle: { color: '#E6A23C' } },
              { value: early, name: '早退', itemStyle: { color: '#F56C6C' } },
              { value: absent, name: '缺勤', itemStyle: { color: '#909399' } }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error('获取考勤统计数据失败:', error)
  }
}

// 初始化请假统计图表
const initLeaveChart = async () => {
  try {
    const res = await request.get('/leave/stats')
    if (res.code === 200) {
      const { dates, counts } = res.data
      leaveChart = echarts.init(leaveChartRef.value)
      leaveChart.setOption({
        title: {
          text: '请假趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: counts,
            type: 'line',
            smooth: true,
            lineStyle: {
              color: '#409EFF'
            },
            itemStyle: {
              color: '#409EFF'
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error('获取请假统计数据失败:', error)
  }
}

// 监听窗口大小变化
const handleResize = () => {
  attendanceChart?.resize()
  leaveChart?.resize()
}

onMounted(() => {
  getStatistics()
  initAttendanceChart()
  initLeaveChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  attendanceChart?.dispose()
  leaveChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

/* 统计卡片样式 */
.stat-card {
  margin-bottom: 24px;
  border-radius: 8px;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  font-size: 52px;
  margin-right: 24px;
  padding: 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-icon.primary {
  color: #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
}

.stat-icon.success {
  color: #67C23A;
  background-color: rgba(103, 194, 58, 0.1);
}

.stat-icon.warning {
  color: #E6A23C;
  background-color: rgba(230, 162, 60, 0.1);
}

.stat-icon.danger {
  color: #F56C6C;
  background-color: rgba(245, 108, 108, 0.1);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 图表区域样式 */
.chart-row {
  margin-top: 12px;
}

.chart-card {
  height: 100%;
  border-radius: 8px;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  padding: 20px;
}

/* 响应式调整 */
@media screen and (max-width: 1400px) {
  .stat-icon {
    font-size: 44px;
    padding: 12px;
  }
  
  .stat-value {
    font-size: 24px;
  }
}

@media screen and (max-width: 1200px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .stat-content {
    padding: 8px;
  }
  
  .stat-icon {
    margin-right: 16px;
  }
}
</style> 