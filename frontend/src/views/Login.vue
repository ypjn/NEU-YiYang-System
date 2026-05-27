<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 style="text-align:center;margin:0">东软颐养中心管理系统</h2>
      </template>
      <el-form :model="form" label-width="60px">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password @keyup.enter="login" />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio value="admin">管理员</el-radio>
            <el-radio value="nurse">护工</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="login" :loading="loading">登 录</el-button>
          <div style="text-align:center;margin-top:8px;font-size:13px;color:#909399">
            没有账号？<el-link type="primary" @click="$router.push('/register')">账号注册</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  role: 'admin'
})

async function login() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const { data } = await api.login(form)
    if (data.success) {
      sessionStorage.setItem('user', JSON.stringify(data))
      sessionStorage.setItem('token', data.token)
      ElMessage.success('登录成功')
      router.push(data.role === 'admin' ? '/admin' : '/nurse')
    } else {
      ElMessage.error(data.message)
    }
  } catch {
    ElMessage.error('登录失败，请检查网络')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
}
</style>
