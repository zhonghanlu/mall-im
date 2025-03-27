<template>
  <div class="detail-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <div>
          <image-preview :src="goodsDetail.goodsItemUrl" class="detail-image"/>
          <h1>商品介绍</h1>
          <div v-html="goodsDetail.articleContent" class="article-content">
          </div>
        </div>
      </el-col>
      <el-col :span="12">
        <h2 class="goods-title">{{ goodsDetail.goodsName }}</h2>
        <!-- 在商品标题右侧添加分享按钮 -->
        <div class="header-actions">
          <el-button type="warning" @click="shareDialogVisible = true" plain>
            <el-icon>
              <Share/>
            </el-icon>
            分享心得
          </el-button>
        </div>
        <div class="price-section">
          <span class="current-price">¥{{ goodsDetail.newPrice }}</span>
          <span class="sales">库存 {{ goodsDetail.goodsStock }} 件</span>
        </div>
        <div class="actions">
          <el-button type="primary" size="large" @click="handleBuy">立即购买</el-button>
          <el-button type="success" size="large" @click="addToCart">加入购物车</el-button>
        </div>
        <el-divider/>
        <div class="description">
          <h3>商品详情</h3>
          <p>{{ goodsDetail.goodsFeature }}</p>
        </div>

        <el-tabs v-model="activeTab" class="detail-tabs">
          <el-tab-pane label="商品参数" name="spec">
            <el-table :data="specData" border>
              <el-table-column prop="specName" label="参数名称" width="150"/>
              <el-table-column prop="remark" label="参数值"/>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="用户评价" name="review">
            <div class="review-list">
              <div v-for="review in reviews" :key="review.id" class="review-item">
                <el-avatar :src="review.avatar"/>
                <div class="review-content">
                  <div class="review-header">
                    <span class="user-name">{{ review.name }}</span>
                    <el-rate v-model="review.rating" disabled/>
                    <span class="review-time">{{ review.time }}</span>
                  </div>
                  <p class="review-text">{{ review.content }}</p>
                </div>
              </div>
            </div>
            <el-button type="primary" class="add-review" @click="showReviewDialog">发表评价</el-button>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>

    <!-- 发表评价的对话框 -->
    <el-dialog
        title="发表评价"
        v-model="reviewDialogVisible"
        width="50%"
        :before-close="handleClose"
    >
      <el-form :model="reviewForm" ref="reviewFormRef" label-width="100px">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="reviewForm.rating"/>
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input
              v-model="reviewForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入您的评价内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取 消</el-button>
          <el-button type="primary" @click="submitReview">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加分享对话框 -->
    <el-dialog v-model="shareDialogVisible" title="分享商品心得" width="600px">
      <el-form :model="shareForm" label-width="80px">
        <el-form-item label="分享内容">
          <el-input
              v-model="shareForm.pushContent"
              type="textarea"
              :rows="4"
              placeholder="请输入您的使用体验或推荐理由"
          />
        </el-form-item>

        <el-form-item label="上传图片" prop="picUrlTemp">
          <image-upload v-model="shareForm.picUrlTemp" :limit="3"/>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="shareDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleShareSubmit">提交分享</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from 'vue-router'
import {ref, reactive, onMounted, watch} from 'vue'
import {ElMessage} from 'element-plus'
import {Share} from '@element-plus/icons-vue' // 新增图标导入
import {useCartStore} from '@/stores/cart'
import {addView, getGoodsDetail} from "@/api/goods.ts";
import ImagePreview from "@/components/imgPreview.vue";
import {addCommunity} from "@/api/community.ts";
import {useAuthStore} from "@/stores/auth.ts";
import ImageUpload from "@/components/imgUpload.vue";

interface GoodsDetail {
  id: number
  goodsName: string
  newPrice: number
  goodsItemUrl: string
  goodsStock: number
  goodsFeature: string
  articleContent: string
}

const route = useRoute()
const goodsId = ref(Number(route.params.id))
const reviewDialogVisible = ref(false)
const reviewForm = ref({})

const goodsDetail = ref<GoodsDetail>({
  id: 1,
  goodsName: '',
  newPrice: 0,
  goodsItemUrl: '',
  goodsStock: 0,
  goodsFeature: '',
  articleContent: ''
})

const router = useRouter();

const addToCart = () => {
  const {id, name, price} = goodsDetail.value
  useCartStore().addItem({id, name, price})
  ElMessage.success('已加入购物车')
}

const handleBuy = () => {
  // 立即购买逻辑
  const goodsList = [{
    goodsId: goodsDetail.value.id,
    goodsName: goodsDetail.value.goodsName,
    goodsPrice: goodsDetail.value.newPrice,
    quantity: 1,
    totalPrice: goodsDetail.value.newPrice * 1
  }];
  router.push({
    name: 'order',
    query: {
      goodsList: JSON.stringify(goodsList)
    }
  })
}

const activeTab = ref('spec')
const specData = ref([
  {specName: '', remark: ''}
])

const reviews = ref([
  {
    id: 1,
    name: '学生用户',
    avatar: 'https://via.placeholder.com/40',
    rating: 4,
    content: '纸质很好，性价比超高！',
    time: '2023-08-20'
  }
])

const showReviewDialog = () => {
  reviewDialogVisible.value = true
}


// 新增分享相关数据
const shareDialogVisible = ref(false)

