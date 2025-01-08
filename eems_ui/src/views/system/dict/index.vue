<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>基础数据管理</span>
          <el-button type="primary" @click="handleAdd">新增数据</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="数据类型" prop="dictType">
          <el-input
            v-model="queryParams.dictType"
            placeholder="请输入数据类型"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="数据名称" prop="dictName">
          <el-input
            v-model="queryParams.dictName"
            placeholder="请输入数据名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select 
            v-model="queryParams.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 240px"
          >
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <div class="table-wrapper">
        <el-table :data="tableData" style="width: 100%" v-loading="loading" border :height="tableHeight">
          <el-table-column align="center" prop="dictType" label="数据类型" />
          <el-table-column align="center" prop="dictName" label="数据名称" />
          <el-table-column align="center" prop="orderNum" label="显示顺序" width="100" />
          <el-table-column align="center" prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="createTime" label="创建时间">
            <template #default="scope">
              {{ scope.row.createTime ? new Date(scope.row.createTime).toLocaleString() : '' }}
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="180">
            <template #default="scope">
              <el-button type="primary" link @click="handleEdit(scope.row)">修改</el-button>
              <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
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

    <!-- 添加/编辑字典类型对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form ref="dictForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="数据类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入数据类型" />
        </el-form-item>
        <el-form-item label="数据名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入数据名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" :max="999" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const form = ref({
  dictType: '',
  dictName: '',
  orderNum: 0,
  status: 1
})

const rules = {
  dictType: [
    { required: true, message: '请输入数据类型', trigger: 'blur' }
  ],
  dictName: [
    { required: true, message: '请输入数据名称', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 查询参数
const queryParams = ref({
  dictType: '',
  dictName: '',
  status: undefined
})

// 获取字典类型列表
const getList = async () => {
  try {
    loading.value = true
    const res = await request.get('/dict/list', {
      params: queryParams.value
    })
    console.log('数据类型列表响应数据:', res)
    if (res.code === 200 && Array.isArray(res.data)) {
      // 处理分页
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      tableData.value = res.data.slice(start, end)
      total.value = res.data.length
      console.log('处理后的表格数据:', tableData.value)
    } else {
      ElMessage.error('获取字典类型列表失败: 响应格式错误')
    }
  } catch (error) {
    console.error('获取数据类型列表失败:', error)
    ElMessage.error('获取数据类型列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 新增字典类型
const handleAdd = () => {
  dialogTitle.value = '新增数据类型'
  form.value = {
    dictType: '',
    dictName: '',
    orderNum: 0,
    status: 1
  }
  dialogVisible.value = true
}

// 修改数据类型
const handleEdit = (row) => {
  dialogTitle.value = '修改数据类型'
  form.value = {
    id: row.id,
    dictType: row.dictType,
    dictName: row.dictName,
    orderNum: row.orderNum,
    status: row.status
  }
  dialogVisible.value = true
}

// 删除数据类型
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认要删除该数据类型吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await request.delete(`/dict/${row.id}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除数据失败:', error)
      ElMessage.error('删除数据失败: ' + error.message)
    }
  })
}

// 提交表单
const submitForm = async () => {
  try {
    const url = form.value.id ? '/dict/update' : '/dict/add'
    const method = form.value.id ? 'put' : 'post'
    
    console.log('提交的数据:', form.value)
    
    await request[method](url, form.value)
    ElMessage.success(form.value.id ? '修改成功' : '新增成功')
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交数据失败:', error)
    ElMessage.error('提交数据失败: ' + error.message)
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

// 搜索按钮操作
const handleQuery = () => {
  currentPage.value = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.value = {
    dictType: '',
    dictName: '',
    status: undefined
  }
  currentPage.value = 1
  getList()
}

// 添加表格高度计算
const tableHeight = ref(500)

const calculateTableHeight = () => {
  tableHeight.value = window.innerHeight - 340
}

onMounted(() => {
  getList()
  calculateTableHeight()
  window.addEventListener('resize', calculateTableHeight)
})

onUnmounted(() => {
  window.removeEventListener('resize', calculateTableHeight)
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
    padding: 20px;
    height: calc(100% - 60px);
    position: relative;
  }
}

.search-form {
  margin-bottom: 20px;
}

.table-wrapper {
  height: calc(100% - 120px);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-table {
  border-top: 1px solid #ebeef5;
}

/* 自定义表格滚动条样式 */
:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: #ddd;
  border-radius: 4px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: #f5f5f5;
}

.pagination-container {
  position: absolute;
  bottom: 20px;
  right: 20px;
  background-color: #fff;
  padding: 10px 0;
  z-index: 1;
}

.dialog-footer {
  text-align: right;
  padding-top: 10px;
}
</style>