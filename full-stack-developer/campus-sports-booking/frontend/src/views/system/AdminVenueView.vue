<template>
  <div class="admin-venue">
    <el-page-header @back="() => router.push('/dashboard')" title="工作台">
      <template #content>
        <span class="page-title">场地管理</span>
      </template>
    </el-page-header>
    
    <el-card class="action-card">
      <el-button type="primary" @click="showDialog('create')">
        <el-icon><Plus /></el-icon>
        新增场地
      </el-button>
    </el-card>
    
    <el-card v-loading="loading">
      <el-table :data="venues" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="场地名称" />
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="capacity" label="容量" />
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
    
    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="场地名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入场地名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">维护中</el-radio>
            <el-radio :label="2">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { venueApi, type Venue, type VenueCategory } from '@/api/venue'

const router = useRouter()

const loading = ref(false)
const venues = ref<Venue[]>([])
const categories = ref<VenueCategory[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const dialogType = ref<'create' | 'edit'>('create')
const formRef = ref()

const form = ref<Venue>({
  categoryId: 0,
  name: '',
  location: '',
  capacity: 10,
  description: '',
  imageUrl: '',
  status: 1
})

const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入场地名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }]
}

const dialogTitle = computed(() => dialogType.value === 'create' ? '新增场地' : '编辑场地')

const getStatusType = (status: number) => {
  const types = ['warning', 'success', 'info']
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts = ['维护中', '正常', '停用']
  return texts[status] || '未知'
}

const getCategoryName = (categoryId: number) => {
  const cat = categories.value.find(c => c.id === categoryId)
  return cat?.name || '未知'
}

const showDialog = (type: 'create' | 'edit', row?: Venue) => {
  dialogType.value = type
  if (type === 'edit' && row) {
    form.value = { ...row, imageUrl: row.imageUrl || '' }
  } else {
    form.value = { categoryId: 0, name: '', location: '', capacity: 10, description: '', imageUrl: '', status: 1 }
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (dialogType.value === 'create') {
        await venueApi.createVenue(form.value)
        ElMessage.success('创建成功')
      } else {
        await venueApi.updateVenue(form.value)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      await fetchData()
    } catch (error) {
      // 错误已处理
    } finally {
      submitting.value = false
    }
  })
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该场地吗？', '提示', { type: 'warning' })
    await venueApi.deleteVenue(id)
    ElMessage.success('删除成功')
    await fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const [venuesData, catsData] = await Promise.all([
      venueApi.getVenues(),
      venueApi.getCategories()
    ])
    venues.value = venuesData.map((v: any) => ({
      ...v,
      categoryName: getCategoryName.call({ categories: catsData }, v.categoryId)
    }))
    categories.value = catsData
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.page-title {
  font-size: 18px;
  font-weight: 500;
}

.action-card {
  margin-top: 20px;
}
</style>
