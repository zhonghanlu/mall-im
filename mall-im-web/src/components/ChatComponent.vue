<template>
  <div class="chat-container">
    <el-card class="chat-card">
      <div class="chat-header">
        <el-avatar :src="currentContact.avatar"/>
        <span class="contact-name">{{ currentContact.name }}</span>
      </div>

      <el-scrollbar
          ref="messageAreaRef"
          class="message-area"
          always
          @scroll="handleScroll"
      >
        <div
            v-for="message in messages"
            :key="message.id"
            class="message-item"
            :class="{ 'is-self': message.isSelf }"
            :style="{ '--enter-delay': `calc(0.1s * ${message.id % 6})` }"
        >
          <transition name="message" appear>
            <div class="message-content">
              <div class="bubble" v-html="parseMarkdown(message.content)"></div>
              <div class="meta-info">
                <el-icon class="status-icon">
                  <CircleCheck/>
                </el-icon>
                <span class="message-time">{{ formatTime(message.time) }}</span>
              </div>
            </div>
          </transition>
        </div>

        <!-- 等待AI标识 -->
        <div v-if="isWaitingForAI" class="message-item waiting-for-ai">
          <div class="message-content">
            <div class="bubble">
              <span>等待AI回复...</span>
              <div class="meta-info">
                <el-icon class="status-icon">
                  <Loading/>
                </el-icon>
                <span class="message-time">{{ formatTime(new Date().toISOString()) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-scrollbar>

      <div class="input-area">
        <el-input
            v-model="newMessage"
            placeholder="输入消息..."
            @keyup.enter="sendMessage"
            class="message-input"
            :class="{ 'input-active': newMessage }"
        >
          <template #append>
            <el-button
                type="primary"
                @click="sendMessage"
                class="send-button"
                :disabled="!newMessage"
            >
              发送
              <el-icon class="send-icon">
                <Promotion/>
              </el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch, onUnmounted } from 'vue'
import { CircleCheck, Loading, Promotion } from '@element-plus/icons-vue'
import { ElScrollbar } from "element-plus";
import { marked } from 'marked';
import hljs from 'highlight.js';
import 'highlight.js/styles/default.css';
import { useAuthStore } from "@/stores/auth.ts";
import { singleChat } from "@/api/imMessages.ts";

interface Contact {
  id: number
  name: string
  avatar: string
}

interface Message {
  id: number
  content: string
  isSelf: boolean
  time: string
}

const props = defineProps<{
  userId: number
  userName: string
  avatar: string
}>()

const socket = ref<WebSocket | null>(null)

const authStore = useAuthStore();
const userId = authStore.userInfo.userId;

const initSocket = () => {
  const url = `${import.meta.env.VITE_WSS_BASEURL}/websocket/message/${userId}`
  socket.value = new WebSocket(url)

  socket.value.onopen = () => {
    console.log('WebSocket connected')
  }

  socket.value.onmessage = (event) => {
    const message: Message = {
      id: Date.now(),
      content: event.data,
      isSelf: false,
      time: Date.now()
    }
    messages.value.push({
      ...message,
      isSelf: message.userId === props.userId
    })
    scrollToBottom()
  }

  socket.value.onclose = () => {
    console.log('WebSocket disconnected')
  }

  socket.value.onerror = (error) => {
    console.error('WebSocket error:', error)
  }
}

const currentContact = ref<Contact>({
  id: props.userId,
  name: props.userName,
  avatar: props.avatar
})

const messages = ref<Message[]>([
  {
    id: 1,
    content: '你好，有什么可以帮你的吗？',
    isSelf: false,
    time: Date.now()
  }
])

const newMessage = ref('')
const isWaitingForAI = ref(false)

const messageAreaRef = ref<InstanceType<typeof ElScrollbar>>()

const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  return isNaN(date.getTime())
      ? timeStr
      : date.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      })
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageAreaRef.value && messageAreaRef.value.wrapRef) {
      messageAreaRef.value.scrollTo({
        top: messageAreaRef.value.wrapRef.scrollHeight,
        behavior: 'smooth'
      })
    }
  })
}

const handleScroll = () => {
  // 可以在这里添加滚动处理逻辑
}

