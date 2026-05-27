<template>
  <el-card>
    <template #header>
      <el-button type="success" size="small" @click="showAdd">添加饮食偏好</el-button>
    </template>
    <el-table :data="preferences" border stripe>
      <el-table-column prop="preferenceId" label="编号" width="80" />
      <el-table-column prop="customerId" label="老人ID" width="80" />
      <el-table-column prop="preferenceType" label="偏好类型" width="100" />
      <el-table-column prop="description" label="口味描述" min-width="150" />
      <el-table-column prop="allergies" label="过敏源" width="120" />
      <el-table-column prop="taboos" label="忌口" width="120" />
      <el-table-column prop="remark" label="备注" width="120" />
      <el-table-column label="操作" width="150">
        <template #default="{row}">
          <el-button type="primary" size="small" @click="showEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="doDelete(row.preferenceId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="editing ? '编辑饮食偏好' : '添加饮食偏好'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="老人ID"><el-input v-model="form.customerId" /></el-form-item>
        <el-form-item label="偏好类型"><el-input v-model="form.preferenceType" /></el-form-item>
        <el-form-item label="口味描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="过敏源"><el-input v-model="form.allergies" /></el-form-item>
        <el-form-item label="忌口"><el-input v-model="form.taboos" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSave">{{ editing ? '保存' : '添加' }}</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const preferences = ref([])
const dialogVisible = ref(false)
const editing = ref(false)
const editId = ref('')

const form = reactive({ customerId: '', preferenceType: '', description: '', allergies: '', taboos: '', remark: '' })

onMounted(loadData)

async function loadData() {
  const { data } = await api.getDietPreferences()
  if (data.success) preferences.value = data.data
}

function resetForm() {
  form.customerId = ''; form.preferenceType = ''; form.description = ''
  form.allergies = ''; form.taboos = ''; form.remark = ''
}

function showAdd() {
  resetForm(); editing.value = false; dialogVisible.value = true
}

function showEdit(row) {
  editId.value = row.preferenceId
  form.customerId = row.customerId; form.preferenceType = row.preferenceType
  form.description = row.description; form.allergies = row.allergies
  form.taboos = row.taboos; form.remark = row.remark
  editing.value = true; dialogVisible.value = true
}

async function doSave() {
  if (!form.customerId) { ElMessage.warning('请输入老人ID'); return }
  const { data } = editing.value
    ? await api.updateDietPreference(editId.value, form)
    : await api.addDietPreference(form)
  if (data.success) { ElMessage.success(data.message); dialogVisible.value = false; loadData() }
  else ElMessage.error(data.message)
}

async function doDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除？', '确认', { type: 'warning' })
    const { data } = await api.deleteDietPreference(id)
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
    loadData()
  } catch { /* cancelled */ }
}
</script>
