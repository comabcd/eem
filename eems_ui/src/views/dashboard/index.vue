<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>员工总数</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.employeeCount }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>今日出勤</span>
              <el-icon><Calendar /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.todayAttendance }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>请假人数</span>
              <el-icon><Timer /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.leaveCount }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>部门数量</span>
              <el-icon><OfficeBuilding /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.departmentCount }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Calendar, Timer, OfficeBuilding } from '@element-plus/icons-vue'
import request from '@/utils/request'

const statistics = ref({
  employeeCount: 0,
  todayAttendance: 0,
  leaveCount: 0,
  departmentCount: 0
})

const getStatistics = async () => {
  try {
    const res = await request.get('/dashboard/statistics')
    statistics.value = res.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  getStatistics()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.data-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    color: #303133;
  }
  
  .card-value {
    font-size: 28px;
    font-weight: bold;
    color: #409EFF;
    text-align: center;
    margin-top: 10px;
  }
}
</style> 