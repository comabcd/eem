<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>岗位管理</span>
          <el-button type="primary" @click="handleAdd">新增岗位</el-button>
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
          <el-table-column align="center" prop="postCode" label="岗位编码">
            <template #default="scope">
              <span>{{ scope.row.postCode }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="department" label="所属部门">
            <template #default="scope">
              <span>{{ scope.row.deptName }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="postName" label="岗位名称">
            <template #default="scope">
              <span>{{ scope.row.postName }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="orderNum" label="显示顺序">
            <template #default="scope">
              <span>{{ scope.row.orderNum }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status == 1 ? 'success' : 'danger'">
                {{ scope.row.status == 1 ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
              {{ parseTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
            <template #default="scope">
              {{ parseTime(scope.row.updateTime) }}
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
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
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑岗位对话框 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      append-to-body
    >
      <el-form
        ref="postFormRef"
        :model="postForm"
        :rules="postRules"
        label-width="100px"
      >
        <el-form-item label="部门" prop="deptId">
          <el-cascader
            v-model="postForm.deptId"
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
          />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="postForm.postCode" placeholder="请输入岗位编码" />
        </el-form-item>
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="postForm.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="orderNum">
          <el-input-number v-model="postForm.orderNum" :min="1" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="postForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="postForm.remark"
            type="textarea"
            placeholder="请输入备注"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { parseTime } from '@/utils/ruoyi'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const postFormRef = ref()

// 对话框配置
const dialog = reactive({
  visible: false,
  title: '',
  type: 'add' // 'add' 或 'edit'
})

// 表单数据
const postForm = reactive({
  id: undefined,
  deptId: undefined,
  postCode: '',
  postName: '',
  orderNum: 1,
  status: 1,
  remark: ''
})

// 表单校验规则
const postRules = {
  deptId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  postCode: [
    { required: true, message: '请输入岗位编码', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  postName: [
    { required: true, message: '请输入岗位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 重置表单
const resetForm = () => {
  postForm.id = undefined
  postForm.deptId = undefined
  postForm.postCode = ''
  postForm.postName = ''
  postForm.orderNum = 1
  postForm.status = 1
  postForm.remark = ''
  postFormRef.value?.resetFields()
}

// 监听 tableData 的变化
watch(tableData, (newVal) => {
  console.log('tableData 发生变化:', newVal)
}, { deep: true })

// 获取岗位列表
const getList = async () => {
  try {
    loading.value = true
    const res = await request.get('/post/list', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    
    if (res.code === 200) {
      // 适配后端 PageResult 格式
      tableData.value = res.data.list      // 使用 list 而不是 rows
      total.value = res.data.total         // 总记录数
      currentPage.value = res.data.pageNum // 当前页码
      pageSize.value = res.data.pageSize   // 每页大小
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取岗位列表失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 新增岗位
const handleAdd = () => {
  dialog.visible = true
  dialog.title = '新增岗位'
  dialog.type = 'add'
  resetForm()
}

// 编辑岗位
const handleEdit = (row) => {
  dialog.visible = true
  dialog.title = '编辑岗位'
  dialog.type = 'edit'
  resetForm()
  // 使用 nextTick 确保表单重置后再填充数据
  nextTick(() => {
    Object.assign(postForm, row)
  })
}

// 提交表单
const handleSubmit = () => {
  postFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (dialog.type === 'add') {
          await request.post('/post/add', postForm)
          ElMessage.success('新增成功')
        } else {
          await request.put('/post/update', postForm)
          ElMessage.success('修改成功')
        }
        dialog.visible = false
        getList()
      } catch (error) {
        console.error('提交岗位信息失败:', error)
      }
    }
  })
}

// 删除岗位
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认要删除该岗位吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await request.delete(`/post/${row.id}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除岗位失败:', error)
    }
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1  // 切换每页显示数量时重置为第一页
  getList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

// 添加表格高度计算
const tableHeight = ref(500)  // 默认高度

// 计算表格高度的函数
const calculateTableHeight = () => {
  // 窗口高度减去其他元素的高度，留出合适的空间
  tableHeight.value = window.innerHeight - 240  // 240是顶部和分页器预留空间
}

// 添加部门相关的数据和方法
const deptOptions = ref([])  // 部门选项

// 获取部门列表
const getDeptList = async () => {
  try {
    const res = await request.get('/department/tree')
    if (res.code === 200) {
      console.log('获取部门列表成功:', res.data)
      deptOptions.value = res.data
    } else {
      ElMessage.error('获取部门列表失败')
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    ElMessage.error('获取部门列表失败')
  }
}

onMounted(() => {
  getList()
  getDeptList()
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
  height: calc(100% - 60px);  /* 减去分页器高度 */
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