<template>
  <el-table :data="checkoutList" border stripe>
    <el-table-column prop="checkoutId" label="ID" width="80" />
    <el-table-column prop="customerId" label="老人ID" width="80" />
    <el-table-column prop="checkoutDate" label="退住日期" width="120" />
    <el-table-column prop="reason" label="原因" width="200" />
    <el-table-column prop="remark" label="备注" />
    <el-table-column label="操作" width="200">
      <template #default="{row}">
        <el-button type="success" size="small" @click="doConfirm(row.checkoutId)">确认退住</el-button>
        <el-button type="danger" size="small" @click="doRevoke(row.checkoutId)">撤销</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const checkoutList = ref([])

onMounted(async () => {
  const { data } = await api.getCheckouts()
  if (data.success) checkoutList.value = data.data
})

async function doConfirm(id) {
  try {
    await ElMessageBox.confirm('确认退住将释放床位，确定？', '确认', { type: 'warning' })
    const { data } = await api.confirmCheckout(id)
    if (data.success) {
      ElMessage.success(data.message)
      const r = await api.getCheckouts()
      checkoutList.value = r.data.data
    } else {
      ElMessage.error(data.message)
    }
  } catch { /* cancelled */ }
}

async function doRevoke(id) {
  try {
    await ElMessageBox.confirm('确定撤销退住记录？', '确认', { type: 'warning' })
    const { data } = await api.revokeCheckout(id)
    if (data.success) {
      ElMessage.success(data.message)
      const r = await api.getCheckouts()
      checkoutList.value = r.data.data
    } else {
      ElMessage.error(data.message)
    }
  } catch { /* cancelled */ }
}
</script>
