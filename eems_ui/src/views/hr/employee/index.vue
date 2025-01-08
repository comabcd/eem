<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>员工管理</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入姓名"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="工号" prop="employeeNo">
          <el-input
            v-model="queryParams.employeeNo"
            placeholder="请输入工号"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-cascader
            v-model="queryParams.deptId"
            :options="deptOptions"
            :props="{
              value: 'id',
              label: 'label',
              children: 'children',
              checkStrictly: true,
              emitPath: false
            }"
            placeholder="请选择部门"
            clearable
            style="width: 180px"
            @change="handleSearchDeptChange"
          />
        </el-form-item>
        <el-form-item label="岗位" prop="postId">
          <el-select
            v-model="queryParams.postId"
            placeholder="请选择岗位"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in postOptions"
              :key="item.id"
              :label="item.postName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
          <el-button type="warning" :icon="Download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>

      <div class="table-wrapper">
        <el-table
          v-loading="loading"
          :data="employeeList"
          border
          :height="tableHeight"
          style="width: 100%"
        >
          <!-- <el-table-column type="selection" width="55" align="center" /> -->
          <el-table-column label="序号" type="index" width="60" align="center" />
          <el-table-column label="工号" align="center" prop="employeeNo" />
          <el-table-column label="姓名" align="center" prop="name" />
          <el-table-column label="性别" align="center" prop="gender">
            <template #default="scope">
              {{ scope.row.gender === 1 ? '男' : '女' }}
            </template>
          </el-table-column>
          <el-table-column label="部门" align="center" prop="department" />
          <el-table-column label="岗位" align="center" prop="post" />
          <el-table-column label="学历" align="center" prop="education" />
          <el-table-column label="政治面貌" align="center" prop="political" />
          <el-table-column label="联系电话" align="center" prop="phone" />
          <el-table-column label="邮箱" align="center" prop="email" show-overflow-tooltip />
          <el-table-column label="入职日期" align="center" prop="entryDate" width="100">
            <template #default="scope">
              {{ formatDate(scope.row.entryDate) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '在职' : '离职' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="200">
            <template #default="scope">
              <el-button
                type="text"
                :icon="Edit"
                @click="handleUpdate(scope.row)"
              >修改</el-button>
              <el-button
                type="text"
                :icon="Delete"
                @click="handleDelete(scope.row)"
                v-if="scope.row.status === 1"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页组件 -->
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

    <!-- 修改对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="修改员工信息"
      width="600px"
      append-to-body
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="工号" prop="employeeNo">
              <el-input v-model="editForm.employeeNo" placeholder="请输入工号" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="editForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="editForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="deptId">
              <el-cascader
                v-model="editForm.deptId"
                :options="deptOptions"
                :props="{
                  value: 'id',
                  label: 'label',
                  children: 'children',
                  checkStrictly: true,
                  emitPath: false
                }"
                placeholder="请选择部门"
                clearable
                style="width: 100%"
                @change="handleDeptChange"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位" prop="postId">
              <el-select v-model="editForm.postId" placeholder="请选择岗位" style="width: 100%">
                <el-option
                  v-for="item in postOptions"
                  :key="item.id"
                  :label="item.post"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历" prop="educationId">
              <el-select v-model="editForm.educationId" placeholder="请选择学历" style="width: 100%">
                <el-option
                  v-for="item in educationOptions"
                  :key="item.id"
                  :label="item.dictName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="政治面貌" prop="politicalId">
              <el-select v-model="editForm.politicalId" placeholder="请选择政治面貌" style="width: 100%">
                <el-option
                  v-for="item in politicalOptions"
                  :key="item.id"
                  :label="item.dictName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="editForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="editForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker
                v-model="editForm.entryDate"
                type="date"
                placeholder="请选择入职日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="editForm.remark"
                type="textarea"
                placeholder="请输入备注信息"
                :rows="3"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="dialogVisible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  employeeNo: '',
  deptId: undefined,
  postId: undefined
})

const loading = ref(false)
const total = ref(0)
const employeeList = ref([])
const deptOptions = ref([])
const postOptions = ref([])
const educationOptions = ref([])
const politicalOptions = ref([])

// 对话框显示状态
const dialogVisible = ref(false)
const editFormRef = ref()
const editForm = reactive({
  id: undefined,
  employeeNo: '',
  name: '',
  gender: 1,
  deptId: undefined,
  postId: undefined,
  educationId: undefined,
  politicalId: undefined,
  phone: '',
  email: '',
  entryDate: '',
  remark: ''
})

// 表单校验规则
const formRules = {
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  deptId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  postId: [
    { required: true, message: '请选择岗位', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  entryDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ],
  educationId: [
    { required: true, message: '请选择学历', trigger: 'change' }
  ],
  politicalId: [
    { required: true, message: '请选择政治面貌', trigger: 'change' }
  ]
}

// 获取员工列表
const getList = async () => {
  loading.value = true
  try {
    console.log('获取员工列表参数:', queryParams)
    const res = await request.get('/employee/search', {
      params: {
        pageNum: queryParams.pageNum,
        pageSize: queryParams.pageSize,
        name: queryParams.name || undefined,
        employeeNo: queryParams.employeeNo || undefined,
        deptId: queryParams.deptId || undefined,
        postId: queryParams.postId || undefined
      }
    })
    if (res.code === 200) {
      console.log('获取员工列表成功:', res.data)
      employeeList.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '获取员工列表失败')
    }
  } catch (error) {
    console.error('获取员工列表失败:', error)
    ElMessage.error('获取员工列表失败')
  }
  loading.value = false
}

