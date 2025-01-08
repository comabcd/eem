<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>请假管理</span>
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
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="员工姓名" prop="employeeName">
          <el-input
            v-model="queryParams.employeeName"
            placeholder="请输入员工姓名"
            clearable
            @keyup.enter="handleQuery"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="请假类型" prop="leaveType">
          <el-select
            v-model="queryParams.leaveType"
            placeholder="请选择请假类型"
            clearable
            style="width: 200px"
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
            style="width: 200px"
          >
            <el-option label="待审批" value="1" />
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
        <el-table-column align="center" prop="employeeNo" label="工号" min-width="100" />
        <el-table-column align="center" prop="name" label="员工姓名" min-width="100" />
        <el-table-column align="center" prop="post" label="所属岗位" min-width="120" />
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
        <el-table-column align="center" prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" min-width="120" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              link 
              @click="handleApproval(scope.row)"
              v-if="scope.row.status === 1"
            >审批</el-button>
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
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 审批对话框 -->
      <el-dialog
        v-model="approvalDialog.visible"
        title="请假审批"
        width="600px"
        append-to-body
      >
        <div class="leave-detail">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="工号">{{ approvalForm.employeeNo }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ approvalForm.name }}</el-descriptions-item>
            <el-descriptions-item label="岗位">{{ approvalForm.post }}</el-descriptions-item>
            <el-descriptions-item label="请假类型">{{ getLeaveTypeName(approvalForm.leaveType) }}</el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ formatDateTime(approvalForm.startTime) }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ formatDateTime(approvalForm.endTime) }}</el-descriptions-item>
            <el-descriptions-item label="时长(天)">{{ approvalForm.duration }}</el-descriptions-item>
            <el-descriptions-item label="请假原因" :span="2">{{ approvalForm.reason }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="approval-form" v-if="approvalForm.status === 1">
          <el-form :model="approvalForm" ref="approvalFormRef" label-width="100px">
            <el-form-item label="审批结果" prop="approvalResult">
              <el-radio-group v-model="approvalForm.approvalResult">
                <el-radio :label="2">同意</el-radio>
                <el-radio :label="3">驳回</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="审批意见" prop="approveRemark" v-if="approvalForm.approvalResult === 3">
              <el-input
                v-model="approvalForm.approveRemark"
                type="textarea"
                placeholder="请输入驳回原因"
                :rows="3"
              />
            </el-form-item>
          </el-form>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="approvalDialog.visible = false">取 消</el-button>
            <el-button type="primary" @click="submitApproval" v-if="approvalForm.status === 1">确 定</el-button>
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
          <el-descriptions-item label="工号">{{ detailForm.employeeNo }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ detailForm.name }}</el-descriptions-item>
          <el-descriptions-item label="岗位">{{ detailForm.post }}</el-descriptions-item>
          <el-descriptions-item label="请假类型">{{ getLeaveTypeName(detailForm.leaveType) }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(detailForm.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDateTime(detailForm.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="时长(天)">{{ detailForm.duration }}</el-descriptions-item>
          <el-descriptions-item label="请假原因" :span="2">{{ detailForm.reason }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(detailForm.status)">
              {{ getStatusName(detailForm.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审批人" v-if="detailForm.approver">{{ detailForm.approver }}</el-descriptions-item>
          <el-descriptions-item label="审批时间" v-if="detailForm.approveTime">{{ formatDateTime(detailForm.approveTime) }}</el-descriptions-item>
          <el-descriptions-item label="审批意见" :span="2" v-if="detailForm.approveRemark">{{ detailForm.approveRemark }}</el-descriptions-item>
        </el-descriptions>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="detailDialog.visible = false">关 闭</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const queryParams = ref({
  employeeNo: '',
  employeeName: '',
  leaveType: '',
  status: ''
})

// 审批对话框
const approvalDialog = reactive({
  visible: false
})

// 详情对话框
const detailDialog = reactive({
  visible: false
})

// 审批表单
const approvalFormRef = ref()
const approvalForm = reactive({
  id: undefined,
  employeeNo: '',
  name: '',
  post: '',
  leaveType: '',
  startTime: '',
  endTime: '',
  duration: '',
  reason: '',
  status: '',
  approvalResult: 2,  // 默认同意
  approveRemark: ''
})

// 详情表单
const detailForm = reactive({
  id: undefined,
  employeeNo: '',
  name: '',
  post: '',
  leaveType: '',
  startTime: '',
  endTime: '',
  duration: '',
  reason: '',
  status: '',
  approver: '',
  approveTime: '',
  approveRemark: ''
})

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

// 打开审批对话框
const handleApproval = (row) => {
  approvalDialog.visible = true
  Object.assign(approvalForm, row)
  approvalForm.approvalResult = 2
  approvalForm.approveRemark = ''
}

// 打开详情对话框
const handleDetail = (row) => {
  detailDialog.visible = true
  Object.assign(detailForm, row)
}

// 提交审批
const submitApproval = async () => {
  if (approvalForm.approvalResult === 3 && !approvalForm.approveRemark) {
    ElMessage.warning('请输入驳回原因')
    return
  }

  try {
    const url = approvalForm.approvalResult === 2 ? '/leave/approve' : '/leave/reject'
    const res = await request.put(`${url}/${approvalForm.id}`, {
      approveRemark: approvalForm.approveRemark
    })
    
    if (res.code === 200) {
      ElMessage.success('审批成功')
      approvalDialog.visible = false
      getList()
    } else {
      ElMessage.error(res.msg || '审批失败')
    }
  } catch (error) {
    console.error('审批失败:', error)
    ElMessage.error('审批失败: ' + error.message)
  }
}

// 获取请假列表
const getList = async () => {
  try {
    loading.value = true
    console.log('开始获取请假列表，参数:', {
      ...queryParams.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    const res = await request.get('/leave/search', {
      params: {
        ...queryParams.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    console.log('请假列表响应:', res)
    if (res.code === 200) {
      // 适配 PageResult 数据结构
      const pageResult = res.data
      tableData.value = pageResult.list || []
      total.value = pageResult.total || 0
      currentPage.value = pageResult.pageNum
      pageSize.value = pageResult.pageSize
      console.log('请假列表数据:', tableData.value)
    } else {
      console.error('获取请假列表失败:', res)
      ElMessage.error(res.msg || '获取请假列表失败')
    }
  } catch (error) {
    console.error('获取请假列表失败:', error)
    ElMessage.error('获取请假列表失败: ' + (error.message || '未知错误'))
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
    employeeNo: '',
    employeeName: '',
    leaveType: '',
    status: ''
  }
  handleQuery()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ')
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  height: calc(100vh - 60px);
  background-color: #fff;
  position: relative;
  margin: -20px;  /* 抵消父容器的padding */
}

.box-card {
  height: 100%;
  border: none;
  border-radius: 0;  /* 移除圆角 */
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