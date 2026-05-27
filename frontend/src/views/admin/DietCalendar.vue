<template>
  <el-card>
    <template #header>
      <el-button type="success" size="small" @click="showAdd">添加食谱</el-button>
    </template>
    <el-table :data="calendar" border stripe>
      <el-table-column prop="calendarId" label="编号" width="80" />
      <el-table-column prop="calendarDate" label="日期" width="120" />
      <el-table-column prop="mealType" label="餐次" width="80" />
      <el-table-column prop="foodIds" label="食材ID（逗号分隔）" min-width="200" />
      <el-table-column prop="remark" label="备注" width="120" />
      <el-table-column label="操作" width="150">
        <template #default="{row}">
          <el-button type="primary" size="small" @click="showEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="doDelete(row.calendarId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="editing ? '编辑食谱' : '添加食谱'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="日期"><el-input v-model="form.calendarDate" placeholder="yyyy-MM-dd" /></el-form-item>
        <el-form-item label="餐次">
          <el-select v-model="form.mealType">
            <el-option label="早餐" value="早餐" />
            <el-option label="午餐" value="午餐" />
            <el-option label="晚餐" value="晚餐" />
          </el-select>
        </el-form-item>
        <el-form-item label="食材ID"><el-input v-model="form.foodIds" placeholder="多个ID用逗号分隔" /></el-form-item>
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

const calendar = ref([])
const dialogVisible = ref(false)
const editing = ref(false)
const editId = ref('')

const form = reactive({ calendarDate: '', mealType: '午餐', foodIds: '', remark: '' })

onMounted(loadData)

async function loadData() {
  const { data } = await api.getDietCalendar()
  if (data.success) calendar.value = data.data
}

function resetForm() {
  form.calendarDate = ''; form.mealType = '午餐'; form.foodIds = ''; form.remark = ''
}

function showAdd() {
  resetForm(); editing.value = false; dialogVisible.value = true
}

function showEdit(row) {
  editId.value = row.calendarId
  form.calendarDate = row.calendarDate; form.mealType = row.mealType
  form.foodIds = row.foodIds; form.remark = row.remark
  editing.value = true; dialogVisible.value = true
}

async function doSave() {
  if (!form.calendarDate) { ElMessage.warning('请输入日期'); return }
  const { data } = editing.value
    ? await api.updateDietCalendar(editId.value, form)
    : await api.addDietCalendar(form)
  if (data.success) { ElMessage.success(data.message); dialogVisible.value = false; loadData() }
  else ElMessage.error(data.message)
}

async function doDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除该食谱？', '确认', { type: 'warning' })
    const { data } = await api.deleteDietCalendar(id)
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
    loadData()
  } catch { /* cancelled */ }
}
</script>
