<template>
  <div class="admin-category">
    <el-page-header @back="() => router.push('/dashboard')" title="工作台">
      <template #content>
        <span class="page-title">场地分类管理</span>
      </template>
    </el-page-header>
    
    <el-card class="action-card">
      <el-button type="primary" @click="showDialog('create')">
        <el-icon><Plus /></el-icon>
        新增分类
      </el-button>
    </el-card>
    
    <el-card v-loading="loading" class="table-card">
      <el-table :data="categories" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
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
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标类名" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
import { venueApi, type VenueCategory } from '@/api/venue'

const router = useRouter()

const loading = ref(false)
const categories = ref<VenueCategory[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const dialogType = ref<'create' | 'edit'>('create')
const formRef = ref()

const form = ref<VenueCategory>({
  name: '',
  icon: '',
  sortOrder: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const dialogTitle = computed(() => dialogType.value === 'create' ? '新增分类' : '编辑分类')

const showDialog = (type: 'create' | 'edit', row?: VenueCategory) => {
  dialogType.value = type
  if (type === 'edit' && row) {
    form.value = { ...row, icon: row.icon || '' }
  } else {
    form.value = { name: '', icon: '', sortOrder: 0, status: 1 }
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
        await venueApi.createCategory(form.value)
        ElMessage.success('创建成功')
      } else {
        await venueApi.updateCategory(form.value)
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
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', { type: 'warning' })
    await venueApi.deleteCategory(id)
    ElMessage.success('删除成功')
    await fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const data = await venueApi.getCategories()
    categories.value = data
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

.table-card {
  margin-top: 16px;
}
</style>