<template>
  <div class="message-center">
    <el-row :gutter="20" class="full-height">
      <!-- 左侧联系人 -->
      <el-col :span="6" class="full-height">
        <el-card class="contact-card full-height">
          <template #header>
            <div class="card-header">
              <span class="header-title">消息列表</span>
              <!-- <el-badge :value="unreadCount" class="unread-badge" /> -->
            </div>
          </template>

          <el-menu
              :default-active="selectedUser?.id"
              @select="handleSelect"
              class="contact-list"
          >
            <el-menu-item
                v-for="contact in contacts"
                :key="contact.id"
                :index="String(contact.id)"
                class="contact-item"
            >
              <div class="contact-content">
                <el-avatar :src="contact.avatar" :size="32" style="margin-top: 12px;"/>
                <div class="contact-info">
                  <div class="contact-header">
                    <span class="contact-name">{{ contact.name }}</span>
                    &nbsp;
                    <span class="message-time">{{ formatMonthDay(contact.lastTime) }}</span>
                  </div>
                </div>
              </div>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧聊天组件 -->
      <el-col :span="18" class="full-height">
        <ChatComponent
            v-if="selectedUser"
            :user-id="selectedUser.id"
            :user-name="selectedUser.name"
            :avatar="selectedUser.avatar"
            class="chat-wrapper"
        />

        <div v-else class="empty-chat">
          <el-icon class="empty-icon">
            <ChatRound/>
          </el-icon>
          <p>请选择联系人开始聊天</p>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue'
import {ChatRound} from '@element-plus/icons-vue'
import ChatComponent from '@/components/ChatComponent.vue'
import {temporaryChat} from "@/api/imMessages.ts";

interface Contact {
  id: number
  name: string
  avatar: string
  lastMessage: string
  lastTime: string
  unread: number
}

const selectedUser = ref<Contact>()

const contacts = ref<Contact[]>([])

onMounted(() => {
  temporaryChat().then(res => {
    contacts.value = res.data.map(item => {
      return {
        id: item.friendId,
        name: item.userName,
        avatar: import.meta.env.VITE_API_BASEURL + item.avatar,
        lastMessage: item.lastMessage,
        lastTime: item.lastTime,
        unread: item.unread
      }
    })
  })
})

const unreadCount = computed(() => {
  return contacts.value.reduce((sum, c) => sum + c.unread, 0)
})

const handleSelect = (index: string) => {
  const contact = contacts.value.find(c => c.id === Number(index))
  if (contact) {
    selectedUser.value = contact
    contact.unread = 0
  }
}

// 新增：格式化日期为月-日
const formatMonthDay = (dateString: string): string => {
  const date = new Date(dateString);
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${month}-${day}`;
}
</script>

<style scoped lang="scss">
@mixin text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-center {
  height: 570px;
  background: #f5f7fa;
  padding: 12px;

  .full-height {
    height: 100%;
  }
}

.contact-card {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  :deep(.el-card__header) {
    padding: 16px 20px;
    background: #fafafa;
    border-bottom: 1px solid #ebeef5;
  }

  :deep(.el-card__body) {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
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

.contact-list {
  border-right: none;
  height: calc(100% - 8px);
  overflow-y: auto;

  :deep(.el-menu-item) {
    height: 60px;
    padding: 0 8px !important;
    transition: background 0.2s;

    &:hover {
      background: #f5f7fa;
    }

    &.is-active {
      background: #ecf5ff;
      border-right: 2px solid var(--el-color-primary);
    }
  }
}

.contact-content {
  display: flex;
  align-items: flex-start;
  height: 100%;
  width: 100%;
  gap: 12px;
  padding: 8px;
  min-width: 0;
}

.contact-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
  flex: 1;
  min-width: 0;
  min-height: 48px;
}

.contact-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
  min-width: 0;
}

.message-text {
  font-size: 12px;
  color: #909399;
  flex: 1;
  min-width: 0;
  @include text-ellipsis;
}

.message-badge {
  margin-left: 4px;
  flex-shrink: 0;
}

.chat-wrapper {
  flex: 1;
  min-height: 500px;
  background: white;
  display: flex;
  flex-direction: column;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.empty-chat {
  height: 100%;
  background: white;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;

  .empty-icon {
    font-size: 48px;
    margin-bottom: 12px;
  }

  p {
    font-size: 14px;
    letter-spacing: 0.5px;
  }
}
</style>
