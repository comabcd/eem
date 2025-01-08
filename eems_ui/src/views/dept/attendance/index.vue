<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>考勤管理</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="工号" prop="employeeNo">
          <el-input
            v-model="queryParams.employeeNo"
            placeholder="请输入工号"
            clearable
            @keyup.enter="handleQuery"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="员工姓名" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入员工姓名"
            clearable
            @keyup.enter="handleQuery"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="岗位" prop="postId">
          <el-select
            v-model="queryParams.postId"
            placeholder="请选择岗位"
            clearable
            style="width: 240px"
          >
            <el-option
              v-for="item in postOptions"
              :key="item.id"
              :label="item.postName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <div class="w-100"></div>
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            clearable
            style="width: 360px"
            @change="handleDateRangeChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="attendanceList"
        border
        style="width: 100%; height: calc(100% - 180px)"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="日期" min-width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.attendanceDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="employeeNo" label="工号" min-width="120" align="center" />
        <el-table-column prop="name" label="姓名" min-width="120" align="center" />
        <el-table-column prop="post" label="岗位" min-width="120" align="center" />
        <el-table-column label="上班打卡" min-width="180" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.checkInTime) }}
          </template>
        </el-table-column>
        <el-table-column label="下班打卡" min-width="180" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.checkOutTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 打卡对话框 -->
    <el-dialog
      title="打卡"
      v-model="dialog.visible"
      width="400px"
      append-to-body
    >
      <el-form
        ref="clockFormRef"
        :model="clockForm"
        :rules="clockRules"
        label-width="80px"
      >
        <el-form-item label="打卡类型" prop="type">
          <el-radio-group v-model="clockForm.type">
            <el-radio :label="1">上班打卡</el-radio>
            <el-radio :label="2">下班打卡</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="clockForm.remark"
            type="textarea"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  employeeNo: '',
  name: '',
  postId: undefined,
  startDate: '',
  endDate: ''
})

const dateRange = ref([])

const loading = ref(false)
const total = ref(0)
const attendanceList = ref([])
const postOptions = ref([])

// 对话框
const dialog = reactive({
  visible: false
})

// 打卡表单
const clockFormRef = ref()
const clockForm = reactive({
  type: 1,
  remark: ''
})

// 表单校验规则
const clockRules = {
  type: [
    { required: true, message: '请选择打卡类型', trigger: 'change' }
  ]
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '--'
  return date.replace(/(\d{4})-(\d{2})-(\d{2})/, '$1-$2-$3')
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '--'
  const dt = new Date(dateTime)
  const year = dt.getFullYear()
  const month = String(dt.getMonth() + 1).padStart(2, '0')
  const day = String(dt.getDate()).padStart(2, '0')
  const hours = String(dt.getHours()).padStart(2, '0')
  const minutes = String(dt.getMinutes()).padStart(2, '0')
  const seconds = String(dt.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 获取考勤列表
const getList = async () => {
  try {
    loading.value = true
    const res = await request.get('/attendance/search', {
      params: {
        ...queryParams
      }
    })
    if (res.code === 200) {
      console.log("获取考勤列表成功:",res.data)
      const pageResult = res.data
      attendanceList.value = pageResult.list || []
      total.value = pageResult.total || 0
      queryParams.pageNum = pageResult.pageNum
      queryParams.pageSize = pageResult.pageSize
    } else {
      ElMessage.error(res.msg || '获取考勤列表失败')
    }
  } catch (error) {
    console.error('获取考勤列表失败:', error)
    ElMessage.error('获取考勤列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.employeeNo = ''
  queryParams.name = ''
  queryParams.postId = undefined
  queryParams.startDate = ''
  queryParams.endDate = ''
  dateRange.value = []
  handleQuery()
}

// 打卡
const handleAdd = () => {
  dialog.visible = true
  clockForm.type = 1
  clockForm.remark = ''
}

// 提交打卡
const handleSubmit = () => {
  clockFormRef.value.validate(async valid => {
    if (valid) {
      try {
        await request.post('/attendance/clock', clockForm)
        ElMessage.success('打卡成功')
        dialog.visible = false
        getList()
      } catch (error) {
        console.error('打卡失败:', error)
      }
    }
  })
}

// 导出
const handleExport = async () => {
  try {
    const res = await request.get('/attendance/export', {
      params: queryParams,
      responseType: 'blob'
    })
    const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = '考勤记录.xlsx'
    link.click()
  } catch (error) {
    console.error('导出失败:', error)
  }
}

// 状态显示
const getStatusType = (status) => {
  const statusMap = {
    1: 'success',  // 正常
    2: 'warning',  // 迟到
    3: 'danger',   // 早退
    4: 'info'      // 缺勤
  }
  return statusMap[status] || ''
}

const getStatusText = (status) => {
  const statusMap = {
    1: '正常',
    2: '迟到',
    3: '早退',
    4: '缺勤'
  }
  return statusMap[status] || ''
}

// 分页
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 获取岗位列表
const getPostList = async () => {
  try {
    const res = await request.get('/post/list')
    if (res.code === 200) {
      postOptions.value = res.data
    } else {
      ElMessage.error(res.msg || '获取岗位列表失败')
    }
  } catch (error) {
    console.error('获取岗位列表失败:', error)
    ElMessage.error('获取岗位列表失败: ' + error.message)
  }
}

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    queryParams.startDate = val[0]
    queryParams.endDate = val[1]
  } else {
    queryParams.startDate = ''
    queryParams.endDate = ''
  }
}

onMounted(() => {
  getList()
  getPostList()
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

.search-form {
  margin: 0;
  padding: 20px;
  background-color: #fff;
  border-radius: 0;
  border-bottom: 1px solid #ebeef5;
}

.el-table {
  height: calc(100% - 120px) !important;
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