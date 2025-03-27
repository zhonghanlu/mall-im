<template>
  <div class="user-center">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="menu-card">
          <el-menu :default-active="activeMenu">
            <el-menu-item index="user" @click="activeMenu = 'user'">
              <el-icon><User /></el-icon>
              个人中心
            </el-menu-item>
            <el-menu-item index="order" @click="activeMenu = 'order'">
              <el-icon><Document /></el-icon>
              订单中心
            </el-menu-item>
            <el-menu-item index="cart" @click="activeMenu = 'cart'">
              <el-icon><ShoppingCart /></el-icon>
              我的购物车
            </el-menu-item>
            <el-menu-item index="message" @click="activeMenu = 'message'">
              <el-icon><Message /></el-icon>
              我的消息
            </el-menu-item>
            <el-menu-item index="friend" @click="activeMenu = 'friend'">
              <el-icon><ChatDotRound /></el-icon>
              我的朋友
            </el-menu-item>
            <el-menu-item index="apply" @click="activeMenu = 'apply'">
              <el-icon><ZoomIn /></el-icon>
              好友申请
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="18">
        <component :is="currentComponent" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { Document, ShoppingCart, User, Message,ZoomIn,ChatDotRound} from '@element-plus/icons-vue'
// 假设文件路径是相对于当前文件的正确路径
import UserProfile from '@/views/userCenter/UserProfile.vue'
import UserOrder from '@/views/userCenter/UserOrder.vue'
import CartView from '@/views/userCenter/UserCart.vue'
import MessageCenter from '@/views/userCenter/UserMessage.vue'
import FriendList from '@/views/userCenter/UserFriendList.vue'
import FriendApply from '@/views/userCenter/UserFriendApply.vue'
import { ref, computed,watch,onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = ref('user')

onMounted(() => {
  const tab = route.query.tab
  if (tab) {
    activeMenu.value = tab as string
  }
})

watch(() => route.query.tab, (newVal) => {
  activeMenu.value = newVal as string
})

watch(() => activeMenu.value, (newVal) => {
  router.push({ query: { tab: newVal } })
})

const currentComponent = computed(() => {
  return {
    'user': UserProfile,
    'order': UserOrder,
    'cart': CartView,
    'message': MessageCenter,
    'friend': FriendList,
    'apply': FriendApply
  }[activeMenu.value]
})
</script>

<style scoped>
.user-center {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
.menu-card {
  margin-right: 20px;
}
</style>