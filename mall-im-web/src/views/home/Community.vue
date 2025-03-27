<template>
  <div class="community-container">
    <el-row
        v-infinite-scroll="loadMore"
        :infinite-scroll-disabled="loading || !hasMore"
        :gutter="20"
        class="post-list"
    >
      <el-col
          v-for="post in posts"
          :key="post.id"
          :xs="24"
          :sm="12"
          :md="8"
          class="post-item"
      >
        <el-card
            class="post-card"
            shadow="hover"
            :body-style="{ padding: '16px' }"
            @mouseenter="post.id"
        >
          <div class="user-info">
            <el-avatar :src="post.avatar" size="small"/>
            <div class="user-meta">
              <span class="user-name">{{ post.pushName }}</span>
              <el-tag
                  v-if="post.pushSchool"
                  size="small"
                  type="info"
                  effect="plain"
                  class="campus-tag"
              >
                {{ post.pushSchool }}
              </el-tag>
              <el-tag
                  v-if="post.isOfficial"
                  type="danger"
                  size="small"
                  effect="dark"
              >
                官方
              </el-tag>
            </div>
            <el-button
                v-if="!post.isOfficial && post.pushId !== currentUserId"
                type="primary"
                size="small"
                class="add-friend-btn"
                @click="showFriendDialog(post)"
            >
              <el-icon>
                <User/>
              </el-icon>
              添加好友
            </el-button>
          </div>

          <el-carousel
              v-if="post.mallCommunityPicList?.length"
              :autoplay="hoverCard === post.id"
              height="200px"
              indicator-position="none"
          >
            <el-carousel-item
                v-for="(img, idx) in post.mallCommunityPicList"
                :key="idx"
            >
              <el-image
                  :src="img"
                  fit="cover"
                  class="post-image"
                  :preview-src-list="post.mallCommunityPicList"
                  lazy
              />
            </el-carousel-item>
          </el-carousel>

          <el-tooltip
              :content="post.pushContent"
              placement="top"
              effect="light"
              class="post-content-tooltip"
          >
            <p class="post-content">
              {{ post.pushContent }}
            </p>
          </el-tooltip>

          <div class="action-bar">
            <el-button
                type="primary"
                size="small"
                @click="() => startChat(post)"
                class="chat-button"
                :disabled="post.pushId === currentUserId"
            >
              <el-icon>
                <ChatLineRound/>
              </el-icon>
              立即沟通
            </el-button>
          </div>

          <div class="interaction-panel">
            <el-button
                :icon="Star"
                :type="post.isLiked ? 'danger' : 'text'"
                @click="toggleLike(post)"
            >
              {{ post.likes || 0 }}
            </el-button>
            <el-tooltip
                content="推荐一下"
                placement="bottom"
            >
              <!--              <el-button-->
              <!--                  :icon="Share"-->
              <!--                  @click="handleShare(post)"-->
              <!--              />-->
            </el-tooltip>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
        v-model="chatDialogVisible"
        title="即时沟通"
        width="80%"
        :before-close="() => chatDialogVisible = false"
        :modal="false"
        custom-class="fade-dialog"
    >
      <ChatComponent :user-id="selectedUserId" :user-name="selectedUserName" :avatar="selectedAvatar"/>
      <!-- <template #footer>
        <el-button @click="chatDialogVisible = false">关闭</el-button>
      </template> -->
    </el-dialog>

    <el-dialog
        v-model="friendDialogVisible"
        :title="'添加好友：' + selectedUser?.pushName"
        width="500px"
        custom-class="fade-dialog"
        :modal="false"
    >
      <el-form :model="friendForm" label-width="80px">
        <el-form-item label="申请备注">
          <el-input
              v-model="friendForm.applyContent"
              placeholder="请输入申请备注"
              type="textarea"
              :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="friendDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFriendRequest">提交申请</el-button>
      </template>
    </el-dialog>

    <div class="loading-status">
      <el-icon v-if="loading" class="loading-icon">
        <Loading/>
      </el-icon>
      {{ loading ? '加载中...' : '已经到底啦～' }}
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue'
import {useRoute} from 'vue-router'
import ChatComponent from '@/components/ChatComponent.vue'
import {Star, Share, ChatLineRound, Loading, User} from '@element-plus/icons-vue'
import {getGoodsList} from "@/api/goods.ts";
import {ElMessage} from "element-plus";
import {listCommunity} from "@/api/community.ts";
import {useAuthStore} from "@/stores/auth.ts";
import {addFriendRelation} from "@/api/friendRelation.ts";

const route = useRoute()
const chatDialogVisible = ref(false)
const friendDialogVisible = ref(false)
const selectedUserId = ref<number>()
const selectedUserName = ref<string>()
const selectedAvatar = ref<string>()
const selectedUser = ref()

const friendForm = ref({
  relatedId: '',
  applyContent: ''
})

interface User {
  id: number
  name: string
  avatar: string
  campus?: string
}

interface Post {
  id: number,
  pushId: number,
  pushName: string,
  avatar: string,
  pushSchool: string,
  pushContent: string,
  mallCommunityPicList: string[],
  isLiked: boolean,
  likes: number,
  isOfficial: boolean,
}

