<template>
  <div class="login-container">
    <el-row :gutter="20" class="container-wrapper">
      <!-- 左侧装饰区域 -->
      <el-col :xs="24" :sm="12" :md="14" class="decorate-area">
        <div class="branding">
          <h1 class="slogan">校园商城交易平台</h1>
          <p class="sub-slogan">发现好物 · 连接校友</p>
        </div>
        <el-image
            src="https://images.pexels.com/photos/3935702/pexels-photo-3935702.jpeg"
            fit="cover"
            class="decorate-image"
        >
          <template #placeholder>
            <div class="image-placeholder">
              <i class="el-icon-loading"/>
            </div>
          </template>
        </el-image>
      </el-col>

      <!-- 右侧表单区域 -->
      <el-col :xs="24" :sm="12" :md="10" class="form-area">
        <el-tabs v-model="activeTab" stretch class="form-tabs" @tab-click="(tab) => {
          resetForm(tab.paneName)
        }">
          <!-- 登录表单 -->
          <el-tab-pane label="登录" name="login">
            <el-form
                ref="loginFormRef"
                :model="loginForm"
                :rules="loginRules"
                @submit.prevent="handleLogin"
            >
              <el-form-item prop="username">
                <el-input
                    v-model="loginForm.username"
                    placeholder="请输入账户"
                    prefix-icon="Iphone"
                    size="large"
                />
              </el-form-item>

              <el-form-item prop="password">
                <el-input
                    v-model="loginForm.password"
                    type="password"
                    placeholder="请输入密码"
                    prefix-icon="Lock"
                    size="large"
                    show-password
                />
              </el-form-item>

              <el-form-item prop="remember">
                <el-checkbox v-model="loginForm.remember">
                  记住账号
                </el-checkbox>
              </el-form-item>

              <el-form-item>
                <el-button
                    type="primary"
                    size="large"
                    native-type="submit"
                    class="submit-btn"
                >
                  立即登录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 注册表单 -->
          <el-tab-pane label="注册" name="register">
            <el-form
                ref="registerFormRef"
                :model="registerForm"
                :rules="registerRules"
                @submit.prevent="handleRegister"
            >
              <el-form-item prop="nickName">
                <el-input
                    v-model="registerForm.nickName"
                    placeholder="请输入用户名"
                    prefix-icon="Avatar"
                    size="large"
                />
              </el-form-item>

              <el-form-item prop="username">
                <el-input
                    v-model="registerForm.username"
                    placeholder="请输入账户"
                    prefix-icon="Iphone"
                    size="large"
                />
              </el-form-item>

              <el-form-item prop="password">
                <el-input
                    v-model="registerForm.password"
                    type="password"
                    placeholder="请输入密码"
                    prefix-icon="Lock"
                    size="large"
                    show-password
                />
              </el-form-item>

              <el-form-item prop="schoolId">
                <el-select
                    v-model="registerForm.schoolId"
                    placeholder="请选择校区"
                    prefix-icon="School"
                    size="large"
                    style="width: 100%"
                >
                  <template #prefix>
                    <el-icon>
                      <School/>
                    </el-icon>
                  </template>
                  <el-option v-for="item in campusList" :label="item.schoolName" :value="item.id"/>
                </el-select>
              </el-form-item>

              <el-form-item>
                <el-button
                    type="success"
                    size="large"
                    native-type="submit"
                    class="submit-btn"
                >
                  立即注册
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {ElMessage} from 'element-plus'
import type {FormInstance, FormRules} from 'element-plus'
import {login, logout, register} from "@/api/user";
import {encrypt, decrypt} from '@/utils/jsencrypt';
import {useAuthStore} from '@/stores/auth';
import {useRouter} from 'vue-router';
import {listAll} from "@/api/mallSchool.ts";
import {useCartStore} from "@/stores/cart.ts";

const cartStore = useCartStore();

// 表单引用
const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()
const activeTab = ref('login')

// 登录表单
const loginForm = ref({
  username: '',
  password: '',
  remember: false
})

// 注册表单
const registerForm = ref({
  nickName: '',
  username: '',
  password: '',
  schoolId: ''
})

// 校区 list
const campusList = ref([
  {
    id: 0,
    schoolName: '',
  }
])

