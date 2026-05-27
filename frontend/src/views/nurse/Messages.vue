<template>
  <el-card>
    <template #header><b>消息通知</b></template>
    <el-table :data="messages" border stripe>
      <el-table-column prop="time" label="时间" width="170" />
      <el-table-column prop="content" label="消息内容" min-width="400" />
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.read ? 'info' : 'danger'" size="small">{{ row.read ? '已读' : '未读' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{row}">
          <el-button v-if="!row.read" type="primary" size="small" @click="doRead(row)">标记已读</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="messages.length === 0" description="暂无消息" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../api'

const props = defineProps({ nurseName: String })
const messages = ref([])

onMounted(loadMessages)

async function loadMessages() {
  const { data } = await api.getMessages(props.nurseName)
  if (data.success) messages.value = data.data
}

async function doRead(msg) {
  await api.markMessageRead(msg.messageId)
  msg.read = true
}
</script>
