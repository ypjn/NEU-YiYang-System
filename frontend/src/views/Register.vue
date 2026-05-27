<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2 style="text-align:center;margin:0">账号注册</h2>
      </template>
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPwd">
          <el-input v-model="form.confirmPwd" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="doRegister" :loading="loading">注 册</el-button>
        </el-form-item>
        <div style="text-align:center;font-size:13px;color:#909399">
          已有账号？<el-link type="primary" @click="$router.push('/login')">返回登录</el-link>
        </div>
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
const formRef = ref(null)

const form = reactive({
  username: '',
  password: '',
  confirmPwd: '',
  realName: '',
  phone: ''
})

const validateConfirmPwd = (_rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次密码输入不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPwd: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

async function doRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const { data } = await api.register({
      username: form.username,
      password: form.password,
      realName: form.realName,
      phone: form.phone
    })
    if (data.success) {
      ElMessage.success(data.message)
      router.push('/login')
    } else {
      ElMessage.error(data.message)
    }
  } catch {
    ElMessage.error('注册失败，请检查网络')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.register-card {
  width: 420px;
}
</style>