// 验证规则
const loginRules: FormRules = {
  username: [
    {required: true, message: '请输入账户', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'}
  ]
}

const registerRules: FormRules = {
  nickName: [
    {required: true, message: '请输入用户名', trigger: 'blur'}
  ],
  username: [
    {required: true, message: '请输入账户', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'}
  ],
  schoolId: [
    {required: true, message: '请选择所属校区', trigger: 'change'}
  ]
}

const resetForm = (type: 'login' | 'register') => {
  if (type === 'login') {
    loginFormRef.value?.resetFields()
  } else {
    registerFormRef.value?.resetFields()
  }
}

const router = useRouter();
const authStore = useAuthStore();

const handleLogin = async () => {
  // try {
  await loginFormRef.value?.validate()
  console.log('登录表单:', loginForm.value)

  const encryptedPassword = encrypt(loginForm.value.password);
  const res = await login(loginForm.value);

  // 记住我功能
  if (loginForm.value.remember) {
    localStorage.setItem('encryptedCredential', encryptedPassword);
  } else {
    localStorage.removeItem('encryptedCredential');
  }

  if (res.code === 200) {
    ElMessage.success('登录成功');
    authStore.setToken(res.token);
    await authStore.fetchUserInfo();
    await router.push('/');
  } else {
    ElMessage.error(res.message || '登录失败');
  }
}

const handleRegister = async () => {
  try {
    await registerFormRef.value?.validate()
    console.log('注册表单:', registerForm.value)
    register(registerForm.value).then(res => {
      if (res.code === 200) {
        ElMessage.success('注册成功');
        activeTab.value = 'login';
        resetForm('register');
      } else {
        ElMessage.error(res.message || '注册失败');
      }
    })
  } catch (error) {
    ElMessage.error('请正确填写表单')
  }
}

// 初始化校区数据
onMounted(() => {
  listAll().then(res => {
    campusList.value = res.data;
  });
})
</script>

<style scoped lang="scss">
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.container-wrapper {
  width: 100%;
  max-width: 1200px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  min-height: 600px;
}

.decorate-area {
  height: 600px;
  padding: 40px;
  background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
  color: white;
  position: relative;
  display: flex;
  flex-direction: column;
}

.branding {
  position: relative;
  z-index: 1;
  margin-bottom: auto;
}

.slogan {
  font-size: 32px;
  margin-bottom: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.sub-slogan {
  font-size: 18px;
  opacity: 0.9;
}

.decorate-image {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60%;
  border-radius: 20px 0 0 0;
  transition: transform 0.3s;

  &:hover {
    transform: scale(1.02);
  }
}

.form-area {
  padding: 60px 40px;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(255, 106, 0, 0.1);
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: auto;
  min-height: unset;
}

:deep(.el-tabs__nav) {
  position: relative;
  width: 100%;
  display: flex;
  justify-content: center;
  left: 0;
  right: 0;
  // 添加以下样式确保点击时位置固定
  transition: none;
}

:deep(.el-tabs__item) {
  flex: 0 0 auto;
  padding: 0 30px !important;
  transition: all 0.3s;

  &.is-active::before {
    left: 50%;
    transform: translateX(-50%);
    width: 60%;
  }
}

:deep(.el-tabs__item.is-active::before) {
  width: 60%;
  left: 20%;
  transform: translateX(0);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

:deep(.el-tabs__item) {
  flex-shrink: 0;
  font-size: 18px;
  padding: 0 25px !important;;
  transition: all 0.3s;

  &::before {
    // bottom: -1px;
    content: '';
    @apply absolute
    bottom-0
    left-1/2
    w-0
    h-1
    bg-primary
    transition-all duration-300;
  }

  &.is-active::before {
    width: 60%;
    left: 40%;
  }
}

:deep(.el-tabs__active-bar) {
  display: none;
}

:deep(.el-input) {
  --el-input-border-color: #eee;
  --el-input-hover-border-color: var(--el-color-primary-light-3);
  --el-input-focus-border-color: var(--el-color-primary);

  .el-input__wrapper {
    border-radius: 8px;
    transition: all 0.3s;
    box-shadow: 0 2px 6px rgba(255, 106, 0, 0.08);

    &:hover {
      box-shadow: 0 4px 12px rgba(255, 106, 0, 0.12);
    }
  }
}

.submit-btn {
  width: 100%;
  margin-top: 20px;
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 1px;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(255, 106, 0, 0.2);
  }
}

.image-placeholder {
  @apply w-full
  h-full
  flex
  items-center justify-center;
  background: rgba(255, 255, 255, 0.8);

  i {
    font-size: 40px;
    color: var(--el-color-primary);
  }
}

@media (max-width: 768px) {
  .container-wrapper {
    width: 95%;
    margin: 0 auto;
    min-height: auto;
  }

  .decorate-area {
    height: 200px;
    padding: 20px;

    .slogan {
      font-size: 24px;
    }

    .sub-slogan {
      font-size: 14px;
    }
  }

  .form-area {
    padding: 30px 20px;
    min-height: auto;

    .el-form-item {
      margin-bottom: 18px;
    }

    .el-input {
      :deep(.el-input__wrapper) {
        padding: 0 12px;
      }
    }
  }
}

</style>
