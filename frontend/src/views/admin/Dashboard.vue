<template>
  <div>
    <el-row :gutter="15" style="margin-bottom:15px">
      <el-col :span="6">
        <el-card shadow="hover" style="text-align:center;border-top:3px solid #409EFF">
          <div style="font-size:14px;color:#909399">在住老人</div>
          <div style="font-size:36px;font-weight:bold;color:#409EFF">{{ stats.elderlyActive }}</div>
          <div style="font-size:12px;color:#c0c4cc">共 {{ stats.elderlyTotal }} 人</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" style="text-align:center;border-top:3px solid #67C23A">
          <div style="font-size:14px;color:#909399">床位占用</div>
          <div style="font-size:36px;font-weight:bold;color:#67C23A">{{ stats.bedOccupied }} / {{ stats.bedTotal }}</div>
          <div style="font-size:12px;color:#c0c4cc">空闲 {{ stats.bedFree }} 张</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" style="text-align:center;border-top:3px solid #E6A23C">
          <div style="font-size:14px;color:#909399">外出中</div>
          <div style="font-size:36px;font-weight:bold;color:#E6A23C">{{ stats.outCount }}</div>
          <div style="font-size:12px;color:#c0c4cc">待处理退住 {{ stats.checkoutPending }} 条</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" style="text-align:center;border-top:3px solid #909399">
          <div style="font-size:14px;color:#909399">员工人数</div>
          <div style="font-size:36px;font-weight:bold;color:#909399">{{ stats.employeeCount }}</div>
          <div style="font-size:12px;color:#c0c4cc">护理记录 {{ stats.todayRecords }} 条</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="15">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><b>床位占用率</b></template>
          <div style="text-align:center;padding:20px">
            <el-progress type="dashboard" :percentage="occupancyRate" :color="rateColor" :stroke-width="20" style="width:200px;height:200px" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><b>快捷入口</b></template>
          <el-row :gutter="10">
            <el-col :span="8" style="margin-bottom:10px">
              <el-button type="primary" style="width:100%;height:80px" @click="$router.push('/admin/elderly')">
                <div style="font-size:18px">入住登记</div>
              </el-button>
            </el-col>
            <el-col :span="8" style="margin-bottom:10px">
              <el-button type="success" style="width:100%;height:80px" @click="$router.push('/admin/nursing-record')">
                <div style="font-size:18px">护理记录</div>
              </el-button>
            </el-col>
            <el-col :span="8" style="margin-bottom:10px">
              <el-button type="warning" style="width:100%;height:80px" @click="$router.push('/admin/out-manage')">
                <div style="font-size:18px">外出管理</div>
              </el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../api'

const stats = ref({
  elderlyTotal: 0, elderlyActive: 0,
  bedTotal: 0, bedOccupied: 0, bedFree: 0,
  outCount: 0, checkoutPending: 0,
  employeeCount: 0, todayRecords: 0
})

const occupancyRate = computed(() => {
  if (stats.value.bedTotal === 0) return 0
  return Math.round((stats.value.bedOccupied / stats.value.bedTotal) * 100)
})

const rateColor = computed(() => {
  if (occupancyRate.value > 80) return '#F56C6C'
  if (occupancyRate.value > 50) return '#E6A23C'
  return '#67C23A'
})

onMounted(async () => {
  const { data } = await api.getDashboard()
  if (data.success) stats.value = data.data
})
</script>
