<template>
  <div class="friend-list">
    <el-row :gutter="20" class="full-height">
      <el-col :span="6">
        <el-card class="contact-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">我的好友</span>
            </div>
          </template>
          <el-menu>
            <el-menu-item
                v-for="friend in friends"
                :key="friend.id"
                @click="selectFriend(friend)"
            >
              <el-avatar :src="friend.avatar" size="32"/>
              <span class="friend-name">{{ friend.name }}</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="18" class="full-height">
        <el-card class="detail-card">
          <div v-if="selectedFriend" class="friend-detail">

            <div class="info-header">
              <div class="school-info">
                <h3>{{ selectedFriend.name }}的学校</h3>
                <el-tag size="large">{{ selectedFriend.school }}</el-tag>
              </div>
              <el-button
                  type="danger"
                  size="common"
                  class="delete-btn"
                  @click="handleDelete(selectedFriend.id)"
              >删除好友
              </el-button>
            </div>

            <ChatComponent
                v-if="selectedFriend"
                :user-id="selectedFriend.id"
                :user-name="selectedFriend.name"
                :avatar="selectedFriend.avatar"
                class="chat-wrapper"
            />

          </div>
          <div v-else class="empty-tip">
            <el-icon>
              <User/>
            </el-icon>
            <p>请选择好友查看详情</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import ChatComponent from '@/components/ChatComponent.vue'
import {friendList, optApply} from "@/api/friendRelation.ts";
import {ElMessage} from "element-plus";

interface Friend {
  id: number
  name: string
  avatar: string
  school: string
}

const selectedFriend = ref<Friend>()
const friends = ref<Friend[]>([])

onMounted(() => {
  getFriendList();
})

const getFriendList = () => {
  // 获取好友列表
  friendList().then(res => {
    friends.value = res.data.map((item: any) => {
      return {
        id: item.friendId,
        name: item.userName,
        avatar: import.meta.env.VITE_API_BASEURL + item.avatar,
        school: item.schoolName
      }
    })
  })
}

const handleDelete = (id: number) => {
  //  调用删除API 3
  optApply({"applyId": id, "flag": 3}).then(res => {
    if (200 === res.code) {
      ElMessage.success('操作成功')
      getFriendList();
    }
  })
}

const selectFriend = (friend: Friend) => {
  selectedFriend.value = friend
}
</script>

<style scoped>
.friend-list {
  height: 700px;
  background: #f5f7fa;
  padding: 12px;
  display: flex;
  flex-direction: column;
}

.full-height {
  flex: 1;
  min-height: 0;
}

.contact-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.el-menu {
  flex: 1;
  overflow-y: auto;
}

.detail-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-wrapper {
  flex: 1;
  min-height: 0;
  background: white;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}


.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
  }

  .unread-badge {
    transform: translateY(-1px);
  }
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.school-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.friend-name {
  margin-left: 10px;
  margin-right: 8px;
}

.school-tag {
  max-width: 80px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
}

.empty-tip {
  text-align: center;
  color: var(--el-text-color-secondary);
  padding: 50px 0;
}
</style>
