<template>
  <div>
    <!-- 床位示意图 -->
    <el-card style="margin-bottom:15px">
      <template #header><b>床位示意图</b></template>
      <div v-for="room in diagram" :key="room.roomNo" style="margin-bottom:12px;padding:10px;background:#fff;border:1px solid #e4e7ed;border-radius:4px">
        <div style="font-weight:bold;margin-bottom:6px">
          [{{ room.buildingName }} {{ room.floor }}F {{ room.roomNo }} ({{ room.roomType }} ¥{{ room.price }}/月)]
        </div>
        <div style="display:flex;gap:15px;flex-wrap:wrap">
          <el-tag v-for="bed in room.beds" :key="bed.bedId"
            :type="bed.status === '空闲' ? 'success' : bed.status === '占用' ? 'danger' : 'warning'"
            style="font-size:14px;padding:8px 14px">
            {{ bed.status === '空闲' ? '○' : bed.status === '占用' ? '●' : '×' }}
            床位{{ bed.bedNo }} [{{ bed.status }}] {{ bed.occupantName }}
          </el-tag>
          <span v-if="!room.beds || room.beds.length === 0" style="color:#999">(无床位)</span>
        </div>
      </div>
    </el-card>

    <!-- 床位管理 -->
    <el-card>
      <template #header>
        <b>床位管理</b>
        <el-button type="success" size="small" style="margin-left:15px" @click="showBedDialog()">添加床位</el-button>
      </template>
      <el-table :data="allBeds" border stripe>
        <el-table-column prop="bedId" label="床位ID" width="80" />
        <el-table-column prop="bedNo" label="床位号" width="100" />
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column label="操作" width="250">
          <template #default="{row}">
            <el-button type="primary" size="small" @click="showBedDialog(row)">修改</el-button>
            <el-button type="warning" size="small" @click="swapVisible = true; swapForm.elderlyIdA = row.bedId">调换床位</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/修改床位 -->
    <el-dialog :title="bedDialogTitle" v-model="bedDialog" width="400px">
      <el-form :model="bedForm" label-width="80px">
        <el-form-item label="床位号"><el-input v-model="bedForm.bedNo" /></el-form-item>
        <el-form-item label="房间ID"><el-input v-model="bedForm.roomId" :disabled="isBedEdit" /></el-form-item>
        <el-form-item v-if="isBedEdit" label="状态">
          <el-select v-model="bedForm.status">
            <el-option label="空闲" value="空闲" />
            <el-option label="占用" value="占用" />
            <el-option label="维修" value="维修" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bedDialog = false">取消</el-button>
        <el-button type="primary" @click="saveBed">确定</el-button>
      </template>
    </el-dialog>

    <!-- 调换床位 -->
    <el-dialog title="调换床位" v-model="swapVisible" width="500px">
      <el-form label-width="100px">
        <el-form-item label="老人A编号"><el-input v-model="swapForm.elderlyIdA" /></el-form-item>
        <el-form-item label="老人B编号"><el-input v-model="swapForm.elderlyIdB" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="swapVisible = false">取消</el-button>
        <el-button type="primary" @click="doSwap">确认调换</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const diagram = ref([])
const allBeds = ref([])
const bedDialog = ref(false)
const bedDialogTitle = ref('添加床位')
const isBedEdit = ref(false)
const swapVisible = ref(false)

const bedForm = reactive({ bedId: '', bedNo: '', roomId: '', status: '空闲' })
const swapForm = reactive({ elderlyIdA: '', elderlyIdB: '' })

onMounted(loadData)

async function loadData() {
  const [d, r] = await Promise.all([api.getBedDiagram(), api.getRooms()])
  if (d.data.success) diagram.value = d.data.data

  const beds = []
  for (const room of diagram.value) {
    for (const bed of (room.beds || [])) {
      beds.push(bed)
    }
  }
  allBeds.value = beds
}

function showBedDialog(bed) {
  if (bed) {
    isBedEdit.value = true
    bedDialogTitle.value = '修改床位'
    Object.assign(bedForm, { bedId: bed.bedId, bedNo: bed.bedNo, roomId: '', status: bed.status })
  } else {
    isBedEdit.value = false
    bedDialogTitle.value = '添加床位'
    Object.assign(bedForm, { bedId: '', bedNo: '', roomId: '', status: '空闲' })
  }
  bedDialog.value = true
}

async function saveBed() {
  if (isBedEdit.value) {
    const { data } = await api.updateBed(bedForm.bedId, { bedNo: bedForm.bedNo, status: bedForm.status })
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  } else {
    const { data } = await api.addBed({ bedNo: bedForm.bedNo, roomId: bedForm.roomId })
    data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  }
  bedDialog.value = false
  loadData()
}

async function doSwap() {
  const { data } = await api.swapBed(swapForm)
  data.success ? ElMessage.success(data.message) : ElMessage.error(data.message)
  swapVisible.value = false
  loadData()
}
</script>
