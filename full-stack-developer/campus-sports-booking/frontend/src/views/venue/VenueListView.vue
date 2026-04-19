<template>
  <div class="venue-list">
    <el-page-header @back="() => router.push('/dashboard')" title="场地预约">
      <template #content>
        <span class="page-title">场地浏览</span>
      </template>
    </el-page-header>
    
    <!-- 分类筛选 -->
    <el-card class="category-filter" shadow="never">
      <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
        <el-radio-button :value="0">全部</el-radio-button>
        <el-radio-button 
          v-for="cat in venueStore.categories" 
          :key="cat.id" 
          :value="cat.id"
        >
          {{ cat.name }}
        </el-radio-button>
      </el-radio-group>
    </el-card>
    
    <!-- 场地列表 -->
    <el-row :gutter="20" class="venue-grid" v-loading="venueStore.loading">
      <el-col 
        v-for="venue in filteredVenues" 
        :key="venue.id" 
        :xs="24" :sm="12" :md="8" :lg="6"
      >
        <el-card 
          class="venue-card" 
          shadow="hover" 
          @click="router.push(`/venue/${venue.id}`)"
        >
          <div class="venue-image">
            <el-image 
              v-if="venue.imageUrl" 
              :src="venue.imageUrl" 
              fit="cover"
              class="venue-img"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon :size="40"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="image-placeholder">
              <el-icon :size="40"><Picture /></el-icon>
            </div>
            <el-tag class="category-tag" type="primary">
              {{ getCategoryName(venue.categoryId) }}
            </el-tag>
          </div>
          
          <div class="venue-info">
            <h3 class="venue-name">{{ venue.name }}</h3>
            <p class="venue-location">
              <el-icon><Location /></el-icon>
              {{ venue.location }}
            </p>
            <p class="venue-capacity">
              <el-icon><User /></el-icon>
              可容纳 {{ venue.capacity }} 人
            </p>
          </div>
        </el-card>
      </el-col>
      
      <el-col v-if="filteredVenues.length === 0" :span="24">
        <el-empty description="暂无场地" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Picture, Location, User } from '@element-plus/icons-vue'
import { useVenueStore } from '@/stores/venue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const venueStore = useVenueStore()

const selectedCategory = ref(0)

const filteredVenues = computed(() => {
  if (selectedCategory.value === 0) {
    return venueStore.venues
  }
  return venueStore.venues.filter(v => v.categoryId === selectedCategory.value)
})

const getCategoryName = (categoryId: number) => {
  const cat = venueStore.categories.find(c => c.id === categoryId)
  return cat?.name || '未知'
}

const handleCategoryChange = () => {
  // 筛选逻辑由 computed 处理
}

onMounted(async () => {
  try {
    await Promise.all([
      venueStore.fetchCategories(),
      venueStore.fetchVenues()
    ])
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

.category-filter {
  margin-top: 20px;
}

.venue-grid {
  margin-top: 20px;
}

.venue-card {
  cursor: pointer;
  transition: transform 0.2s;
  margin-bottom: 20px;
}

.venue-card:hover {
  transform: translateY(-4px);
}

.venue-image {
  position: relative;
  height: 160px;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}

.venue-img {
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
  background: #f5f7fa;
}

.category-tag {
  position: absolute;
  top: 8px;
  right: 8px;
}

.venue-info {
  padding: 12px 0;
}

.venue-name {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.venue-location,
.venue-capacity {
  margin: 4px 0;
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
