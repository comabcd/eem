<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <el-button type="primary" @click="handleAdd">新增部门</el-button>
        </div>
      </template>
      
      <div class="table-wrapper">
        <el-table
          :data="tableData"
          style="width: 100%"
          v-loading="loading"
          row-key="id"
          default-expand-all
          :height="tableHeight"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        >
          <el-table-column prop="label" label="部门名称" />
          <el-table-column align="center" prop="orderNum" label="排序" width="100" />
          <el-table-column align="center" prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="创建时间" width="180">
            <template #default="scope">
              {{ parseTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" link @click="handleAdd(scope.row)">新增</el-button>
              <el-button type="primary" link @click="handleEdit(scope.row)">修改</el-button>
              <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 添加/编辑部门对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form ref="deptForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="tableData"
            :props="{ label: 'label', value: 'id' }"
            placeholder="请选择上级部门"
            check-strictly
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" />
        </el-form-item>
        <el-form-item label="部门状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
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
import { parseTime } from '@/utils/ruoyi'  // 引入时间格式化工具函数

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({
  parentId: null,
  deptName: '',
  orderNum: 0,
  status: 1
})

const rules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入显示排序', trigger: 'blur' }
  ]
}

// 获取部门列表
const getList = async () => {
  try {
    loading.value = true
    const res = await request.get('/department/tree')
    if (res.code === 200 && Array.isArray(res.data)) {
      console.log('获取到部门数据:', res.data)
      tableData.value = res.data
    } else {
      ElMessage.error('获取部门列表失败: 响应格式错误')
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    ElMessage.error('获取部门列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 新增部门
const handleAdd = (row) => {
  dialogTitle.value = '添加部门'
  form.value = {
    parentId: row ? row.id : null,
    deptName: '',
    orderNum: 0,
    status: 1
  }
  dialogVisible.value = true
}

// 修改部门
const handleEdit = (row) => {
  dialogTitle.value = '修改部门'
  form.value = {
    id: row.id,
    parentId: row.parentId,
    deptName: row.label,
    orderNum: row.orderNum || 0,
    status: row.status || 1
  }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    const url = form.value.id ? '/department/update' : '/department/add'
    const method = form.value.id ? 'put' : 'post'
    
    // 构造提交的数据
    const submitData = {
      ...form.value,
      label: form.value.deptName  // 添加 label 字段
    }
    console.log('提交的数据:', submitData)
    
    await request[method](url, submitData)
    ElMessage.success(form.value.id ? '修改成功' : '新增成功')
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交部门信息失败:', error)
    ElMessage.error('提交部门信息失败: ' + error.message)
  }
}

// 删除部门
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认要删除该部门吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await request.delete(`/department/${row.id}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除部门失败:', error)
      ElMessage.error('删除部门失败: ' + error.message)
    }
  })
}

// 添加表格高度计算
const tableHeight = ref(500)  // 默认高度

// 计算表格高度的函数
const calculateTableHeight = () => {
  // 窗口高度减去其他元素的高度，留出合适的空间
  tableHeight.value = window.innerHeight - 180  // 180是顶部和底部预留空间
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
    height: calc(100% - 60px);  /* 减去header高度 */
  }
}

.table-wrapper {
  height: 100%;
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

.dialog-footer {
  text-align: right;
  padding-top: 10px;
}
</style> 