// 获取部门列表
const getDeptList = async () => {
  try {
    const res = await request.get('/department/tree')
    if (res.code === 200) {
      console.log('获取部门列表成功', res.data)
      deptOptions.value = res.data
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    ElMessage.error('获取部门列表失败')
  }
}

// 获取岗位列表
const getPostList = async (deptId) => {
  try {
    const res = await request.get('/post/search', {
      params: { deptId }
    })
    if (res.code === 200) {
      console.log('获取岗位列表成功:', res.data)
      postOptions.value = res.data
    } else {
      ElMessage.error(res.msg || '获取岗位列表失败')
    }
  } catch (error) {
    console.error('获取岗位列表失败:', error)
    ElMessage.error('获取岗位列表失败')
  }
}

// 获取政治面貌列表
const getPoliticalList = async () => {
  try {
    const params = {
      dictType: '政治面貌',
      dictName: '',
      status: 1
    }
    const res = await request.get('/dict/list', { params })
    if (res.code === 200) {
      console.log('获取政治面貌列表成功:', res.data)
      politicalOptions.value = res.data.filter(item => item.status === 1)  // 只使用状态为1的数据
    } else {
      ElMessage.error('获取政治面貌列表失败')
    }
  } catch (error) {
    console.error('获取政治面貌列表失败:', error)
    ElMessage.error('获取政治面貌列表失败: ' + error.message)
  }
}

// 获取学历列表
const getEducationList = async () => {
  try {
    const params = {
      dictType: '学历',
      dictName: '',
      status: 1
    }
    const res = await request.get('/dict/list', { params })
    if (res.code === 200) {
      console.log('获取学历列表成功:', res.data)
      educationOptions.value = res.data.filter(item => item.status === 1)  // 只使用状态为1的数据
    } else {
      ElMessage.error('获取学历列表失败')
    }
  } catch (error) {
    console.error('获取学历列表失败:', error)
    ElMessage.error('获取学历列表失败: ' + error.message)
  }
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.name = ''
  queryParams.employeeNo = ''
  queryParams.deptId = undefined
  queryParams.postId = undefined
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  handleQuery()
}

// 新增按钮操作
const handleAdd = () => {
  router.push('/hr/onboard')
}

// 修改按钮操作
const handleUpdate = (row) => {
  dialogVisible.value = true
  nextTick(() => {
    editFormRef.value?.resetFields()
    Object.assign(editForm, {
      id: row.id,
      employeeNo: row.employeeNo,
      name: row.name,
      gender: row.gender,
      deptId: row.deptId,
      postId: row.postId,
      educationId: row.educationId,
      politicalId: row.politicalId,
      phone: row.phone,
      email: row.email,
      entryDate: row.entryDate,
      remark: row.remark
    })
  })
}

// 提交表单
const submitForm = () => {
  editFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.put('/employee/update', editForm)
        if (res.code === 200) {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        }
      } catch (error) {
        console.error('修改失败:', error)
        ElMessage.error('修改失败')
      }
    }
  })
}

// 删除按钮操作
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `是否为员工 ${row.name} 办理离职？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await request.delete('/hr/resign/' + row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 导出按钮操作
const handleExport = () => {
  // TODO: 实现导出功能
  console.log('导出')
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const dateObj = new Date(date)
  const year = dateObj.getFullYear()
  const month = String(dateObj.getMonth() + 1).padStart(2, '0')
  const day = String(dateObj.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 监听部门选择变化
const handleDeptChange = (value) => {
  // 清空岗位选择
  if (editForm.deptId !== value) {
    editForm.postId = undefined
  }
  // 获取对应部门的岗位列表
  if (value) {
    getPostList(value)
  } else {
    postOptions.value = []
  }
}

// 监听搜索表单部门选择变化
const handleSearchDeptChange = (value) => {
  // 清空岗位选择
  if (queryParams.deptId !== value) {
    queryParams.postId = undefined
  }
  // 获取对应部门的岗位列表
  if (value) {
    getPostList(value)
  } else {
    postOptions.value = []
  }
}

// 处理页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 处理每页条数改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getList()
}

// 添加表格高度计算
const tableHeight = ref(500)  // 默认高度

// 计算表格高度的函数
const calculateTableHeight = () => {
  // 窗口高度减去其他元素的高度，留出合适的空间
  tableHeight.value = window.innerHeight - 300  // 300是顶部和分页器预留空间
}

onMounted(() => {
  getList()
  getDeptList()
  getPoliticalList()
  getEducationList()
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
    position: relative;  /* 为分页器定位提供参考 */
  }
}

.table-wrapper {
  height: calc(100% - 120px);  /* 减去搜索表单和分页器高度 */
  overflow: hidden;
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