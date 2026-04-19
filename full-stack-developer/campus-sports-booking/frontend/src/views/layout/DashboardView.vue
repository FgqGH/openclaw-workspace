<template>
  <div class="dashboard">
    <el-page-header @back="() => router.push('/')" title="工作台">
      <template #content>
        <span class="page-title">欢迎使用校园运动场地预约系统</span>
      </template>
    </el-page-header>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" color="#409eff"><Calendar /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.myReservations }}</div>
              <div class="stat-label">我的预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6" v-if="authStore.isAdmin()">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" color="#67c23a"><Location /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.allReservations }}</div>
              <div class="stat-label">全部预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" color="#e6a23c"><Tickets /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.venues }}</div>
              <div class="stat-label">开放场地</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" color="#f56c6c"><Bell /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingCount }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="quick-actions">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="action-buttons">
            <el-button type="primary" @click="router.push('/venue')">
              <el-icon><Search /></el-icon>
              浏览场地
            </el-button>
            <el-button type="success" @click="router.push('/reservation/my')">
              <el-icon><Calendar /></el-icon>
              我的预约
            </el-button>
            <el-button v-if="authStore.isAdmin()" type="warning" @click="router.push('/admin/timeslot')">
              <el-icon><Clock /></el-icon>
              管理时段
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="recent-reservations">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>近期预约</span>
          </template>
          <el-table :data="recentReservations" stripe>
            <el-table-column prop="venueName" label="场地" />
            <el-table-column prop="slotDate" label="日期" />
            <el-table-column label="时段">
              <template #default="{ row }">
                {{ row.startTime }} - {{ row.endTime }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, Location, Tickets, Bell, Search, Clock } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useVenueStore } from '@/stores/venue'
import { useReservationStore } from '@/stores/reservation'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const venueStore = useVenueStore()
const reservationStore = useReservationStore()

const stats = ref({
  myReservations: 0,
  allReservations: 0,
  venues: 0,
  pendingCount: 0
})

const recentReservations = ref([])

const getStatusType = (status: number) => {
  const types = ['info', 'success', 'warning', 'danger']
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts = ['已取消', '已预约', '已完成', '违约']
  return texts[status] || '未知'
}

onMounted(async () => {
  try {
    await venueStore.fetchVenues()
    await reservationStore.fetchMyReservations()
    
    stats.value.myReservations = reservationStore.myReservations.length
    stats.value.venues = venueStore.venues.length
    
    // 筛选近期已预约
    const now = new Date()
    recentReservations.value = reservationStore.myReservations
      .filter(r => r.status === 1)
      .map(r => {
        const venue = venueStore.venues.find(v => v.id === r.venueId)
        return { ...r, venueName: venue?.name || '未知场地' }
      })
      .slice(0, 5)
    
    if (authStore.isAdmin()) {
      await reservationStore.fetchAllReservations()
      stats.value.allReservations = reservationStore.allReservations.length
      stats.value.pendingCount = reservationStore.allReservations.filter(r => r.status === 1).length
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
}

.page-title {
  font-size: 18px;
  font-weight: 500;
}

.stats-row {
  margin-top: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 40px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.quick-actions {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.recent-reservations {
  margin-top: 20px;
}
</style>
