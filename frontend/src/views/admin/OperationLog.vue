<template>
  <el-card>
    <template #header>
      <b>操作日志</b>
      <el-input v-model="keyword" placeholder="搜索操作人/操作类型/操作对象" style="width:280px;margin-left:15px" size="small" clearable @input="loadData" />
    </template>
    <el-table :data="logs" border stripe max-height="calc(100vh - 200px)">
      <el-table-column prop="time" label="操作时间" width="170" />
      <el-table-column prop="operatorName" label="操作人" width="100" />
      <el-table-column prop="operatorRole" label="角色" width="80">
        <template #default="{row}">
          <el-tag :type="row.operatorRole === 'admin' ? 'danger' : 'info'" size="small">
            {{ row.operatorRole === 'admin' ? '管理员' : '护工' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="action" label="操作类型" width="130" />
      <el-table-column prop="target" label="操作模块" width="120" />
      <el-table-column prop="detail" label="操作详情" min-width="300" />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../api'

const logs = ref([])
const keyword = ref('')

onMounted(loadData)

async function loadData() {
  const { data } = await api.getOperationLogs(keyword.value)
  if (data.success) logs.value = data.data
}
</script>
