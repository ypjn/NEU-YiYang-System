<template>
  <div>
    <el-row :gutter="10" style="margin-bottom:15px">
      <el-col :span="6"><el-input v-model="outFilter" placeholder="按状态/老人ID筛选" /></el-col>
      <el-col :span="4">
        <el-button type="primary" @click="loadOut">查询</el-button>
        <el-button @click="outFilter='';loadOut()">全部</el-button>
      </el-col>
    </el-row>
    <el-table :data="outList" border stripe>
      <el-table-column prop="outId" label="ID" width="80" />
      <el-table-column prop="customerId" label="老人ID" width="80" />
      <el-table-column prop="outTime" label="外出时间" width="180" />
      <el-table-column prop="expectedReturnTime" label="预计归来" width="180" />
      <el-table-column prop="actualReturnTime" label="实际归来" width="180" />
      <el-table-column prop="companion" label="陪同人" width="100" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" min-width="200">
        <template #default="{row}">
          <el-button v-if="row.status === '外出中'" type="success" size="small" @click="showReturn(row)">登记归来</el-button>
          <el-button v-if="row.status === '外出中'" type="warning" size="small" @click="doTimeout(row.outId)">标记超时</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="登记归来" v-model="returnVisible" width="400px">
      <el-form-item label="归来时间">
        <el-input v-model="returnTime" placeholder="yyyy-MM-dd HH:mm" />
      </el-form-item>
      <template #footer>
        <el-button @click="returnVisible = false">取消</el-button>
        <el-button type="primary" @click="doReturn">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const outList = ref([])
const outFilter = ref('')
const returnVisible = ref(false)
const returnTime = ref('')
const currentOutId = ref('')

onMounted(loadOut)

async function loadOut() {
  const { data } = await api.getOutRegs({ status: outFilter.value, customerId: outFilter.value })
  if (data.success) outList.value = data.data
}

function showReturn(out) {
  currentOutId.value = out.outId
  returnTime.value = new Date().toISOString().replace('T', ' ').slice(0, 16)
  returnVisible.value = true
}

async function doReturn() {
  const { data } = await api.markReturn(currentOutId.value, { actualReturnTime: returnTime.value })
  data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  returnVisible.value = false
  loadOut()
}

async function doTimeout(id) {
  try {
    await ElMessageBox.confirm('确定标记为超时未归？', '确认', { type: 'warning' })
    const { data } = await api.markTimeout(id)
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
    loadOut()
  } catch { /* cancelled */ }
}
</script>
