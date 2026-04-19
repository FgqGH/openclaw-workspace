<template>
  <div class="admin-timeslot">
    <el-page-header @back="() => router.push('/dashboard')" title="工作台">
      <template #content>
        <span class="page-title">时段管理</span>
      </template>
    </el-page-header>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="场地">
          <el-select v-model="filterForm.venueId" placeholder="选择场地" clearable>
            <el-option v-for="v in venues" :key="v.id" :label="v.name" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="filterForm.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="showBatchDialog">批量添加时段</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card v-loading="loading">
      <el-table :data="timeSlots" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="venueName" label="场地" />
        <el-table-column prop="slotDate" label="日期" />
        <el-table-column label="时段">
          <template #default="{ row }">
            {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="预约情况">
          <template #default="{ row }">
            {{ row.bookedCount }} / {{ row.maxBookings }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showDialog('edit', row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 批量添加对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量添加时段" width="600px">
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="100px">
        <el-form-item label="场地" prop="venueId">
          <el-select v-model="batchForm.venueId" placeholder="选择场地">
            <el-option v-for="v in venues" :key="v.id" :label="v.name" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="dates">
          <el-date-picker
            v-model="batchForm.dates"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="时段模板" prop="template">
          <el-checkbox-group v-model="batchForm.template">
            <el-checkbox label="08:00:00-09:00:00">08:00-09:00</el-checkbox>
            <el-checkbox label="09:00:00-10:00:00">09:00-10:00</el-checkbox>
            <el-checkbox label="10:00:00-11:00:00">10:00-11:00</el-checkbox>
            <el-checkbox label="11:00:00-12:00:00">11:00-12:00</el-checkbox>
            <el-checkbox label="14:00:00-15:00:00">14:00-15:00</el-checkbox>
            <el-checkbox label="15:00:00-16:00:00">15:00-16:00</el-checkbox>
            <el-checkbox label="16:00:00-17:00:00">16:00-17:00</el-checkbox>
            <el-checkbox label="17:00:00-18:00:00">17:00-18:00</el-checkbox>
            <el-checkbox label="18:00:00-19:00:00">18:00-19:00</el-checkbox>
            <el-checkbox label="19:00:00-20:00:00">19:00-20:00</el-checkbox>
            <el-checkbox label="20:00:00-21:00:00">20:00-21:00</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleBatchCreate">确定添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { venueApi, type Venue, type TimeSlot } from '@/api/venue'

const router = useRouter()

const loading = ref(false)
const venues = ref<Venue[]>([])
const timeSlots = ref<(TimeSlot & { venueName?: string })[]>([])
const batchDialogVisible = ref(false)
const submitting = ref(false)
const batchFormRef = ref()

const filterForm = ref({
  venueId: undefined as number | undefined,
  date: ''
})

const batchForm = ref({
  venueId: undefined as number | undefined,
  dates: [] as string[],
  template: [] as string[]
})

const batchRules = {
  venueId: [{ required: true, message: '请选择场地', trigger: 'change' }],
  dates: [{ required: true, message: '请选择日期范围', trigger: 'change' }],
  template: [{ required: true, message: '请选择时段', trigger: 'change' }]
}

const getStatusType = (status: number) => {
  const types = ['info', 'success', 'warning']
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts = ['不可用', '可预约', '已约满']
  return texts[status] || '未知'
}

const handleSearch = async () => {
  if (!filterForm.value.venueId || !filterForm.value.date) {
    ElMessage.warning('请选择场地和日期')
    return
  }
  
  loading.value = true
  try {
    const data = await venueApi.getTimeSlots(filterForm.value.venueId, filterForm.value.date)
    const venue = venues.value.find(v => v.id === filterForm.value.venueId)
    timeSlots.value = data.map((slot: any) => ({
      ...slot,
      venueName: venue?.name
    }))
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const showBatchDialog = () => {
  batchForm.value = { venueId: undefined, dates: [], template: [] }
  batchDialogVisible.value = true
}

const handleBatchCreate = async () => {
  if (!batchFormRef.value) return
  await batchFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    const [startDate, endDate] = batchForm.value.dates
    const slots: TimeSlot[] = []
    const start = new Date(startDate)
    const end = new Date(endDate)
    
    for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
      const dateStr = d.toISOString().split('T')[0]
      for (const timeStr of batchForm.value.template) {
        const [startTime, endTime] = timeStr.split('-')
        slots.push({
          id: 0,
          venueId: batchForm.value.venueId!,
          slotDate: dateStr,
          startTime,
          endTime,
          maxBookings: 1,
          bookedCount: 0,
          status: 1,
          createdAt: ''
        })
      }
    }
    
    submitting.value = true
    try {
      await venueApi.batchCreateTimeSlots(slots)
      ElMessage.success('批量创建成功')
      batchDialogVisible.value = false
      handleSearch()
    } catch (error) {
      ElMessage.error('创建失败')
    } finally {
      submitting.value = false
    }
  })
}

const showDialog = (type: string, row: TimeSlot) => {
  ElMessage.info('编辑功能开发中')
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该时段吗？', '提示', { type: 'warning' })
    await venueApi.deleteTimeSlot(id)
    ElMessage.success('删除成功')
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(async () => {
  try {
    const data = await venueApi.getVenues()
    venues.value = data
  } catch (error) {
    ElMessage.error('加载场地失败')
  }
})
</script>

<style scoped>
.page-title {
  font-size: 18px;
  font-weight: 500;
}

.filter-card {
  margin-top: 20px;
}
</style>
