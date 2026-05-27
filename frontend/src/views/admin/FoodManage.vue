<template>
  <el-card>
    <template #header>
      <el-button type="success" size="small" @click="showAdd">添加食材</el-button>
    </template>
    <el-table :data="foods" border stripe>
      <el-table-column prop="foodId" label="编号" width="80" />
      <el-table-column prop="foodName" label="名称" width="120" />
      <el-table-column prop="category" label="类别" width="80" />
      <el-table-column prop="unit" label="单位" width="60" />
      <el-table-column prop="price" label="单价" width="80" />
      <el-table-column prop="nutrition" label="营养信息" min-width="150" />
      <el-table-column prop="remark" label="备注" width="120" />
      <el-table-column label="操作" width="150">
        <template #default="{row}">
          <el-button type="primary" size="small" @click="showEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="doDelete(row.foodId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="editing ? '编辑食材' : '添加食材'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.foodName" /></el-form-item>
        <el-form-item label="类别"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="营养信息"><el-input v-model="form.nutrition" /></el-form-item>
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

const foods = ref([])
const dialogVisible = ref(false)
const editing = ref(false)
const editId = ref('')

const form = reactive({ foodName: '', category: '', unit: '', price: 0, nutrition: '', remark: '' })

onMounted(loadData)

async function loadData() {
  const { data } = await api.getFoods()
  if (data.success) foods.value = data.data
}

function resetForm() {
  form.foodName = ''; form.category = ''; form.unit = ''; form.price = 0
  form.nutrition = ''; form.remark = ''
}

function showAdd() {
  resetForm(); editing.value = false; dialogVisible.value = true
}

function showEdit(row) {
  editId.value = row.foodId
  form.foodName = row.foodName; form.category = row.category; form.unit = row.unit
  form.price = row.price; form.nutrition = row.nutrition; form.remark = row.remark
  editing.value = true; dialogVisible.value = true
}

async function doSave() {
  if (!form.foodName) { ElMessage.warning('请输入名称'); return }
  const { data } = editing.value
    ? await api.updateFood(editId.value, form)
    : await api.addFood(form)
  if (data.success) { ElMessage.success(data.message); dialogVisible.value = false; loadData() }
  else ElMessage.error(data.message)
}

async function doDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除该食材？', '确认', { type: 'warning' })
    const { data } = await api.deleteFood(id)
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
    loadData()
  } catch { /* cancelled */ }
}
</script>
