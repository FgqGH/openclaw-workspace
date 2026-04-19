<template>
  <div class="my-reservations">
    <el-page-header @back="() => router.push('/dashboard')" title="工作台">
      <template #content>
        <span class="page-title">我的预约</span>
      </template>
    </el-page-header>
    
    <el-card class="filter-card" shadow="never">
      <el-radio-group v-model="statusFilter">
        <el-radio-button :value="-1">全部</el-radio-button>
        <el-radio-button :value="1">已预约</el-radio-button>
        <el-radio-button :value="2">已完成</el-radio-button>
        <el-radio-button :value="0">已取消</el-radio-button>
      </el-radio-group>
    </el-card>
    
    <el-card class="table-card" v-loading="reservationStore.loading">
      <el-table :data="filteredReservations" stripe>
        <el-table-column prop="venueName" label="场地" />
        <el-table-column prop="slotDate" label="日期" />
        <el-table-column label="时段">
          <template #default="{ row }">
            {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="预约时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 1" 
              type="danger" 
              size="small"
              @click="handleCancel(row)"
            >
              取消预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="filteredReservations.length === 0" description="暂无预约记录" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useReservationStore } from '@/stores/reservation'
import { useVenueStore } from '@/stores/venue'
import type { Reservation } from '@/api/reservation'

const router = useRouter()
const reservationStore = useReservationStore()
const venueStore = useVenueStore()

const statusFilter = ref(-1)

const filteredReservations = computed(() => {
  let list = reservationStore.myReservations
  
  // 补充场地信息
  list = list.map(r => {
    const venue = venueStore.venues.find(v => v.id === r.venueId)
    return { ...r, venueName: venue?.name || '未知场地' }
  })
  
  if (statusFilter.value === -1) return list
  return list.filter(r => r.status === statusFilter.value)
})

const getStatusType = (status: number) => {
  const types = ['info', 'success', 'warning', 'danger']
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts = ['已取消', '已预约', '已完成', '违约']
  return texts[status] || '未知'
}

const handleCancel = async (row: Reservation) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消该预约吗？取消后场地将释放可供他人预约。',
      '取消预约',
      { type: 'warning' }
    )
    
    await reservationStore.cancelReservation(row.id)
    ElMessage.success('取消成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

onMounted(async () => {
  try {
    await Promise.all([
      reservationStore.fetchMyReservations(),
      venueStore.fetchVenues()
    ])
  } catch (error) {
    ElMessage.error('加载失败')
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

.table-card {
  margin-top: 16px;
}
</style>
