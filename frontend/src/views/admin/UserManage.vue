<template>
  <div>
    <el-card>
      <template #header><b>用户管理</b></template>
      <el-row :gutter="10" style="margin-bottom:15px">
        <el-col :span="6"><el-input v-model="searchKeyword" placeholder="搜索关键词" /></el-col>
        <el-col :span="4">
          <el-select v-model="searchType" placeholder="搜索方式">
            <el-option label="按账号" value="username" />
            <el-option label="按角色" value="role" />
            <el-option label="按姓名" value="name" />
          </el-select>
        </el-col>
        <el-col :span="4"><el-button type="primary" @click="doSearch">查询</el-button><el-button @click="loadUsers">全部</el-button></el-col>
        <el-col :span="10" style="text-align:right"><el-button type="success" @click="showAddDialog">添加用户</el-button></el-col>
      </el-row>
      <el-table :data="users" border stripe>
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="username" label="账号" width="100" />
        <el-table-column label="角色" width="80">
          <template #default="{row}">{{ row.role === 'admin' ? '管理员' : '护工' }}</template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{row}">
            <el-button type="primary" size="small" @click="showEditDialog(row)">修改</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/修改对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" :placeholder="isEdit ? '不修改请留空' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="管理员" value="admin" />
            <el-option label="护工" value="nurse" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const users = ref([])
const searchKeyword = ref('')
const searchType = ref('username')
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const isEdit = ref(false)

const form = reactive({
  username: '', password: '', role: 'nurse', realName: '', phone: ''
})

onMounted(loadUsers)

async function loadUsers() {
  const { data } = await api.getUsers()
  if (data.success) users.value = data.data
}

async function doSearch() {
  if (!searchKeyword.value) { loadUsers(); return }
  const { data } = await api.searchUsers(searchKeyword.value, searchType.value)
  if (data.success) users.value = data.data
}

function showAddDialog() {
  isEdit.value = false
  dialogTitle.value = '添加用户'
  Object.assign(form, { username: '', password: '', role: 'nurse', realName: '', phone: '' })
  dialogVisible.value = true
}

function showEditDialog(user) {
  isEdit.value = true
  dialogTitle.value = '修改用户'
  Object.assign(form, {
    username: user.username, password: '', role: user.role,
    realName: user.realName || '', phone: user.phone || ''
  })
  dialogVisible.value = true
}

async function handleSave() {
  try {
    const payload = {
      password: form.password,
      role: form.role,
      realName: form.realName,
      phone: form.phone
    }
    if (isEdit.value) {
      const { data } = await api.updateUser(form.username, payload)
      data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
    } else {
      payload.username = form.username
      const { data } = await api.addUser(payload)
      data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
    }
    dialogVisible.value = false
    loadUsers()
  } catch { ElMessage.error('操作失败') }
}

async function handleDelete(user) {
  try {
    await ElMessageBox.confirm(`确定删除用户 "${user.username}" 吗？`, '确认删除', { type: 'warning' })
    const { data } = await api.deleteUser(user.username)
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
    loadUsers()
  } catch { /* cancelled */ }
}
</script>
