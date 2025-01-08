<template>
  <div class="register-container">
    <el-form
      ref="registerFormRef"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
    >
      <div class="title">
        <h3>注册账号</h3>
      </div>
      
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          placeholder="用户名"
          prefix-icon="User"
        />
      </el-form-item>
      
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="密码"
          prefix-icon="Lock"
        />
      </el-form-item>
      
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="确认密码"
          prefix-icon="Lock"
        />
      </el-form-item>
      
      <el-form-item prop="employeeNo">
        <el-input
          v-model="registerForm.employeeNo"
          placeholder="工号"
          prefix-icon="Ticket"
        />
      </el-form-item>
      
      <el-form-item prop="name">
        <el-input
          v-model="registerForm.name"
          placeholder="姓名"
          prefix-icon="User"
        />
      </el-form-item>
      
      <el-form-item prop="gender">
        <el-select v-model="registerForm.gender" placeholder="性别" style="width: 100%">
          <el-option label="男" :value="1" />
          <el-option label="女" :value="0" />
        </el-select>
      </el-form-item>
      
      <el-form-item prop="phone">
        <el-input
          v-model="registerForm.phone"
          placeholder="手机号"
          prefix-icon="Phone"
        />
      </el-form-item>
      
      <el-form-item prop="email">
        <el-input
          v-model="registerForm.email"
          placeholder="邮箱"
          prefix-icon="Message"
        />
      </el-form-item>
      
      <el-form-item prop="deptId">
        <el-select 
          v-model="registerForm.deptId"
          placeholder="所属部门"
          style="width: 100%"
        >
          <el-option
            v-for="dept in deptList"
            :key="dept.id"
            :label="dept.deptName"
            :value="dept.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item prop="postId">
        <el-select
          v-model="registerForm.postId"
          placeholder="岗位"
          style="width: 100%"
        >
          <el-option
            v-for="post in postList"
            :key="post.id"
            :label="post.postName"
            :value="post.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item>
        <el-button
          :loading="loading"
          type="primary"
          style="width: 100%"
          @click="handleRegister"
        >
          注册
        </el-button>
      </el-form-item>
      
      <div class="login-link">
        <el-link type="primary" @click="$router.push('/login')">返回登录</el-link>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)
const deptList = ref([])
const postList = ref([])

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  employeeNo: '',
  name: '',
  gender: '',
  phone: '',
  email: '',
  deptId: '',
  postId: ''
})

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  employeeNo: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  deptId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  postId: [
    { required: true, message: '请选择岗位', trigger: 'change' }
  ]
}

// 获取部门列表
const getDeptList = async () => {
  try {
    const res = await request.get('/department/list')
    deptList.value = res.data
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

// 获取岗位列表
const getPostList = async () => {
  try {
    const res = await request.get('/post/list')
    postList.value = res.data
  } catch (error) {
    console.error('获取岗位列表失败:', error)
  }
}

const handleRegister = () => {
  registerFormRef.value.validate(async valid => {
    if (valid) {
      try {
        loading.value = true
        await request.post('/auth/register', registerForm)
        ElMessage.success('注册成功')
        router.push('/login')
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  getDeptList()
  getPostList()
})
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  padding: 40px 0;
}

.register-form {
  width: 400px;
  padding: 35px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 30px;
}

.title h3 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.login-link {
  text-align: right;
  margin-top: 10px;
}
</style> 