<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>入职办理</span>
        </div>
      </template>

      <!-- 入职信息表单 -->
      <el-form
        ref="onboardFormRef"
        :model="onboardForm"
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="onboardForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="username">
              <el-input v-model="onboardForm.username" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="onboardForm.gender">
                <el-radio label="1">男</el-radio>
                <el-radio label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="deptId">
              <el-cascader
                v-model="onboardForm.deptId"
                :options="deptOptions"
                :props="{
                  value: 'id',
                  label: 'label',
                  children: 'children',
                  checkStrictly: true,
                  emitPath: false,
                }"
                placeholder="请选择部门"
                clearable
                style="width: 100%"
                @change="handleDeptChange"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="岗位" prop="postId">
              <el-select v-model="onboardForm.postId" placeholder="请选择岗位" style="width: 100%">
                <el-option
                  v-for="item in postOptions"
                  :key="item.id"
                  :label="item.postName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="onboardForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="onboardForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker
                v-model="onboardForm.entryDate"
                type="date"
                placeholder="请选择入职日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学历" prop="educationId">
              <el-select v-model="onboardForm.educationId" placeholder="请选择学历" style="width: 100%">
                <el-option
                  v-for="item in educationOptions"
                  :key="item.id"
                  :label="item.dictName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="政治面貌" prop="political">
              <el-select v-model="onboardForm.politicalId" placeholder="请选择政治面貌" style="width: 100%">
                <el-option
                  v-for="item in politicalOptions"
                  :key="item.id"
                  :label="item.dictName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="onboardForm.remark"
                type="textarea"
                placeholder="请输入备注信息"
                :rows="3"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const onboardFormRef = ref()
const deptOptions = ref([])
const postOptions = ref([])
const educationOptions = ref([])
const politicalOptions = ref([])

// 表单数据
const onboardForm = reactive({
  name: '',
  username: '',  // 工号
  gender: '1',
  deptId: '',     // 改为与后端一致的字段名
  postId: '',     // 改为与后端一致的字段名
  phone: '',
  email: '',
  entryDate: '',
  educationId: '',  // 改为 educationId
  politicalId: '',  // 改为 politicalId
  remark: ''
})

// 表单校验规则
const formRules = {
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  employeeNo: [
    { required: true, message: '请输入工号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: '工号只能包含字母和数字', trigger: 'blur' }
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

// 获取部门列表
const getDeptList = async () => {
  try {
    const res = await request.get('/department/tree')
    if (res.code === 200) {
      console.log('获取部门列表成功:', res.data)
      deptOptions.value = res.data
      console.log('处理后的部门数据:', deptOptions.value)
    } else {
      ElMessage.error('获取部门列表失败')
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    ElMessage.error('获取部门列表失败: ' + error.message)
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
      ElMessage.error('获取岗位列表失败')
    }
  } catch (error) {
    console.error('获取岗位列表失败:', error)
    ElMessage.error('获取岗位列表失败')
  }
}

// 监听部门选择变化
const handleDeptChange = (value) => {
  // 清空岗位选择
  if (onboardForm.deptId !== value) {
    onboardForm.postId = undefined
  }
  // 获取对应部门的岗位列表
  if (value) {
    getPostList(value)
  } else {
    postOptions.value = []
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

// 提交表单
const handleSubmit = () => {
  onboardFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        console.log('提交表单数据:', onboardForm)
        const res = await request.post('/hr/onboard', onboardForm)
        if (res.code === 200) {
          ElMessage.success('入职办理成功')
          resetForm()
        }
      } catch (error) {
        console.error('入职办理失败:', error)
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  onboardFormRef.value?.resetFields()
  Object.assign(onboardForm, {
    name: '',
    username: '',
    gender: '1',
    deptId: '',
    postId: '',
    phone: '',
    email: '',
    entryDate: '',
    educationId: '',  // 改为 educationId
    politicalId: '',  // 改为 politicalId
    remark: ''
  })
}

onMounted(() => {
  getDeptList()
  getEducationList()
  getPoliticalList()
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