const shareForm = ref({
  pushId: 0,
  goodsId: 0,
  pushName: '',
  pushSchool: '',
  pushContent: '',
  picUrlTemp: null,
  picUrl: null
})

const authStore = useAuthStore();

// 提交分享
const handleShareSubmit = () => {
  if (!shareForm.value.pushContent.trim()) {
    return ElMessage.error('请填写分享内容')
  }
  let picList = shareForm.value.picUrlTemp;
  shareForm.value.picUrl = picList.split(',', 3)
  if (!shareForm.value.picUrl) {
    return ElMessage.error('请填写分享图片')
  }

  shareForm.value.goodsId = goodsId.value;
  shareForm.value.pushId = authStore.userInfo.userId;
  shareForm.value.pushName = authStore.userInfo.userName;
  shareForm.value.pushSchool = authStore.userInfo.schoolName;

  addCommunity(shareForm.value).then(res => {
    if (res.code === 200) {
      ElMessage.success('分享成功');
    } else {
      ElMessage.error(res.message || '分享失败');
    }
  })

  shareDialogVisible.value = false
  shareForm.value.pushContent = ''
  shareForm.value.picUrlTemp = null
  shareForm.value.picUrl = null
}

onMounted(() => {
  initGoodsDetail();
})

const initGoodsDetail = () => {
  getGoodsDetail(goodsId.value).then(res => {
    goodsDetail.value = res.data
    specData.value = res.data.mallGoodsSpecList
    reviews.value = res.data.mallGoodReviewList.map(item => ({
      id: item.id,
      name: item.reviewUserName,
      avatar: import.meta.env.VITE_API_BASEURL + item.reviewUserUrl,
      rating: item.score,
      content: item.content,
      time: formatTime(item.reviewDate)
    }))
  }).catch(error => {
    console.error('Error fetching goods detail:', error)
    ElMessage.error('获取商品详情失败，请重试')
  });
}

const handleClose = () => {
  reviewDialogVisible.value = false
}

// 提交
const submitReview = () => {

  if (!reviewForm.value.rating) {
    return ElMessage.error('请选择评分')
  }

  if (!reviewForm.value.content) {
    return ElMessage.error('请填写评价')
  }

  let dataForm = {
    goodId: goodsId.value,
    score: reviewForm.value.rating,
    content: reviewForm.value.content,
  }
  addView(dataForm).then(res => {
    if (200 == res.code) {
      ElMessage.success('评价成功')
      reviewDialogVisible.value = false

      // 回显
      initGoodsDetail()
    }
  })



  reviewForm.value.rating = ''
  reviewForm.value.content = ''
}

const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  return isNaN(date.getTime())
      ? timeStr
      : date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      })
}

</script>

<style scoped>
.detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.detail-image {
  width: 100%;
  height: 500px;
  object-fit: cover;
  border-radius: 8px;
}

.goods-title {
  font-size: 28px;
  margin-bottom: 20px;
}

.price-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.current-price {
  color: #f56c6c;
  font-size: 32px;
  margin-right: 20px;
}

.sales {
  color: #999;
}

.actions {
  margin: 30px 0;
}

.description {
  line-height: 1.8;
  color: #666;
}

/* 分享按钮样式 */
.header-actions {
  position: absolute;
  right: 20px;
  top: 20px;
  z-index: 100;
}

/* 上传组件样式 */
:deep(.el-upload) {
  border: 1px dashed var(--tb-orange);

  &:hover {
    border-color: #FF8833;
  }
}

:deep(.el-upload-list__item) {
  transition: all 0.3s;

  &:hover {
    border-color: var(--tb-orange);
  }
}

/* 新增主题变量 */
.detail-container {
  --tb-orange: #FF6A00;
  --tb-orange-light: #FFF7EB;
  --el-color-primary: var(--tb-orange);
}

.price-section {
  .current-price {
    color: var(--tb-orange);
  }

  .sales {
    color: var(--el-text-color-secondary);
  }
}

/* 修改按钮组样式 */
.actions {
  .el-button--primary {
    background-color: var(--tb-orange);
    border-color: var(--tb-orange);

    &:hover {
      background-color: #FF8833;
      border-color: #FF8833;
    }
  }
}

/* 调整标签页激活状态 */
:deep(.detail-tabs) {
  .el-tabs__item.is-active {
    color: var(--tb-orange);
  }

  .el-tabs__active-bar {
    background-color: var(--tb-orange);
  }
}

/* 修改评价组件 */
.review-item {
  border-bottom: 1px solid var(--el-border-color-light);
  padding: 16px 0;

  .el-avatar {
    border: 2px solid var(--tb-orange-light);
  }

  .el-rate {
    --el-rate-fill-color: var(--tb-orange);
  }

  &:hover {
    background-color: var(--tb-orange-light);
  }
}

.add-review {
  margin-top: 20px;
  background-color: var(--tb-orange-light);
  color: var(--tb-orange);
  border-color: var(--tb-orange-light);

  &:hover {
    background-color: var(--tb-orange);
    color: white;
  }
}

.article-content {
  overflow: hidden;
  max-width: 100%;

  &:deep(img) {
    width: 600px !important;
    height: auto !important;
    max-width: 100%;
    object-fit: cover;
    border-radius: 8px;
  }
}
</style>
