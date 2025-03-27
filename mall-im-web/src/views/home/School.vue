<template>
  <div class="school-container">
    <el-row :gutter="20">
      <el-col
          v-for="school in schools"
          :key="school.id"
          :xs="24"
          :sm="12"
          :md="8"
          class="school-item"
          @click="goToCommunity(school.schoolName)"
      >
        <div class="school-card">
          <el-image
              :src="school.picUrl"
              fit="cover"
              class="school-avatar"
          />
          <span class="school-name">{{ school.schoolName }}</span>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {index} from "@/api/mallSchool.ts";

const router = useRouter()

const schools = ref([
  {id: 1, schoolName: '松江校区', picUrl: 'https://example.com/songjiang.png'},
])


onMounted(() => {
  index().then(res => {
    schools.value = res.data.map(item => ({
      id: item.id,
      schoolName: item.schoolName,
      picUrl: import.meta.env.VITE_API_BASEURL + item.picUrl
    }))
  })
})

const goToCommunity = (schoolName: number) => {
  router.push({name: 'community', query: {schoolName: schoolName}})
}
</script>

<style scoped>
.school-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.school-item {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.school-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(255, 106, 0, 0.15);
}

.school-card {
  position: relative;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  border-radius: 12px;
  background: white;
  border: 2px solid #eee;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.school-avatar {
  width: 64px;
  height: 64px;
  margin-bottom: 12px;
}

.school-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}
</style>
