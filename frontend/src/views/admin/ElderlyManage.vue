<template>
  <div>
    <el-card>
      <template #header><b>老人管理</b></template>
      <!-- 搜索 -->
      <el-row :gutter="10" style="margin-bottom:15px">
        <el-col :span="8"><el-input v-model="keyword" placeholder="输入编号或姓名搜索" @keyup.enter="search" /></el-col>
        <el-col :span="4"><el-button type="primary" @click="search">查询</el-button><el-button @click="keyword='';search()">全部</el-button></el-col>
        <el-col :span="12" style="text-align:right"><el-button type="success" @click="checkinVisible = true">入住登记</el-button></el-col>
      </el-row>
      <!-- 列表 -->
      <el-table :data="elderlyList" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="nursingLevelCode" label="护理等级" width="100" />
        <el-table-column prop="roomNo" label="房间号" width="80" />
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column label="操作" min-width="140">
          <template #default="{row}">
            <el-button type="primary" size="small" @click="showSetLevel(row)">设置护理等级</el-button>
            <el-button v-if="row.status !== '退住'" type="danger" size="small" @click="showCheckout(row)">办理退住</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 入住登记对话框 -->
    <el-dialog title="老人入住登记" v-model="checkinVisible" width="600px">
      <el-form :model="checkinForm" label-width="110px">
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="姓名"><el-input v-model="checkinForm.name" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="年龄"><el-input-number v-model="checkinForm.age" :min="0" :max="150" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="性别"><el-select v-model="checkinForm.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="身份证号"><el-input v-model="checkinForm.idCard" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="电话"><el-input v-model="checkinForm.phone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="入住日期"><el-input v-model="checkinForm.checkinDate" placeholder="yyyy-MM-dd" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="住址"><el-input v-model="checkinForm.address" /></el-form-item>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="紧急联系人"><el-input v-model="checkinForm.emergencyContact" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="紧急联系电话"><el-input v-model="checkinForm.emergencyPhone" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="12">
            <el-form-item label="护理等级">
              <el-select v-model="checkinForm.nursingLevelCode">
                <el-option v-for="l in levels" :key="l.code" :label="l.code + ' - ' + l.name" :value="l.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="房间号"><el-input v-model="checkinForm.roomNo" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="床位">
          <el-select v-model="checkinForm.bedId" clearable placeholder="可选">
            <el-option v-for="b in availableBeds" :key="b.bedId" :label="b.bedNo + ' (' + b.roomNo + ')'" :value="b.bedId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkinVisible = false">取消</el-button>
        <el-button type="primary" @click="doCheckin">确认入住</el-button>
      </template>
    </el-dialog>

    <!-- 设置护理等级对话框 -->
    <el-dialog title="设置护理等级" v-model="levelVisible" width="400px">
      <el-form-item label="老人"> {{ currentElderly?.name }} ({{ currentElderly?.id }}) </el-form-item>
      <el-form-item label="护理等级">
        <el-select v-model="newLevel" placeholder="请选择">
          <el-option v-for="l in levels" :key="l.code" :label="l.code + ' - ' + l.name" :value="l.code" />
        </el-select>
      </el-form-item>
      <template #footer>
        <el-button @click="levelVisible = false">取消</el-button>
        <el-button type="primary" @click="doSetLevel">确定</el-button>
      </template>
    </el-dialog>

    <!-- 办理退住对话框 -->
    <el-dialog title="办理退住" v-model="checkoutVisible" width="400px">
      <el-form-item label="老人"> {{ currentElderly?.name }} ({{ currentElderly?.id }}) </el-form-item>
      <el-form-item label="退住日期">
        <el-input v-model="checkoutForm.checkoutDate" placeholder="yyyy-MM-dd" />
      </el-form-item>
      <el-form-item label="原因">
        <el-input v-model="checkoutForm.reason" type="textarea" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="checkoutForm.remark" />
      </el-form-item>
      <template #footer>
        <el-button @click="checkoutVisible = false">取消</el-button>
        <el-button type="danger" @click="doCheckout">确认退住</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const keyword = ref('')
const elderlyList = ref([])
const levels = ref([])
const availableBeds = ref([])
const checkinVisible = ref(false)
const levelVisible = ref(false)
const checkoutVisible = ref(false)
const currentElderly = ref(null)
const newLevel = ref('')
const checkoutForm = reactive({ checkoutDate: '', reason: '', remark: '' })

const checkinForm = reactive({
  name: '', age: 75, gender: '男', idCard: '', phone: '', address: '',
  emergencyContact: '', emergencyPhone: '', checkinDate: new Date().toISOString().slice(0, 10),
  nursingLevelCode: 'ZL', roomNo: '', bedId: ''
})

onMounted(async () => {
  await search()
  const { data: ld } = await api.getLevels()
  if (ld.success) levels.value = ld.data
  const { data: bd } = await api.getAvailableBeds()
  if (bd.success) availableBeds.value = bd.data
})

async function search() {
  const { data } = await api.getElderly(keyword.value)
  if (data.success) elderlyList.value = data.data
}

async function doCheckin() {
  const { data } = await api.checkin(checkinForm)
  if (data.success) {
    ElMessage.success(data.message)
    checkinVisible.value = false
    search()
    const { data: bd } = await api.getAvailableBeds()
    if (bd.success) availableBeds.value = bd.data
  } else {
    ElMessage.error(data.message)
  }
}

function showSetLevel(elderly) {
  currentElderly.value = elderly
  newLevel.value = elderly.nursingLevelCode
  levelVisible.value = true
}

async function doSetLevel() {
  const { data } = await api.setNursingLevel(currentElderly.value.id, { nursingLevelCode: newLevel.value })
  if (data.success) { ElMessage.success(data.message); levelVisible.value = false; search() }
  else ElMessage.error(data.message)
}

function showCheckout(elderly) {
  currentElderly.value = elderly
  checkoutForm.checkoutDate = new Date().toISOString().slice(0, 10)
  checkoutForm.reason = ''
  checkoutForm.remark = ''
  checkoutVisible.value = true
}

async function doCheckout() {
  const { data } = await api.createCheckout({
    customerId: currentElderly.value.id,
    checkoutDate: checkoutForm.checkoutDate,
    reason: checkoutForm.reason,
    remark: checkoutForm.remark
  })
  if (data.success) {
    ElMessage.success(data.message)
    checkoutVisible.value = false
    search()
  } else {
    ElMessage.error(data.message)
  }
}
</script>
