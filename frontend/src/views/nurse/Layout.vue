<template>
  <el-container style="height:100vh">
    <el-aside width="200px" style="background:#304156">
      <div style="color:#fff;text-align:center;padding:20px 0;font-size:16px;font-weight:bold">颐养中心管理系统</div>
      <el-menu :default-active="route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/nurse/elderly"><el-icon><Search /></el-icon> 老人服务</el-menu-item>
        <el-menu-item index="/nurse/records"><el-icon><List /></el-icon> 我的护理记录</el-menu-item>
        <el-menu-item index="/nurse/all-records"><el-icon><Collection /></el-icon> 全部护理记录</el-menu-item>
        <el-menu-item index="/nurse/services"><el-icon><Service /></el-icon> 管家服务管理</el-menu-item>
        <el-menu-item index="/nurse/diet"><el-icon><DishDot /></el-icon> 饮食偏好查询</el-menu-item>
        <el-menu-item index="/nurse/messages">
          <el-icon><Bell /></el-icon> 消息通知
          <el-badge v-if="unreadCount > 0" :value="unreadCount" style="margin-left:8px" />
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #dcdfe6;display:flex;align-items:center;justify-content:space-between">
        <span style="font-size:16px">护工：{{ user.realName || user.username }}</span>
        <el-button type="danger" size="small" @click="logout">退出登录</el-button>
      </el-header>
      <el-main style="background:#f0f2f5">
        <router-view :nurse-name="user.realName || user.username" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, List, Collection, Service, DishDot, Bell } from '@element-plus/icons-vue'
import api from '../../api'

const router = useRouter()
const route = useRoute()
const user = JSON.parse(sessionStorage.getItem('user') || '{}')
const unreadCount = ref(0)

onMounted(async () => {
  const name = user.realName || user.username
  if (name) {
    const { data } = await api.getUnreadCount(name)
    if (data.success) unreadCount.value = data.data
  }
})

function logout() {
  sessionStorage.clear()
  router.push('/login')
}
</script>
