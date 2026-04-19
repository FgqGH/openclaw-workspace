<template>
  <div class="venue-detail" v-loading="loading">
    <el-page-header @back="() => router.push('/venue')" title="返回场地列表">
      <template #content>
        <span class="page-title">{{ venue?.name || '场地详情' }}</span>
      </template>
    </el-page-header>
    
    <el-row :gutter="20" class="detail-content" v-if="venue">
      <!-- 场地信息 -->
      <el-col :span="8">
        <el-card>
          <div class="venue-image">
            <el-image 
              v-if="venue.imageUrl" 
              :src="venue.imageUrl" 
              fit="cover"
              class="detail-img"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon :size="60"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="image-placeholder">
              <el-icon :size="60"><Picture /></el-icon>
            </div>
          </div>
          
          <div class="info-list">
            <div class="info-item">
              <span class="label">场地名称</span>
              <span class="value">{{ venue.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">所属分类</span>
              <span class="value">{{ categoryName }}</span>
            </div>
            <div class="info-item">
              <span class="label">位置</span>
              <span class="value">{{ venue.location }}</span>
            </div>
            <div class="info-item">
              <span class="label">容纳人数</span>
              <span class="value">{{ venue.capacity }} 人</span>
            </div>
            <div class="info-item">
              <span class="label">场地描述</span>
              <span class="value">{{ venue.description || '暂无描述' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 时段选择 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>预约时段</span>
          </template>
          
          <!-- 日期选择 -->
          <div class="date-picker">
            <el-date-picker
              v-model="selectedDate"
              type="date"
              placeholder="选择日期"
              :disabled-date="disabledDate"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="fetchSlots"
            />
          </div>
          
          <!-- 时段列表 -->
          <div class="time-slots" v-if="selectedDate">
            <el-empty v-if="timeSlots.length === 0" description="当日暂无可预约时段" />
            
            <el-row :gutter="12" v-else>
              <el-col 
                v-for="slot in timeSlots" 
                :key="slot.id" 
                :span="8"
              >
                <div 
                  class="time-slot"
                  :class="{
                    'available': slot.status === 1 && slot.bookedCount < slot.maxBookings,
                    'full': slot.bookedCount >= slot.maxBookings || slot.status !== 1
                  }"
                  @click="handleSlotClick(slot)"
                >
                  <div class="slot-time">
                    {{ slot.startTime }} - {{ slot.endTime }}
                  </div>
                  <div class="slot-status">
                    <template v-if="slot.bookedCount >= slot.maxBookings || slot.status !== 1">
                      已约满
                    </template>
                    <template v-else>
                      可预约 ({{ slot.bookedCount }}/{{ slot.maxBookings }})
                    </template>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          
          <el-empty v-else description="请先选择日期" />
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 预约确认对话框 -->
    <el-dialog v-model="showConfirmDialog" title="确认预约" width="400px">
      <div class="confirm-info" v-if="selectedSlot">
        <p><strong>场地：</strong>{{ venue?.name }}</p>
        <p><strong>日期：</strong>{{ selectedDate }}</p>
        <p><strong>时段：</strong>{{ selectedSlot.startTime }} - {{ selectedSlot.endTime }}</p>
      </div>
      <template #footer>
        <el-button @click="showConfirmDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmReservation">
          确认预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { useVenueStore } from '@/stores/venue'
import { useReservationStore } from '@/stores/reservation'
import type { TimeSlot } from '@/api/venue'

const route = useRoute()
const router = useRouter()
const venueStore = useVenueStore()
const reservationStore = useReservationStore()

const loading = ref(false)
const venue = ref<any>(null)
const selectedDate = ref('')
const timeSlots = ref<TimeSlot[]>([])
const selectedSlot = ref<TimeSlot | null>(null)
const showConfirmDialog = ref(false)
const submitting = ref(false)

const categoryName = computed(() => {
  if (!venue.value) return ''
  const cat = venueStore.categories.find(c => c.id === venue.value.categoryId)
  return cat?.name || '未知'
})

const disabledDate = (date: Date) => {
  // 禁止选择过去日期
  const now = new Date()
  now.setHours(0, 0, 0, 0)
  return date < now
}

const fetchSlots = async () => {
  if (!selectedDate.value || !venue.value) return
  
  try {
    const slots = await venueStore.fetchTimeSlots(venue.value.id, selectedDate.value)
    timeSlots.value = slots
  } catch (error) {
    ElMessage.error('加载时段失败')
  }
}

const handleSlotClick = (slot: TimeSlot) => {
  if (slot.bookedCount >= slot.maxBookings || slot.status !== 1) {
    ElMessage.warning('该时段已约满')
    return
  }
  
  selectedSlot.value = slot
  showConfirmDialog.value = true
}

const confirmReservation = async () => {
  if (!selectedSlot.value) return
  
  submitting.value = true
  try {
    await reservationStore.createReservation(selectedSlot.value.id)
    ElMessage.success('预约成功')
    showConfirmDialog.value = false
    await fetchSlots() // 刷新时段
  } catch (error) {
    // 错误已在拦截器处理
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const venueId = Number(route.params.id)
    venue.value = await venueStore.fetchVenueById(venueId)
    
    // 默认选择今天
    const today = new Date()
    selectedDate.value = today.toISOString().split('T')[0]
    await fetchSlots()
  } catch (error) {
    ElMessage.error('加载场地失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-title {
  font-size: 18px;
  font-weight: 500;
}

.detail-content {
  margin-top: 20px;
}

.venue-image {
  width: 100%;
  height: 200px;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 16px;
}

.detail-img {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  gap: 12px;
}

.info-item .label {
  color: #999;
  min-width: 70px;
}

.info-item .value {
  color: #333;
}

.date-picker {
  margin-bottom: 20px;
}

.time-slot {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.time-slot.available:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.time-slot.full {
  background: #f5f7fa;
  color: #c0c4cc;
  cursor: not-allowed;
}

.slot-time {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.slot-status {
  font-size: 12px;
  color: #666;
}

.time-slot.full .slot-status {
  color: #c0c4cc;
}

.confirm-info p {
  margin: 8px 0;
}
</style>
