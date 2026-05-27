<template>
  <el-card>
    <template #header><b>我的护理记录</b></template>
    <el-table :data="records" border stripe v-loading="loading">
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

const props = defineProps({ nurseName: String })
const records = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  const { data } = await api.getNurseRecords(props.nurseName)
  if (data.success) records.value = data.data
  loading.value = false
})
</script>
