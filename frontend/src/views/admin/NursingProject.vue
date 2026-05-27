<template>
  <el-card>
    <template #header><el-button type="success" size="small" @click="projectDialog = true">添加护理项目</el-button></template>
    <el-table :data="projects" border stripe>
      <el-table-column prop="code" label="编码" width="80" />
      <el-table-column prop="name" label="名称" width="150" />
      <el-table-column prop="category" label="类别" width="100" />
      <el-table-column prop="unit" label="单位" width="60" />
      <el-table-column prop="price" label="价格" width="80" />
      <el-table-column prop="cycle" label="周期" width="100" />
      <el-table-column prop="remark" label="适用等级" min-width="200" />
    </el-table>
    <el-dialog title="添加护理项目" v-model="projectDialog" width="500px">
      <el-form :model="projectForm" label-width="80px">
        <el-form-item label="编码"><el-input v-model="projectForm.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="projectForm.name" /></el-form-item>
        <el-form-item label="类别"><el-input v-model="projectForm.category" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="projectForm.unit" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="projectForm.price" :min="0" /></el-form-item>
        <el-form-item label="周期"><el-input v-model="projectForm.cycle" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="projectForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="projectDialog = false">取消</el-button>
        <el-button type="primary" @click="addProject">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const projects = ref([])
const projectDialog = ref(false)
const projectForm = reactive({ code: '', name: '', category: '', unit: '', price: 0, cycle: '', remark: '' })

onMounted(async () => {
  const { data } = await api.getProjects()
  if (data.success) projects.value = data.data
})

async function addProject() {
  const { data } = await api.addProject(projectForm)
  if (data.success) {
    ElMessage.success(data.message)
    projectDialog.value = false
    const r = await api.getProjects()
    projects.value = r.data.data
  } else {
    ElMessage.error(data.message)
  }
}
</script>
