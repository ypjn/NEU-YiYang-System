<template>
  <div>
    <el-card>
      <template #header>
        <b>管家服务管理</b>
        <el-button type="success" size="small" style="margin-left:15px" @click="showCreate">服务购买</el-button>
      </template>
      <el-table :data="services" border stripe>
        <el-table-column prop="assignmentId" label="ID" width="80" />
        <el-table-column prop="employeeId" label="管家ID" width="80" />
        <el-table-column prop="elderlyId" label="老人ID" width="80" />
        <el-table-column prop="serviceType" label="服务类型" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="fee" label="费用" width="100" />
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column label="操作" min-width="350">
          <template #default="{row}">
            <el-button type="info" size="small" @click="showFollowup(row)">服务关注</el-button>
            <el-button type="warning" size="small" @click="showRenew(row)">服务续费</el-button>
            <el-button v-if="row.status !== '已到期' && row.status !== '已撤销'" type="danger" size="small" @click="showRevoke(row)">撤销服务</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 服务购买 -->
    <el-dialog title="服务购买" v-model="createVisible" width="600px">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="管家">
          <el-select v-model="createForm.employeeId">
            <el-option v-for="e in employees" :key="e.employeeId" :label="e.name + ' (' + e.position + ')'" :value="e.employeeId" />
          </el-select>
        </el-form-item>
        <el-form-item label="老人编号"><el-input v-model="createForm.elderlyId" /></el-form-item>
        <el-form-item label="服务类型"><el-input v-model="createForm.serviceType" /></el-form-item>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="开始日期"><el-input v-model="createForm.startDate" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束日期"><el-input v-model="createForm.endDate" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="费用"><el-input-number v-model="createForm.fee" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="createForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="doCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 服务关注 -->
    <el-dialog title="服务关注" v-model="followupVisible" width="400px">
      <el-form-item label="添加备注"><el-input v-model="followupNote" type="textarea" /></el-form-item>
      <template #footer>
        <el-button @click="followupVisible = false">取消</el-button>
        <el-button type="primary" @click="doFollowup">确定</el-button>
      </template>
    </el-dialog>

    <!-- 服务续费 -->
    <el-dialog title="服务续费" v-model="renewVisible" width="400px">
      <el-form-item label="续费金额"><el-input-number v-model="renewFee" :min="0" /></el-form-item>
      <el-form-item label="延长至日期"><el-input v-model="renewEndDate" /></el-form-item>
      <template #footer>
        <el-button @click="renewVisible = false">取消</el-button>
        <el-button type="primary" @click="doRenew">确定</el-button>
      </template>
    </el-dialog>

    <!-- 撤销服务确认 -->
    <el-dialog title="撤销服务" v-model="revokeVisible" width="400px">
      <p>确定要撤销该服务吗？撤销后不可恢复。</p>
      <template #footer>
        <el-button @click="revokeVisible = false">取消</el-button>
        <el-button type="danger" @click="doRevoke">确认撤销</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const services = ref([])
const employees = ref([])
const createVisible = ref(false)
const followupVisible = ref(false)
const renewVisible = ref(false)
const revokeVisible = ref(false)
const currentServiceId = ref('')
const followupNote = ref('')
const renewFee = ref(0)
const renewEndDate = ref('')

const createForm = reactive({
  employeeId: '', elderlyId: '', serviceType: '', startDate: '', endDate: '', fee: 0, remark: ''
})

onMounted(async () => {
  const [s, e] = await Promise.all([api.getServices(''), api.getEmployees({})])
  if (s.data.success) services.value = s.data.data
  if (e.data.success) employees.value = e.data.data
})

function showCreate() { createVisible.value = true }

async function doCreate() {
  const { data } = await api.createService(createForm)
  data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  createVisible.value = false
  const { data: s } = await api.getServices('')
  if (s.success) services.value = s.data
}

function showFollowup(sa) { currentServiceId.value = sa.assignmentId; followupNote.value = ''; followupVisible.value = true }

async function doFollowup() {
  const { data } = await api.followupService(currentServiceId.value, { note: followupNote.value })
  data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  followupVisible.value = false
  const { data: s } = await api.getServices('')
  if (s.success) services.value = s.data
}

function showRenew(sa) { currentServiceId.value = sa.assignmentId; renewFee.value = 0; renewEndDate.value = ''; renewVisible.value = true }

async function doRenew() {
  const { data } = await api.renewService(currentServiceId.value, { fee: String(renewFee.value), endDate: renewEndDate.value })
  data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  renewVisible.value = false
  const { data: s } = await api.getServices('')
  if (s.success) services.value = s.data
}

function showRevoke(sa) { currentServiceId.value = sa.assignmentId; revokeVisible.value = true }

async function doRevoke() {
  const { data } = await api.revokeService(currentServiceId.value)
  data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  revokeVisible.value = false
  const { data: s } = await api.getServices('')
  if (s.success) services.value = s.data
}
</script>