const currentPage = ref({
  pageNum: 1,
  pageSize: 6
}) // 添加当前页码

const authStore = useAuthStore()
const currentUserId = ref(authStore.userInfo.userId)

const selectedSchoolName = ref<string | null>(route.query.schoolName ? route.query.schoolName : null)
const schoolLoading = ref(false)

const posts = ref<Post[]>([{
  id: 1,
  pushId: 9999,
  pushName: '校园AI',
  pushSchool: '',
  avatar: 'https://pic.rmb.bdstatic.com/bjh/cms/250128/02a28d240e4ca71f913fcf8a90d15987_1738066386.0928_67.png',
  pushContent: '国产 Ai 大模型，性能强悍，响应迅速',
  mallCommunityPicList: [
    'https://t12.baidu.com/it/u=3454171622,257088040&fm=30&app=106&f=JPEG?w=640&h=439&s=6EA18B441F32AED64E7D7D8B0300E088',
  ],
  isLiked: false,
  likes: 128,
  isOfficial: true,
}])

const loading = ref(false)
const hasMore = ref(true)
const hoverCard = ref<number | null>(null)

const toggleLike = (post: Post) => {
  post.isLiked = !post.isLiked
  post.likes += post.isLiked ? 1 : -1
}

const startChat = (post: Post) => {
  selectedUserId.value = post.pushId
  selectedUserName.value = post.pushName
  selectedAvatar.value = post.avatar
  chatDialogVisible.value = true
}

// 初始数据
onMounted(() => {
  loadMore();
})


const loadMore = async () => {
  if (!hasMore.value || loading.value) return

  loading.value = true
  try {
    const res = await listCommunity({
      pageNum: currentPage.value.pageNum,
      pageSize: currentPage.value.pageSize,
      pushSchool: selectedSchoolName.value
    })

    // 检查 res.row 是否存在
    if (!res.rows) {
      console.error('res.row is undefined or null')
      loading.value = false
      return
    }

    const newPosts = res.rows.map(item => ({
      ...item,
      avatar: import.meta.env.VITE_API_BASEURL + item.avatar,
      mallCommunityPicList: item.mallCommunityPicList
          ? item.mallCommunityPicList.map(pic => import.meta.env.VITE_API_BASEURL + pic.picUrl)
          : []
    }))

    posts.value.push(...newPosts)
    // 根据总记录数和当前加载数量判断是否还有更多
    hasMore.value = (currentPage.value.pageNum * currentPage.value.pageSize) < res.total
    currentPage.value.pageNum++
  } catch (err) {
    ElMessage.error('加载失败，请稍后重试')
    console.error('加载社区列表失败:', err)
  } finally {
    loading.value = false
  }
}

const showFriendDialog = (post: Post) => {
  friendDialogVisible.value = true
  selectedUser.value = post
}

// 提交好友申请
const submitFriendRequest = () => {
  if (!friendForm.value.applyContent) {
    ElMessage.error('申请备注不能为空')
    return
  }

  if (!selectedUser.value) {
    ElMessage.error('请选择要添加的好友')
    return
  }

  friendForm.value.relatedId = selectedUser.value.pushId

  addFriendRelation(friendForm.value).then(res => {
    if (res.code === 200) {
      ElMessage.success('申请成功')
    }
  })
  friendDialogVisible.value = false
  friendForm.value.relatedId = ''
  friendForm.value.applyContent = ''
}

</script>

<style scoped>
.community-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.post-card {
  margin-bottom: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  overflow: hidden;
  min-height: 450px; /* 设置最小高度 */
  display: flex;
  flex-direction: column;
}

.post-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.user-info {
  flex-shrink: 0;
}

.el-carousel {
  flex-shrink: 0;
  max-height: 240px;
}

.post-content {
  flex: 1;
  overflow: hidden;
}

.action-bar {
  flex-shrink: 0;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(255, 106, 0, 0.15);
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  justify-content: space-between;
}

.add-friend-btn {
  margin-left: auto;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.add-friend-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 106, 0, 0.15);
}

.user-meta {
  margin-left: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.post-content {
  margin: 16px 0;
  color: #666;
  line-height: 1.7;
  font-size: 14px;
  max-height: 3em;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  transition: all 0.3s ease;
}

.post-content:hover {
  color: #333;
}

.post-image {
  width: 100%;
  border-radius: 8px;
  margin: 12px 0;
}

.action-bar {
  margin-top: 16px;
  text-align: center;
}

.chat-button {
  width: 100%;
  padding: 12px;
}

.interaction-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.loading-status {
  text-align: center;
  padding: 24px;
  color: #999;
}

.loading-icon {
  animation: rotating 2s linear infinite;
  margin-right: 8px;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.post-content-tooltip {
  max-width: 600px;
}

.post-content-tooltip .el-tooltip__popper {
  line-height: 1.8;
  padding: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.fade-dialog {
  transition: all 0.3s ease-out;
}

.fade-dialog-enter-from,
.fade-dialog-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.fade-dialog-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 1, 1);
}
</style>

