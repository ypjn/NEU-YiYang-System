<template>
  <div>
    <el-card>
      <template #header><b>员工管理</b></template>
      <el-row :gutter="10" style="margin-bottom:15px">
        <el-col :span="6"><el-input v-model="searchPos" placeholder="按职位搜索" /></el-col>
        <el-col :span="4"><el-button type="primary" @click="loadByPos">查询</el-button><el-button @click="loadAll">全部</el-button></el-col>
        <el-col :span="14" style="text-align:right"><el-button type="success" @click="showDialog()">添加员工</el-button></el-col>
      </el-row>
      <el-table :data="employees" border stripe>
        <el-table-column prop="employeeId" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="position" label="职位" width="100" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="hireDate" label="入职日期" width="120" />
        <el-table-column prop="salary" label="薪资" width="100" />
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column label="操作" width="180">
          <template #default="{row}">
            <el-button type="primary" size="small" @click="showDialog(row)">修改</el-button>
            <el-button type="danger" size="small" @click="doDelete(row.employeeId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialog" width="500px">
      <el-form :model="form" label-width="80px">
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="性别"><el-select v-model="form.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="职位"><el-input v-model="form.position" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="身份证号"><el-input v-model="form.idCard" /></el-form-item>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="入职日期"><el-input v-model="form.hireDate" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="薪资"><el-input-number v-model="form.salary" :min="0" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
        <el-form-item v-if="isEdit" label="状态">
          <el-select v-model="form.status">
            <el-option label="在职" value="在职" />
            <el-option label="离职" value="离职" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const employees = ref([])
const searchPos = ref('')
const dialog = ref(false)
const dialogTitle = ref('添加员工')
const isEdit = ref(false)

const form = reactive({
  employeeId: '', name: '', gender: '男', position: '', phone: '',
  idCard: '', hireDate: '', salary: 0, remark: '', status: '在职'
})

onMounted(loadAll)

async function loadAll() {
  const { data } = await api.getEmployees({})
  if (data.success) employees.value = data.data
}

async function loadByPos() {
  const { data } = await api.getEmployees({ position: searchPos.value })
  if (data.success) employees.value = data.data
}

function showDialog(emp) {
  if (emp) {
    isEdit.value = true; dialogTitle.value = '修改员工'
    Object.assign(form, { ...emp })
  } else {
    isEdit.value = false; dialogTitle.value = '添加员工'
    Object.assign(form, { employeeId: '', name: '', gender: '男', position: '', phone: '', idCard: '', hireDate: '', salary: 0, remark: '', status: '在职' })
  }
  dialog.value = true
}

async function save() {
  if (isEdit.value) {
    const { data } = await api.updateEmployee(form.employeeId, form)
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  } else {
    const { data } = await api.addEmployee(form)
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  }
  dialog.value = false; loadAll()
}

async function doDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除该员工？', '确认', { type: 'warning' })
    const { data } = await api.deleteEmployee(id)
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
    loadAll()
  } catch { /* cancelled */ }
}
</script>
