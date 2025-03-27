<template>
  <div class="user-profile">
    <!-- 个人信息模块 -->
    <el-card class="profile-card">
      <div class="avatar-upload">
        <image-upload v-model="userInfo.avatar" :limit="1">
          <el-avatar :size="120" :src="userInfo.avatar"/>
          <div class="upload-mask">
            <el-icon>
              <Upload/>
            </el-icon>
          </div>
        </image-upload>
      </div>

      <el-form label-width="100px" class="info-form">
        <el-form-item label="用户名称">
          <el-input v-model="userInfo.nickName"/>
        </el-form-item>
        <el-form-item label="账户名称">
          <el-input v-model="userInfo.userName" disabled/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleModifyUserInfo">
            修改信息
          </el-button>
          <el-button type="primary" @click="handleModifyPassword">
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 地址管理模块 -->
    <el-card class="address-card">
      <div class="header">
        <h3>收货地址管理</h3>
        <el-button type="primary" @click="handleAddAddress">
          新增地址
        </el-button>
      </div>

      <el-table :data="addressList" style="width: 100%">
        <el-table-column prop="userName" label="收件人" width="120"/>
        <el-table-column prop="phone" label="联系电话" width="150"/>
        <el-table-column prop="address" label="详细地址"/>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button link @click="handleEditAddress(scope.row)">编辑</el-button>
            <el-button
                link
                type="danger"
                @click="handleDeleteAddress(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 密码修改弹窗 -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="30%">
      <el-form
          ref="pwdForm"
          :model="pwdForm"
          label-width="100px"
          :rules="pwdRules"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
              v-model="pwdForm.oldPassword"
              type="password"
              show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
              v-model="pwdForm.newPassword"
              type="password"
              show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              v-model="pwdForm.confirmPassword"
              type="password"
              show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPassword">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 地址编辑弹窗 -->
    <el-dialog
        v-model="addressDialogVisible"
        :title="isEditAddress ? '编辑地址' : '新增地址'"
        width="40%"
    >
      <el-form
          ref="addressForm"
          :model="currentAddress"
          label-width="80px"
          :rules="addressRules"
      >
        <el-form-item label="收件人" prop="userName">
          <el-input v-model="currentAddress.userName"/>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="currentAddress.phone"/>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input
              v-model="currentAddress.address"
              type="textarea"
              :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddress">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {ref, reactive, computed, onMounted} from 'vue'
import {Upload} from '@element-plus/icons-vue'
import {ElMessage, type UploadProps} from 'element-plus'
import {useAuthStore} from "@/stores/auth.ts";
import ImageUpload from "@/components/imgUpload.vue";
import {updateUser} from "@/api/user.ts";
import {addAddress, deleteAddress, editAddress, getAddressList} from "@/api/address.ts";

interface Address {
  id?: number
  userName: string
  phone: string
  address: string
}

const authStore = useAuthStore()

// 用户信息
const userInfo = reactive({
  avatar: authStore.avatar,
  nickName: authStore.userInfo.nickName,
  userName: authStore.userInfo.userName,
})

// 地址列表
const addressList = ref<Address[]>([
  {
    id: 1,
    userName: '张三',
    phone: '13800138000',
    address: '北京市海淀区中关村大街1号'
  }
])

// 密码修改相关
const pwdDialogVisible = ref(false)
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{required: true, message: '请输入旧密码', trigger: 'blur'}],
  newPassword: [{required: true, message: '请输入新密码', trigger: 'blur'}],
  confirmPassword: [{required: true, message: '请确认密码', trigger: 'blur'}]
}

// 地址编辑相关
const addressDialogVisible = ref(false)
const isEditAddress = ref(false)
const currentAddress = reactive<Address>({
  userName: '',
  phone: '',
  address: ''
})

const addressRules = {
  userName: [{required: true, message: '请输入收件人姓名', trigger: 'blur'}],
  phone: [
    {required: true, message: '请输入联系电话', trigger: 'blur'},
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ],
  address: [{required: true, message: '请输入详细地址', trigger: 'blur'}]
}

onMounted(() => {
  initAddressList();
})

// 初始化地址信息
const initAddressList = () => {
  getAddressList().then(res => {
    addressList.value = res.data
  })
}

// 计算属性，截取 avatar 的相对路径
const avatarRelativePath = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASEURL
  const fullUrl = userInfo.avatar
  if (fullUrl.startsWith(baseUrl)) {
    return fullUrl.substring(baseUrl.length)
  }
  return fullUrl
})

// 修改个人信息
const handleModifyUserInfo = () => {
  updateUser({
    "userId": authStore.userInfo.userId,
    "nickName": userInfo.nickName,
    "avatar": avatarRelativePath.value
  }).then(res => {
    if (200 === res.code) {
      ElMessage.success('修改成功')
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  })

}

// 打开修改密码弹窗
const handleModifyPassword = () => {
  pwdDialogVisible.value = true
}

// 提交密码修改
const submitPassword = () => {
  // 这里添加密码修改逻辑
  pwdDialogVisible.value = false

}

// 处理地址操作
const handleAddAddress = () => {
  isEditAddress.value = false
  Object.assign(currentAddress, {
    userName: '',
    phone: '',
    address: ''
  })
  addressDialogVisible.value = true
}

// 修改地址
const handleEditAddress = (address: Address) => {
  isEditAddress.value = true
  Object.assign(currentAddress, address)
  addressDialogVisible.value = true
}

const handleDeleteAddress = (id: number) => {
  deleteAddress(id).then(res => {
    if (200 == res.code) {
      ElMessage.success('删除成功')
      // 回调
      initAddressList();
    }
  })
}

// 新增修改提交
const submitAddress = () => {
  console.log(currentAddress)
  if (!isEditAddress.value) {
    // 新增
    addAddress(currentAddress).then(res => {
      if (200 === res.code) {
        ElMessage.success('新增成功')
        // 回调
        initAddressList();
      } else {
        ElMessage.error(res.message || '新增失败')
      }
    })
  } else {
    // 修改
    editAddress(currentAddress).then(res => {
      if (200 === res.code) {
        ElMessage.success('修改成功')
        // 回调
        initAddressList();
      } else {
        ElMessage.error(res.message || '修改失败')
      }
    })
  }
  addressDialogVisible.value = false
}
</script>

<style scoped>
.user-profile {
  padding: 20px;
}

.profile-card {
  margin-bottom: 20px;
}

.avatar-upload {
  text-align: center;
  margin-bottom: 20px;
}

.avatar-uploader {
  position: relative;
  display: inline-block;
}

.upload-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.avatar-uploader:hover .upload-mask {
  opacity: 1;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.info-form {
  max-width: 500px;
  margin: 0 auto;
}
</style>
