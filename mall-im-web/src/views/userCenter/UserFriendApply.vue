<template>
  <div class="friend-apply">
    <el-card class="apply-card">
      <el-table :data="applications" style="width: 100%">
        <el-table-column prop="applicant" label="申请人">
          <template #default="scope">
            <div class="applicant-info">
              <el-avatar :src="scope.row.avatar"/>
              <span class="name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="school" label="所在学校"/>
        <el-table-column prop="message" label="申请备注"/>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button
                type="success"
                size="small"
                @click="handleApprove(scope.row)"
            >同意
            </el-button>
            <el-button
                type="danger"
                size="small"
                @click="handleReject(scope.row)"
            >拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {friendApplyList, optApply} from "@/api/friendRelation.ts";
import {ElMessage} from "element-plus";

interface Application {
  id: number
  name: string
  avatar: string
  school: string
  message: string
}

const applications = ref<Application[]>([])

// 初始化数据
onMounted(() => {
  getApplyList();
})

const getApplyList = () => {
  friendApplyList().then(res => {
    applications.value = res.data.map((item: any) => {
      return {
        id: item.relatedId,
        name: item.userName,
        avatar: import.meta.env.VITE_API_BASEURL + item.avatar,
        school: item.schoolName,
        message: item.applyContent
      }
    })
  })
}


const handleApprove = (app: Application) => {
  //  调用同意API 1
  optApply({"applyId": app.id, "flag": 1}).then(res => {
    if (200 === res.code) {
      ElMessage.success('操作成功')
      getApplyList();
    }
  })
}

const handleReject = (app: Application) => {
  //  调用拒绝API 2
  optApply({"applyId": app.id, "flag": 2}).then(res => {
    if (200 === res.code) {
      ElMessage.success('操作成功')
      getApplyList();
    }
  })
}
</script>

<style scoped>
.friend-apply {
  padding: 0px;
}

.applicant-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.apply-card {
  margin: 20px;
}
</style>
