<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <!-- <el-button type="primary" @click="handleAdd">新增用户</el-button> -->
        </div>
      </template>
      
      <div class="table-wrapper">
        <el-table
          :data="tableData"
          style="width: 100%"
          v-loading="loading"
          :height="tableHeight"
          border
        >
          <el-table-column align="center" prop="username" label="用户名" />
          <el-table-column align="center" prop="realName" label="真实姓名" />
          <el-table-column align="center" prop="role" label="角色">
            <template #default="scope">
              {{ getRoleName(scope.row.role) }}
            </template>
          </el-table-column>
          <el-table-column align="center" prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="createTime" label="创建时间">
            <template #default="scope">
              {{ scope.row.createTime ? new Date(scope.row.createTime).toLocaleString() : '' }}
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="warning" link @click="handleResetPassword(scope.row)">重置密码</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

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

    <!-- 编辑用户对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form ref="userForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" align="center" prop="username">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" align="center" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="角色" align="center" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="系统管理员" :value="1" />
            <el-option label="部门主管" :value="2" />
            <el-option label="人力资源管理员" :value="3" />
            <el-option label="普通员工" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" align="center" prop="status">
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const deptOptions = ref([])

const form = ref({
  username: '',
  realName: '',
  role: 4,
  deptId: null,
  status: 1  // 默认启用
})

const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  deptId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取用户列表
const getList = async () => {
  try {
    loading.value = true
    const res = await request.get('/user/list')
    console.log('用户列表响应数据:', res)
    if (res.code === 200 && Array.isArray(res.data)) {
      // 处理分页
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      tableData.value = res.data.slice(start, end)
      total.value = res.data.length
      console.log('处理后的表格数据:', tableData.value)
    } else {
      ElMessage.error('获取用户列表失败: 响应格式错误')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取部门树形数据
const getDeptTree = async () => {
  try {
    const res = await request.get('/department/tree')
    if (res.code === 200 && Array.isArray(res.data)) {
      deptOptions.value = res.data
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    ElMessage.error('获取部门列表失败: ' + error.message)
  }
}

// 获取角色名称
const getRoleName = (role) => {
  const roleMap = {
    1: '系统管理员',
    2: '部门主管',
    3: '人力资源管理员',
    4: '普通员工'
  }
  return roleMap[role] || '未知角色'
}

// 编辑用户
const handleEdit = (row) => {
  console.log('修改用户数据:', row)
  dialogTitle.value = '编辑用户'
  form.value = {
    id: row.id,
    username: row.username,
    realName: row.realName,
    role: row.role,
    deptId: row.deptId || row.departmentId,  // 兼容两种字段名
    status: row.status || 1  // 如果没有状态值，默认为启用
  }
  console.log('编辑表单数据:', form.value)
  dialogVisible.value = true
}

// 重置密码
const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    '确认要将该用户的密码重置为 123456 吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request.put(`/user/resetPassword/${row.id}`, {
        password: '123456'
      })
      ElMessage.success('密码重置成功')
    } catch (error) {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败: ' + error.message)
    }
  })
}

// 提交表单
const submitForm = async () => {
  try {
    // 构造提交的数据
    const submitData = {
      ...form.value,
      departmentId: form.value.deptId  // 确保使用后端需要的字段名
    }
    console.log('提交的数据:', submitData)
    
    await request.put('/user/update', submitData)
    ElMessage.success('修改成功')
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交用户信息失败:', error)
    ElMessage.error('提交用户信息失败: ' + error.message)
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

// 添加表格高度计算
const tableHeight = ref(500)

const calculateTableHeight = () => {
  tableHeight.value = window.innerHeight - 240
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

.table-wrapper {
  height: calc(100% - 60px);
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