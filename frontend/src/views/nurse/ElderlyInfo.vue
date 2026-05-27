<template>
  <div>
    <el-card style="margin-bottom:15px">
      <template #header><b>老人信息查询</b></template>
      <el-row :gutter="10">
        <el-col :span="12"><el-input v-model="keyword" placeholder="输入老人编号或姓名" @keyup.enter="search" /></el-col>
        <el-col :span="4"><el-button type="primary" @click="search">查询</el-button></el-col>
      </el-row>
      <el-table v-if="elderlyList.length > 0" :data="elderlyList" border stripe style="margin-top:15px">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="name" label="姓名" width="80" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="nursingLevelCode" label="护理等级" width="100" />
        <el-table-column prop="roomNo" label="房间号" width="80" />
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column label="操作" width="280">
          <template #default="{row}">
            <el-button type="primary" size="small" @click="showCareRegister(row)">登记护理</el-button>
            <el-button type="warning" size="small" @click="showOutReg(row)">外出登记</el-button>
            <el-button type="success" size="small" @click="showHealth(row)">健康记录</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="请输入关键词搜索老人" />
    </el-card>

    <!-- 登记护理对话框 -->
    <el-dialog title="登记护理执行" v-model="careVisible" width="500px">
      <el-form-item label="老人">{{ currentElderly?.name }} ({{ currentElderly?.nursingLevelCode }})</el-form-item>
      <el-form-item label="选择项目">
        <el-select v-model="careForm.projectCode" placeholder="请选择护理项目">
          <el-option v-for="p in applicableProjects" :key="p.code" :label="p.code + ' ' + p.name + ' (¥' + p.price + ')'" :value="p.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注"><el-input v-model="careForm.remark" /></el-form-item>
      <template #footer>
        <el-button @click="careVisible = false">取消</el-button>
        <el-button type="primary" @click="doCareRegister">确定</el-button>
      </template>
    </el-dialog>

    <!-- 外出登记对话框 -->
    <el-dialog title="外出登记" v-model="outVisible" width="500px">
      <el-form-item label="老人">{{ currentElderly?.name }}</el-form-item>
      <el-form-item label="外出时间"><el-input v-model="outForm.outTime" placeholder="yyyy-MM-dd HH:mm" /></el-form-item>
      <el-form-item label="预计归来"><el-input v-model="outForm.expectedReturnTime" /></el-form-item>
      <el-form-item label="陪同人"><el-input v-model="outForm.companion" /></el-form-item>
      <el-form-item label="事由"><el-input v-model="outForm.reason" /></el-form-item>
      <template #footer>
        <el-button @click="outVisible = false">取消</el-button>
        <el-button type="primary" @click="doOutReg">确定</el-button>
      </template>
    </el-dialog>

    <!-- 健康记录对话框 -->
    <el-dialog title="登记健康记录" v-model="healthVisible" width="500px">
      <el-form :model="healthForm" label-width="80px">
        <el-form-item label="老人">{{ currentElderly?.name }}</el-form-item>
        <el-form-item label="记录日期"><el-input v-model="healthForm.recordDate" /></el-form-item>
        <el-form-item label="血压"><el-input v-model="healthForm.bloodPressure" /></el-form-item>
        <el-form-item label="心率"><el-input v-model="healthForm.heartRate" /></el-form-item>
        <el-form-item label="血糖"><el-input v-model="healthForm.bloodSugar" /></el-form-item>
        <el-form-item label="体重"><el-input v-model="healthForm.weight" /></el-form-item>
        <el-form-item label="体温"><el-input v-model="healthForm.temperature" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="healthForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="healthVisible = false">取消</el-button>
        <el-button type="primary" @click="doHealth">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const props = defineProps({ nurseName: String })

const keyword = ref('')
const elderlyList = ref([])
const currentElderly = ref(null)
const applicableProjects = ref([])
const careVisible = ref(false)
const outVisible = ref(false)
const healthVisible = ref(false)

const careForm = reactive({ projectCode: '', remark: '' })
const outForm = reactive({ outTime: '', expectedReturnTime: '', companion: '', reason: '' })
const healthForm = reactive({
  recordDate: new Date().toISOString().slice(0, 10),
  bloodPressure: '', heartRate: '', bloodSugar: '', weight: '', temperature: '', remark: ''
})

async function search() {
  if (!keyword.value) { elderlyList.value = []; return }
  const { data } = await api.getElderly(keyword.value)
  if (data.success) elderlyList.value = data.data
}

async function showCareRegister(elderly) {
  currentElderly.value = elderly
  careForm.projectCode = ''; careForm.remark = ''
  if (elderly.nursingLevelCode) {
    const { data } = await api.getApplicableProjects(elderly.nursingLevelCode)
    if (data.success) applicableProjects.value = data.data
  }
  careVisible.value = true
}

async function doCareRegister() {
  const { data } = await api.createRecord({
    elderlyId: currentElderly.value.id,
    projectCode: careForm.projectCode,
    nurseName: props.nurseName,
    remark: careForm.remark
  })
  data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  careVisible.value = false
}

function showOutReg(elderly) {
  currentElderly.value = elderly
  outForm.outTime = new Date().toISOString().replace('T', ' ').slice(0, 16)
  outForm.expectedReturnTime = ''; outForm.companion = ''; outForm.reason = ''
  outVisible.value = true
}

async function doOutReg() {
  const { data } = await api.addOutReg({
    customerId: currentElderly.value.id,
    outTime: outForm.outTime,
    expectedReturnTime: outForm.expectedReturnTime,
    companion: outForm.companion,
    reason: outForm.reason
  })
  data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  outVisible.value = false
}

function showHealth(elderly) {
  currentElderly.value = elderly
  healthForm.recordDate = new Date().toISOString().slice(0, 10)
  healthForm.bloodPressure = ''; healthForm.heartRate = ''; healthForm.bloodSugar = ''
  healthForm.weight = ''; healthForm.temperature = ''; healthForm.remark = ''
  healthVisible.value = true
}

async function doHealth() {
  const { data } = await api.addHealthRecord({
    customerId: currentElderly.value.id,
    ...healthForm
  })
  data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  healthVisible.value = false
}
</script>
