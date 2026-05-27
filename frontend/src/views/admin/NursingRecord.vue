<template>
  <el-card>
    <el-table :data="records" border stripe>
      <el-table-column prop="id" label="记录号" width="80" />
      <el-table-column prop="elderlyId" label="老人编号" width="100" />
      <el-table-column prop="projectCode" label="项目编码" width="100" />
      <el-table-column prop="executeTime" label="执行时间" width="180" />
      <el-table-column prop="nurseName" label="护工" width="100" />
      <el-table-column prop="remark" label="备注" />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../api'

const records = ref([])

onMounted(async () => {
  const { data } = await api.getRecords()
  if (data.success) records.value = data.data
})
</script>
