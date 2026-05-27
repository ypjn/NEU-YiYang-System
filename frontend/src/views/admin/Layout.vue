<template>
  <el-container style="height:100vh">
    <el-aside width="200px" style="background:#304156">
      <div style="color:#fff;text-align:center;padding:20px 0;font-size:16px;font-weight:bold">颐养中心管理系统</div>
      <el-menu :default-active="route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/admin/dashboard"><el-icon><DataBoard /></el-icon> 系统首页</el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><User /></el-icon> 用户管理</el-menu-item>
        <el-menu-item index="/admin/elderly"><el-icon><UserFilled /></el-icon> 老人管理</el-menu-item>
        <el-sub-menu index="nursing">
          <template #title><el-icon><FirstAidKit /></el-icon> 护理管理</template>
          <el-menu-item index="/admin/nursing-level">护理级别</el-menu-item>
          <el-menu-item index="/admin/nursing-project">护理项目</el-menu-item>
          <el-menu-item index="/admin/nursing-record">护理记录</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/admin/beds"><el-icon><OfficeBuilding /></el-icon> 床位管理</el-menu-item>
        <el-sub-menu index="out-checkout">
          <template #title><el-icon><Clock /></el-icon> 外出/退住管理</template>
          <el-menu-item index="/admin/out-manage">外出管理</el-menu-item>
          <el-menu-item index="/admin/checkout-manage">退住管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="diet">
          <template #title><el-icon><DishDot /></el-icon> 膳食管理</template>
          <el-menu-item index="/admin/foods">食材管理</el-menu-item>
          <el-menu-item index="/admin/diet-calendar">膳食日历</el-menu-item>
          <el-menu-item index="/admin/diet-preference">饮食偏好</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/admin/employees"><el-icon><Avatar /></el-icon> 员工管理</el-menu-item>
        <el-menu-item index="/admin/services"><el-icon><Service /></el-icon> 管家服务管理</el-menu-item>
        <el-menu-item index="/admin/logs"><el-icon><Document /></el-icon> 操作日志</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #dcdfe6;display:flex;align-items:center;justify-content:space-between">
        <span style="font-size:16px">管理员：{{ user.realName || user.username }}</span>
        <el-button type="danger" size="small" @click="logout">退出登录</el-button>
      </el-header>
      <el-main style="background:#f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { DataBoard, User, UserFilled, FirstAidKit, OfficeBuilding, Clock, DishDot, Avatar, Service, Document } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const user = JSON.parse(sessionStorage.getItem('user') || '{}')

function logout() {
  sessionStorage.clear()
  router.push('/login')
}
</script>