const sendMessage = async () => {
  if (!newMessage.value.trim()) return

  const timestamp = new Date().toISOString()

  const message: Message = {
    id: Date.now(),
    content: newMessage.value,
    isSelf: true,
    time: timestamp
  }

  messages.value.push(message)
  newMessage.value = ''
  scrollToBottom()

  if (props.userId === 9999) {
    isWaitingForAI.value = true
    try {
      const response = await fetch(import.meta.env.VITE_API_BASEURL + '/ai/chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ "message": message.content })
      });

      if (!response.ok) {
        throw new Error('网络响应不正常');
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder('utf-8');
      let content = ''

      const aiMessage: Message = {
        id: Date.now() + 1,
        content: '',
        isSelf: false,
        time: timestamp
      }

      messages.value.push(aiMessage)

      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        content += decoder.decode(value)
        aiMessage.content = content
        messages.value = [...messages.value] // Force reactivity update
        scrollToBottom()
      }
    } catch (error) {
      console.error('Error calling AI API:', error)
    } finally {
      isWaitingForAI.value = false
    }
  } else {
    if (socket.value && socket.value.readyState === WebSocket.OPEN) {
      const messageDto = {
        sendId: userId,
        receiverId: currentContact.value.id,
        content: message.content
      }
      console.log(messageDto)
      socket.value.send(JSON.stringify(messageDto))
    } else {
      console.error('WebSocket is not open')
    }
  }
}

onMounted(() => {
  console.log(props.userId);
  console.log(props.userName);
  console.log(props.avatar);

  // 初始化历史聊天记录
  initSingleChatHistory(userId, props.userId);

  initSocket()
})

// 初始化历史聊天记录
const initSingleChatHistory = (userId: number, chatUserId: number) => {
  // 聊天对象存在时，获取聊天记录
  if (chatUserId) {
    singleChat({
      userId: userId,
      chatUserId: chatUserId
    }).then(res => {
      messages.value = res.data.map((item: any) => ({
        id: item.id,
        content: item.content,
        isSelf: item.senderId === userId,
        time: item.sentAt
      }))
      scrollToBottom()
    })
  }
}

onUnmounted(() => {
  if (socket.value) {
    socket.value.close()
  }
})

watch(() => props.userId, (newUserId, oldUserId) => {
  console.log('userId changed from', oldUserId, 'to', newUserId);

  // 关闭旧的 WebSocket 连接
  if (socket.value) {
    socket.value.close()
  }

  // 重置状态
  messages.value = [];
  currentContact.value = {
    id: newUserId,
    name: props.userName,
    avatar: props.avatar
  };

  // 初始化新的 WebSocket 连接
  initSocket();

  // 初始化新的历史聊天记录
  initSingleChatHistory(userId, newUserId);
})

const parseMarkdown = (content: string) => {
  marked.setOptions({
    breaks: true,
    highlight: function (code, lang) {
      return hljs.highlightAuto(code).value;
    }
  });
  return marked.parse(content);
}
</script>

<style scoped>
.chat-container {
  max-width: 800px;
  margin: 0px auto;
}

.chat-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  height: 570px;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.contact-name {
  margin-left: 10px;
  font-size: 16px;
  font-weight: 500;
}

.message-area {
  flex: 1;
  padding: 20px 0;
  height: 400px;
}

.message-item {
  margin: 10px 0;
  display: flex;
  justify-content: flex-start;

  &.is-self {
    justify-content: flex-end;

    .message-content {
      background-color: #409eff;
      color: white;
    }
  }

  &.waiting-for-ai {
    justify-content: flex-start;

    .message-content {
      background-color: #f0f2f5;
      color: #909399;
    }
  }
}

.message-content {
  max-width: 70%;
  padding: 8px 0;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.bubble {
  padding: 12px 16px;
  border-radius: 12px;
  background: #f0f2f5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: relative;
  transition: transform 0.2s ease;
}

.bubble::after {
  content: '';
  position: absolute;
  width: 8px;
  height: 8px;
  background: inherit;
  transform: rotate(45deg);
  left: -4px;
  top: 12px;
}

.is-self .bubble {
  background: #409eff;
  color: white;
}

.is-self .bubble::after {
  left: auto;
  right: -4px;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  opacity: 0.8;
}

.message-time {
  font-size: 12px;
  font-feature-settings: 'tnum';
}

.status-icon {
  font-size: 12px;
  color: currentColor;
}

.message-enter-active {
  animation: messageIn 0.3s ease-out var(--enter-delay);
}

@keyframes messageIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-input {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  :deep(.el-input__inner) {
    border-radius: 20px;
    padding-right: 100px;
    transition: inherit;
  }

  &.input-active {
    :deep(.el-input__inner) {
      border-color: #409eff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
    }
  }
}

.send-button {
  transition: inherit;
  transform-origin: right center;
  padding: 12px 20px;
  border-radius: 18px;

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  }
}

.send-icon {
  margin-left: 6px;
  transition: transform 0.2s ease;
}
</style>
