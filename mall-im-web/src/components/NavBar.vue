<template>
  <el-menu
      mode="horizontal"
      :router="true"
      class="nav-container"
  >
    <el-menu-item index="/">首页</el-menu-item>
    <el-menu-item index="/school">社区</el-menu-item>

    <el-row class="search-wrapper" justify="center">
      <el-col :span="18">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索商品"
            size="large"
            @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button
                type="primary"
                icon="Search"
                @click="handleSearch"
                :style="{ padding: '0 20px' }"
            />
          </template>
        </el-input>
      </el-col>
    </el-row>
    <div class="right-wrapper">
      <el-dropdown @command="handleCommand">
        <div class="user-avatar">
          <!--          <image-preview :src="avatarUrl"/>-->
          <img :src="authStore.avatar" class="user-avatar-img"/>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="user-center?tab=user">个人中心</el-dropdown-item>
            <el-dropdown-item command="user-center?tab=order">我的订单</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <el-badge :value="cartStore.totalItems" class="cart-badge">
        <el-menu-item index="/user-center?tab=cart" class="cart-item">
          <el-icon class="cart-icon">
            <ShoppingCart/>
          </el-icon>
        </el-menu-item>
      </el-badge>
    </div>
  </el-menu>
</template>

<script setup lang="ts">
import {ShoppingCart, User} from '@element-plus/icons-vue'
import {useRouter} from 'vue-router'
import {useCartStore} from '../stores/cart'
import {onMounted, ref} from 'vue'
import {logout} from '@/api/user'
import {useAuthStore} from '@/stores/auth';
import ImagePreview from "@/components/imgPreview.vue";

const cartStore = useCartStore()
cartStore.loadCart();
const router = useRouter()
const authStore = useAuthStore();
const searchKeyword = ref('')

const avatarUrl = ref(import.meta.env.VITE_API_BASEURL + authStore.avatar)

onMounted(() => {
  console.log(authStore.avatar)
})


const handleSearch = () => {
  router.push(`/search?q=${searchKeyword.value}`)
  searchKeyword.value = ''
}

const handleCommand = (command) => {
  if (command === 'logout') {
    // 退出登录逻辑
    logout();
    // 清除用户信息token
    authStore.clearAuth();
    router.push('/login')
  } else {
    router.push(`/${command}`)
  }
}
</script>

<style scoped>
.nav-container {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
}

.user-avatar:hover {
  transform: scale(1.1);
  transition: transform 0.3s ease;
}

.cart-info .item {
  transition: all 0.3s ease;
}

.cart-info .item:hover {
  color: var(--el-color-primary);
  transform: translateY(-2px);
}

.el-dropdown-menu {
  margin-top: 10px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.user-name {
  color: var(--el-color-primary);
  margin-left: 8px;
}

.nav-container {
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #fff !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1) !important;
  padding: 12px 40px;
  gap: 30px;
}

.search-wrapper {
  width: 800px;
  margin: 0 $--md;

  .el-input {
    box-shadow: $--box-shadow-base;

    :deep(.el-input__wrapper) {
      background: rgba($--color-primary, 0.05);
    }
  }
}

.right-wrapper {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-left: auto;
}

/* .el-menu-item {
  color: #666 !important;
  &.is-active {
    color: #ff5000 !important;
    &::after {
      background: #ff5000 !important;
    }
  }
} */

.user-avatar-img {
  cursor: pointer;
  width: 35px;
  height: 35px;
  border-radius: 8px;
}

.el-menu--horizontal {
  border-bottom: none;
}

.search-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px !important;

  .search-box {
    margin: 0 20px;
    flex: 0 1 500px;
  }

  .el-menu-item {
    padding: 0 15px !important;
  }
}

.user-avatar {
  display: flex;
  align-items: center;
  padding: 0 15px;
  cursor: pointer;

  &:hover {
    background-color: #f5f7fa;
  }
}
</style>
