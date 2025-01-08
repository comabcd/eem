<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>考勤打卡</span>
          <div class="header-right">
            <el-button 
              type="primary" 
              @click="handleClockIn"
              :disabled="todayRecord.checkInTime"
            >上班打卡</el-button>
            <el-button 
              type="primary" 
              @click="handleClockOut"
              :disabled="!todayRecord.checkInTime || todayRecord.checkOutTime"
            >下班打卡</el-button>
          </div>
        </div>
      </template>

      <!-- 今日打卡信息 -->
      <div class="today-info">
        <div class="info-item">
          <div class="label">今日日期</div>
          <div class="value">{{ formatDate(new Date()) }}</div>
        </div>
        <div class="info-item">
          <div class="label">上班时间</div>
          <div class="value">{{ formatTime(todayRecord.checkInTime) }}</div>
        </div>
        <div class="info-item">
          <div class="label">下班时间</div>
          <div class="value">{{ formatTime(todayRecord.checkOutTime) }}</div>
        </div>
        <div class="info-item">
          <div class="label">工作时长</div>
          <div class="value">{{ calculateWorkHours(todayRecord) }}</div>
        </div>
        <div class="info-item status-item">
          <div class="label">考勤状态</div>
          <div class="value">
            <el-tag 
              :type="getStatusType(todayRecord.status)"
              class="status-tag"
            >
              {{ getStatusName(todayRecord.status) }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="queryParams.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="考勤状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px;"
          >
            <el-option label="正常" value="1" />
            <el-option label="迟到" value="2" />
            <el-option label="早退" value="3" />
            <el-option label="缺勤" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading" border>
        <el-table-column align="center" label="考勤日期" prop="date" min-width="120" />
        <el-table-column align="center" label="上班时间" min-width="120">
          <template #default="scope">
            {{ formatTime(scope.row.checkInTime) }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="下班时间" min-width="120">
          <template #default="scope">
            {{ formatTime(scope.row.checkOutTime) }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="工作时长" min-width="120">
          <template #default="scope">
            {{ calculateWorkHours({
              checkInTime: scope.row.checkInTime,
              checkOutTime: scope.row.checkOutTime,
              attendanceDate: scope.row.date
            }) }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="考勤状态" min-width="120">
          <template #default="scope">
            <el-tag 
              :type="getStatusType(scope.row.status)"
              class="status-tag"
            >
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const todayRecord = ref({})

const queryParams = ref({
  dateRange: [],
  status: ''
})

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    '1': '正常',
    '2': '迟到',
    '3': '早退',
    '4': '缺勤'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    '1': 'success',
    '2': 'warning',
    '3': 'warning',
    '4': 'danger'
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '未打卡'
  try {
    // 如果是完整的日期时间字符串（包含日期部分）
    if (timeStr.includes('T') || timeStr.includes('-')) {
      const date = new Date(timeStr)
      if (isNaN(date.getTime())) {
        return '--'
      }
      return date.toLocaleTimeString('zh-CN', { hour12: false })
    }
    // 如果只是时间字符串，直接返回
    return timeStr
  } catch (error) {
    console.error('时间格式化错误:', error)
    return '--'
  }
}

// 计算工作时长
const calculateWorkHours = (record) => {
  if (!record || !record.checkInTime || !record.checkOutTime) {
    return '--'
  }
  try {
    let checkInTime, checkOutTime

    // 处理打卡时间
    if (record.checkInTime.includes('T')) {
      checkInTime = new Date(record.checkInTime)
    } else {
      checkInTime = new Date(`${record.attendanceDate || record.date} ${record.checkInTime}`)
    }

    if (record.checkOutTime.includes('T')) {
      checkOutTime = new Date(record.checkOutTime)
    } else {
      checkOutTime = new Date(`${record.attendanceDate || record.date} ${record.checkOutTime}`)
    }

    // 验证时间是否有效
    if (isNaN(checkInTime.getTime()) || isNaN(checkOutTime.getTime())) {
      console.error('无效的时间格式')
      return '--'
    }

    const diffMs = checkOutTime.getTime() - checkInTime.getTime()
    const hours = diffMs / (1000 * 60 * 60)
    
    // 如果工作时长超过24小时或小于0，可能是跨天打卡
    if (hours < 0) {
      // 如果是负数，说明可能跨天了，加上24小时
      return ((hours + 24).toFixed(1) + '小时')
    } else if (hours > 24) {
      // 如果超过24小时，取模
      return ((hours % 24).toFixed(1) + '小时')
    }
    
    return hours.toFixed(1) + '小时'
  } catch (error) {
    console.error('工作时长计算错误:', error)
    return '--'
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

// 获取考勤列表
const getList = async () => {
  try {
    loading.value = true
    const [startDate, endDate] = queryParams.value.dateRange || []
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      status: queryParams.value.status,
      startDate,
      endDate
    }

    const res = await request({
      url: '/attendance/my',
      method: 'get',
      params
    })
    
    if (res.code === 200) {
      console.log('获取考勤列表成功:', res.data)
      // 直接处理数组数据
      if (Array.isArray(res.data)) {
        tableData.value = res.data
        total.value = res.data.length
      } else if (res.data && Array.isArray(res.data.records)) {
        // 兼容分页对象格式
        tableData.value = res.data.records
        total.value = res.data.total || 0
      } else {
        tableData.value = []
        total.value = 0
      }
    }
  } catch (error) {
    console.error('获取考勤列表失败:', error)
    ElMessage.error(error.response?.data?.message || '获取考勤列表失败')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  currentPage.value = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.value = {
    dateRange: [],
    status: ''
  }
  handleQuery()
}

// 处理上班打卡
const handleClockIn = async () => {
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
      getTodayRecord()
      getList()
    }
  } catch (error) {
    console.error('上班打卡失败:', error)
    ElMessage.error(error.response?.data?.message || '上班打卡失败')
  }
}

// 处理下班打卡
const handleClockOut = async () => {
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
      getTodayRecord()
      getList()
    }
  } catch (error) {
    console.error('下班打卡失败:', error)
    ElMessage.error(error.response?.data?.message || '下班打卡失败')
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

onMounted(() => {
  getTodayRecord()
  getList()
})
</script>

<style scoped>
.app-container {
  height: calc(100vh - 60px);
  background-color: #fff;
  position: relative;
  margin: -20px;
}

.box-card {
  height: 100%;
  border: none;
  border-radius: 0;
  background: none;
  
  :deep(.el-card__header) {
    padding: 15px 20px;
    border-bottom: 1px solid #ebeef5;
  }
  
  :deep(.el-card__body) {
    height: calc(100% - 55px);
    padding: 20px;
    position: relative;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  gap: 10px;
}

.today-info {
  display: flex;
  justify-content: space-between;
  padding: 20px;
  margin-bottom: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.info-item {
  text-align: center;
  
  .label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }
  
  .value {
    font-size: 16px;
    color: #303133;
    font-weight: bold;
  }
}

.search-form {
  margin-bottom: 20px;
  
  :deep(.el-form-item) {
    margin-bottom: 0;
  }
  
  :deep(.el-select) {
    width: 150px;
  }
  
  :deep(.el-date-editor) {
    width: 360px;
  }
}

.el-table {
  height: calc(100% - 260px) !important;
  border-top: 1px solid #ebeef5;
}

.pagination-container {
  position: absolute;
  bottom: 20px;
  right: 20px;
  background-color: #fff;
}

.dialog-footer {
  text-align: right;
  padding-top: 10px;
}
</style> 