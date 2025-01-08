<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="welcome-text">
          <h2>企业无忧</h2>
          <p>Enterprise Employee Management System</p>
        </div>
      </div>
      <div class="login-form">
        <div class="login-header">
          <h3>欢迎登录</h3>
          <p>「 一个暖炉，一杯水，一本书，时光在淅沥的雨声中慢下来。 」</p>
        </div>
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form-content"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              clearable
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              :loading="loading"
              type="primary"
              class="login-button"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-box {
  width: 900px;
  height: 500px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  display: flex;
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
}

.welcome-text {
  text-align: center;
  color: #ffffff;
}

.welcome-text h2 {
  font-size: 32px;
  margin-bottom: 16px;
  font-weight: 600;
}

.welcome-text p {
  font-size: 16px;
  opacity: 0.9;
}

.login-form {
  width: 400px;
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 14px;
  color: #666;
}

.login-form-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.el-input) {
  height: 40px;
  
  .el-input__wrapper {
    background-color: #f5f7fa;
    border: none;
    border-radius: 8px;
    padding: 0 15px;
    
    &.is-focus {
      box-shadow: 0 0 0 1px #1890ff;
    }
  }
  
  .el-input__inner {
    height: 40px;
  }
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-button {
  width: 100%;
  height: 40px;
  border-radius: 8px;
  font-size: 16px;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  border: none;
  
  &:hover {
    opacity: 0.9;
  }
  
  &:active {
    opacity: 0.8;
  }
}

/* 添加响应式设计 */
@media screen and (max-width: 992px) {
  .login-box {
    width: 100%;
    max-width: 500px;
    height: auto;
    flex-direction: column;
  }
  
  .login-left {
    padding: 30px;
  }
  
  .login-form {
    width: 100%;
    padding: 30px;
  }
}
</style>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  loginFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        await userStore.login(loginForm.value)
        router.push('/')
        ElMessage.success('登录成功')
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script> 