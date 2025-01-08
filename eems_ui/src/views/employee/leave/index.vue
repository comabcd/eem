<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>请假申请</span>
          <el-button type="primary" @click="handleAdd">申请请假</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="请假类型" prop="leaveType">
          <el-select
            v-model="queryParams.leaveType"
            placeholder="请选择请假类型"
            clearable
            style="width: 240px"
          >
            <el-option label="事假" value="1" />
            <el-option label="病假" value="2" />
            <el-option label="年假" value="3" />
            <el-option label="调休" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 240px"
          >
            <el-option label="待审批" value="0" />
            <el-option label="已通过" value="1" />
            <el-option label="已驳回" value="2" />
            <el-option label="待销假" value="3" />
            <el-option label="已完成" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading" border>
        <el-table-column type="index" label="序号" min-width="60" align="center" />
        <el-table-column align="center" prop="leaveType" label="请假类型" min-width="100">
          <template #default="scope">
            {{ getLeaveTypeName(scope.row.leaveType) }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="开始时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="结束时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column align="center" prop="duration" label="时长(天)" min-width="80" />
        <el-table-column align="center" label="创建时间" min-width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column align="center" prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template #default="scope">
            <el-button 
              type="danger" 
              link 
              @click="handleCancel(scope.row)"
              v-if="scope.row.status === 1"
            >撤销</el-button>
            <el-button 
              type="success" 
              link 
              @click="handleComplete(scope.row)"
              v-if="scope.row.status === 3"
            >销假</el-button>
            <el-button 
              type="primary" 
              link 
              @click="handleDetail(scope.row)"
            >详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 请假申请对话框 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      append-to-body
    >
      <el-form
        ref="leaveFormRef"
        :model="leaveForm"
        :rules="leaveRules"
        label-width="100px"
      >
        <el-form-item label="请假类型" prop="leaveType">
          <el-select v-model="leaveForm.leaveType" placeholder="请选择请假类型" style="width: 100%">
            <el-option label="事假" value="1" />
            <el-option label="病假" value="2" />
            <el-option label="年假" value="3" />
            <el-option label="调休" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="leaveForm.startTime"
            type="datetime"
            placeholder="请选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="leaveForm.endTime"
            type="datetime"
            placeholder="请选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="请假原因" prop="reason">
          <el-input
            v-model="leaveForm.reason"
            type="textarea"
            placeholder="请输入请假原因"
            :rows="3"
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

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialog.visible"
      title="请假详情"
      width="600px"
      append-to-body
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="请假类型">{{ getLeaveTypeName(detailForm.leaveType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailForm.status)">
            {{ getStatusName(detailForm.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDateTime(detailForm.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDateTime(detailForm.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="时长(天)">{{ detailForm.duration }}</el-descriptions-item>
        <el-descriptions-item label="请假原因" :span="2">{{ detailForm.reason }}</el-descriptions-item>
        <el-descriptions-item label="审批人" v-if="detailForm.approver">{{ detailForm.approver }}</el-descriptions-item>
        <el-descriptions-item label="审批时间" v-if="detailForm.approveTime">{{ formatDateTime(detailForm.approveTime) }}</el-descriptions-item>
        <el-descriptions-item label="驳回原因" :span="2" v-if="detailForm.status === 2 && detailForm.approveRemark">{{ detailForm.approveRemark }}</el-descriptions-item>
        <el-descriptions-item label="销假时间" v-if="detailForm.status === 4 && detailForm.returnTime">
          {{ formatDateTime(detailForm.returnTime) }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialog.visible = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ')
}

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const leaveFormRef = ref()

const queryParams = ref({
  leaveType: '',
  status: ''
})

const dialog = reactive({
  visible: false,
  title: '申请请假'
})

const leaveForm = reactive({
  leaveType: '',
  startTime: '',
  endTime: '',
  reason: ''
})

const leaveRules = {
  leaveType: [
    { required: true, message: '请选择请假类型', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入请假原因', trigger: 'blur' }
  ]
}

// 获取请假类型名称
const getLeaveTypeName = (type) => {
  const typeMap = {
    '1': '事假',
    '2': '病假',
    '3': '年假',
    '4': '调休'
  }
  return typeMap[type] || '未知'
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    1: '待审批',
    2: '已驳回',
    3: '待销假',
    4: '已完成'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    1: 'warning',
    2: 'danger',
    3: 'warning',
    4: 'success'
  }
  return typeMap[status] || 'info'
}

// 获取请假列表
const getList = async () => {
  console.log('queryParams.value', queryParams.value)
  try {
    loading.value = true
    const res = await request.get('/leave/my', {
      params: {
        ...queryParams.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    if (res.code === 200) {
      console.log('获取请假列表成功：', res.data)
      tableData.value = res.data
      total.value = res.data.length
    } else {
      ElMessage.error(res.message || '获取请假列表失败')
    }
  } catch (error) {
    console.error('获取请假列表失败:', error)
    ElMessage.error('获取请假列表失败: ' + error.message)
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
    leaveType: '',
    status: ''
  }
  handleQuery()
}

// 新增请假
const handleAdd = () => {
  dialog.visible = true
  dialog.title = '申请请假'
  Object.assign(leaveForm, {
    leaveType: '',
    startTime: '',
    endTime: '',
    reason: ''
  })
}

// 提交请假申请
const handleSubmit = () => {
  leaveFormRef.value.validate(async valid => {
    if (valid) {
      try {
        await request.post('/leave/apply', leaveForm)
        ElMessage.success('提交成功')
        dialog.visible = false
        getList()
      } catch (error) {
        console.error('提交请假申请失败:', error)
      }
    }
  })
}

// 撤销请假申请
const handleCancel = (row) => {
  ElMessageBox.confirm(
    '确认要撤销该请假申请吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await request.put(`/leave/cancel/${row.id}`)
      ElMessage.success('撤销成功')
      getList()
    } catch (error) {
      console.error('撤销请假申请失败:', error)
    }
  })
}

// 查看详情
const handleDetail = (row) => {
  detailDialog.visible = true
  Object.assign(detailForm, row)
}

// 销假操作
const handleComplete = (row) => {
  ElMessageBox.confirm(
    '确认要销假吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await request.put(`/leave/complete/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('销假成功')
        getList()
      } else {
        ElMessage.error(res.msg || '销假失败')
      }
    } catch (error) {
      console.error('销假失败:', error)
      ElMessage.error('销假失败: ' + error.message)
    }
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

// 详情对话框
const detailDialog = reactive({
  visible: false
})

// 详情表单
const detailForm = reactive({
  leaveType: '',
  startTime: '',
  endTime: '',
  duration: '',
  reason: '',
  status: '',
  approver: '',
  approveTime: '',
  approveRemark: '',
  completeTime: ''
})

onMounted(() => {
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

.el-table {
  height: calc(100% - 60px) !important;
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