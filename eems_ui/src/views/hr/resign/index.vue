<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>离职办理</span>
          <el-button type="primary" @click="handleAdd">新增离职</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="员工姓名" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入员工姓名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-select
            v-model="queryParams.deptId"
            placeholder="请选择部门"
            clearable
          >
            <el-option
              v-for="item in deptOptions"
              :key="item.id"
              :label="item.deptName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="待办理" value="0" />
            <el-option label="已办理" value="1" />
            <el-option label="已取消" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="deptName" label="所属部门" />
        <el-table-column prop="postName" label="岗位" />
        <el-table-column prop="resignType" label="离职类型">
          <template #default="scope">
            {{ getResignTypeName(scope.row.resignType) }}
          </template>
        </el-table-column>
        <el-table-column prop="resignDate" label="预计离职日期" />
        <el-table-column prop="reason" label="离职原因" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button 
              type="success" 
              link 
              @click="handleConfirm(scope.row)"
              v-if="scope.row.status === '0'"
            >确认离职</el-button>
            <el-button 
              type="danger" 
              link 
              @click="handleCancel(scope.row)"
              v-if="scope.row.status === '0'"
            >取消离职</el-button>
            <el-button 
              type="primary" 
              link 
              @click="handleEdit(scope.row)"
              v-if="scope.row.status === '0'"
            >编辑</el-button>
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const deptOptions = ref([])

const queryParams = ref({
  name: '',
  deptId: undefined,
  status: undefined
})

// 获取部门列表
const getDeptList = async () => {
  try {
    const res = await request.get('/department/tree')
    deptOptions.value = res.data
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

// 获取离职类型名称
const getResignTypeName = (type) => {
  const typeMap = {
    '1': '主动离职',
    '2': '被动离职',
    '3': '退休',
    '4': '其他'
  }
  return typeMap[type] || '未知'
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    '0': '待办理',
    '1': '已办理',
    '2': '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    '0': 'warning',
    '1': 'success',
    '2': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取离职列表
const getList = async () => {
  try {
    loading.value = true
    const res = await request.get('/hr/resign/list', {
      params: {
        ...queryParams.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取离职列表失败:', error)
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
    name: '',
    deptId: undefined,
    status: undefined
  }
  handleQuery()
}

// 新增离职
const handleAdd = () => {
  // TODO: 实现新增离职功能
}

// 编辑
const handleEdit = (row) => {
  // TODO: 实现编辑功能
}

// 确认离职
const handleConfirm = (row) => {
  ElMessageBox.confirm(
    '确认该员工已办理完离职手续？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await request.put(`/hr/resign/confirm/${row.id}`)
      ElMessage.success('操作成功')
      getList()
    } catch (error) {
      console.error('确认离职失败:', error)
    }
  })
}

// 取消离职
const handleCancel = (row) => {
  ElMessageBox.prompt('请输入取消原因', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S/,
    inputErrorMessage: '取消原因不能为空'
  }).then(async ({ value }) => {
    try {
      await request.put(`/hr/resign/cancel/${row.id}`, {
        cancelReason: value
      })
      ElMessage.success('操作成功')
      getList()
    } catch (error) {
      console.error('取消离职失败:', error)
    }
  })
}

// 查看详情
const handleDetail = (row) => {
  // TODO: 实现查看详情功能
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
  getDeptList()
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

.search-form {
  margin-bottom: 20px;
  
  :deep(.el-form-item) {
    margin-bottom: 0;
  }
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