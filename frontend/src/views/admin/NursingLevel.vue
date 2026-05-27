<template>
  <el-card>
    <template #header><el-button type="success" size="small" @click="levelDialog = true">添加护理级别</el-button></template>
    <el-table :data="levels" border stripe>
      <el-table-column prop="code" label="编码" width="80" />
      <el-table-column prop="name" label="名称" width="120" />
      <el-table-column prop="description" label="描述" min-width="300" />
      <el-table-column prop="frequency" label="巡查频次" width="200" />
    </el-table>
    <el-dialog title="添加护理级别" v-model="levelDialog" width="500px">
      <el-form :model="levelForm" label-width="80px">
        <el-form-item label="编码"><el-input v-model="levelForm.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="levelForm.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="levelForm.description" type="textarea" /></el-form-item>
        <el-form-item label="巡查频次"><el-input v-model="levelForm.frequency" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="levelDialog = false">取消</el-button>
        <el-button type="primary" @click="addLevel">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const levels = ref([])
const levelDialog = ref(false)
const levelForm = reactive({ code: '', name: '', description: '', frequency: '' })

onMounted(async () => {
  const { data } = await api.getLevels()
  if (data.success) levels.value = data.data
})

async function addLevel() {
  const { data } = await api.addLevel(levelForm)
  if (data.success) {
    ElMessage.success(data.message)
    levelDialog.value = false
    const r = await api.getLevels()
    levels.value = r.data.data
  } else {
    ElMessage.error(data.message)
  }
}
</script>
