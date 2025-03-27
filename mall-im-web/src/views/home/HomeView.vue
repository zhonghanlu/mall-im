<template>
  <div class="home-container">
    <el-row :gutter="20">
      <!-- 左侧分类 -->
      <el-col :span="5" :xs="24" :sm="6" :md="5" class="left-aside">
        <el-card class="ad-card">
          <el-carousel height="200px">
            <el-carousel-item v-for="item in advertList" :key="item">
              <image-preview :src="item.advertUrl"/>
            </el-carousel-item>
          </el-carousel>
        </el-card>

        <el-card class="category-card">
          <div class="card-header">
            <el-icon>
              <Menu/>
            </el-icon>
            <span>商品分类</span>
          </div>
          <el-tree
              :data="categoryTree"
              node-key="id"
              default-expand-all
              :props="defaultProps"
              class="category-tree"
              @node-click="handleNodeClick"
          />
        </el-card>
      </el-col>

      <!-- 主内容区 -->
      <el-col :span="14" :xs="24" :sm="18" :md="14" class="main-content">
        <!-- 分类导航 -->
        <el-tabs v-model="activeTab" class="category-tabs" sticky>
          <el-tab-pane name="recommend">
            <template #label>
              <el-icon>
                <Star/>
              </el-icon>
              推荐
            </template>
          </el-tab-pane>
          <el-tab-pane name="school">
            <template #label>
              <el-icon>
                <School/>
              </el-icon>
              校园推荐
            </template>
          </el-tab-pane>
          <el-tab-pane name="hot">
            <template #label>
              <el-icon>
                <Sunrise/>
              </el-icon>
              热卖商品
            </template>
          </el-tab-pane>
        </el-tabs>
        <el-row
            v-infinite-scroll="loadMore"
            :infinite-scroll-disabled="loading || !hasMore"
            :infinite-scroll-distance="10"
            :gutter="20" class="product-list">
          <el-col
              v-for="goods in goodsList"
              :key="goods.id"
              :xs="12"
              :sm="8"
              :md="8"
              class="product-card"
          >
            <el-card
                :body-style="{ padding: '15px' }"
                shadow="hover"
                class="card-item"
                @click="goToDetail(goods)"
            >
              <image-preview style="height: 150px;width: 260px" :src="goods.goodsFaceUrl"/>

              <div class="product-info">
                <span class="product-name">{{ goods.goodsName }}</span>
                <div class="price-section">
                  <span class="original-price">原价：¥{{ goods.oldPrice.toFixed(2) }}</span>
                  <span class="current-price">现价：¥{{ (goods.newPrice * 0.9).toFixed(2) }}</span>
                  <span class="sales" style="color:darkgrey;font-size: 13px;">库存：{{ goods.goodsStock }}</span>
                </div>
                <div class="actions">
                  <el-button type="primary" size="small" @click.stop="handleBuy(goods)">立即购买</el-button>
                  <el-button type="success" size="small" @click.stop="addToCart(goods)">加入购物车</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>

      <!-- 右侧热销栏 -->
      <el-col :span="5" class="right-aside">
        <el-card class="hot-goods-card">
          <div class="card-header">
            <el-icon>
              <Fire/>
            </el-icon>
            <span>智能推荐</span>
          </div>
          <!-- 右侧热销内容 -->
          <div class="hot-goods-list">
            <div v-for="(item,index) in hotGoods" :key="index" class="hot-item" @click="goToDetail(item)">
              <img :src="item.goodsFaceUrl" class="hot-image"/>
              <div class="hot-info">
                <div class="hot-title">{{ item.goodsName }}</div>
                <div class="hot-price">¥{{ item.newPrice }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'
import {ElMessage} from 'element-plus'
import {Star, School, Sunrise} from '@element-plus/icons-vue'
import {useCartStore} from '@/stores/cart'
import {useRouter} from 'vue-router';
import {getGoodsList, recommended} from "@/api/goods.ts";
import {getCategoryList} from "@/api/category.ts";
import {getAdvertList} from "@/api/advert.ts";
import ImagePreview from "@/components/imgPreview.vue";

interface Goods {
  id: number
  goodsName: string
  newPrice: number
  oldPrice: number
  goodsFaceUrl: string
  goodsStock: number
}

const cartStore = useCartStore()
cartStore.loadCart();
const loading = ref(false)
const hasMore = ref(true)
const activeTab = ref('recommend')
const currentPage = ref({
  pageNum: 1,
  pageSize: 6
}) // 添加当前页码

const queryParams = ref({
  goodsClassify: '',
  searchFlag: 1
})

const categoryTree = ref([
  {
    id: 1,
    label: '学习用品',
    icon: 'Notebook',
    children: [
      {id: 11, label: '教材', icon: 'Notebook'},
      {id: 12, label: '文具'}
    ]
  }
])

const defaultProps = {
  children: 'children',
  label: 'classifyName'
}

const advertList = ref([{
  id: 0,
  advertUrl: '',
}])

const goodsList = ref<Goods[]>([])

// 热销商品筛选
const hotGoods = ref([])

onMounted(() => {
  advert0List();
  categoryTreeList();
  initRecommended();
  loadMore();
});

// 广告
const advert0List = () => {
  getAdvertList(0).then(res => {
    advertList.value = res.rows
  });
}

// 分类
const categoryTreeList = () => {
  getCategoryList().then(res => {
    categoryTree.value = generateTree(res.data)
  })
}

// 智能推荐
const initRecommended = () => {
  recommended().then(res => {
    console.log(res.data)
    hotGoods.value = res.data.map(item => ({
      ...item,
      goodsFaceUrl: import.meta.env.VITE_API_BASEURL + item.goodsFaceUrl,
    }))

    // 展示前五条
    if (hotGoods.value.length > 5) {
      hotGoods.value = hotGoods.value.slice(0, 5);
    }
  })
}

const handleNodeClick = (val) => {
  goodsList.value = []
  hasMore.value = true
  currentPage.value.pageNum = 1
  currentPage.value.pageNum = 6
  queryParams.value.goodsClassify = val.classifyName
  loadMore()
  console.log('点击了分类')
}

const generateTree = (list: any, parentId = 0) => {
  const tree = []
  for (let item of list) {
    if (item.parentId === parentId) {
      const children = generateTree(list, item.id)
      if (children.length > 0) {
        item.children = children
      }
      tree.push(item)
    }
  }
  return tree
}

const searchFlagMap = {
  recommend: 1,
  school: 2,
  hot: 3,
}

watch(
    activeTab,
    async (newTab) => {
      console.log('Tab changed to:', newTab)
      const newSearchFlag = searchFlagMap[newTab] || queryParams.value.searchFlag
      if (newSearchFlag !== queryParams.value.searchFlag) {
        queryParams.value.searchFlag = newSearchFlag
        currentPage.value.pageSize = 6
        currentPage.value.pageNum = 1
        goodsList.value = []
        hasMore.value = true
        loading.value = false
        console.log('Updated searchFlag:', queryParams.value.searchFlag)
        try {
          await loadMore();
        } catch (error) {
          console.error('获取商品列表失败:', error)
          ElMessage.error('获取商品列表失败')
        } finally {
          loading.value = false
        }
      }
    },
    {immediate: true} // 立即执行一次
)

const loadMore = async () => {
  console.log('loadMore called')
  console.log(hasMore.value)
  console.log(loading.value)
  if (!hasMore.value || loading.value) return

  loading.value = true
  try {
    const res = await getGoodsList({
      pageNum: currentPage.value.pageNum,
      pageSize: currentPage.value.pageSize,
      goodsClassify: queryParams.value.goodsClassify,
      searchFlag: queryParams.value.searchFlag
    })

    goodsList.value.push(...res.rows)
    // 根据总记录数和当前加载数量判断是否还有更多
    hasMore.value = (currentPage.value.pageNum * currentPage.value.pageSize) < res.total
    currentPage.value.pageNum++
  } catch (err) {
    ElMessage.error('加载失败，请稍后重试')
    console.error('加载商品列表失败:', err)
  } finally {
    loading.value = false
  }
}


const router = useRouter()

const goToDetail = (goods: Goods) => {
  router.push(`/detail/${goods.id}`)
}

const addToCart = (goods: Goods) => {

  console.log(goods)

  const {id, goodsName, newPrice} = goods
  cartStore.addItem({id, goodsName, newPrice})
  ElMessage.success('已加入购物车')
}

const handleBuy = (goods: Goods) => {
// 立即购买逻辑
  const goodsList = [{
    goodsId: goods.id,
    goodsName: goods.goodsName,
    goodsPrice: goods.newPrice,
    quantity: 1,
    totalPrice: goods.newPrice
  }];
  router.push({
    name: 'order',
    query: {
      goodsList: JSON.stringify(goodsList)
    }
  })
}
</script>

<style scoped>
.home-container {
  padding: 20px;
  position: relative;
}

/* 分类标签页样式 */
.category-tabs {
  --tab-height: 48px;
  margin-bottom: 20px;
  position: sticky;
  top: 80px;
  z-index: 1001;
  background: var(--el-bg-color);
  backdrop-filter: blur(5px);

  :deep(.el-tabs__header) {
    margin: 0;
    height: var(--tab-height);
  }

  :deep(.el-tabs__nav-wrap) {
    padding: 0 30px;
    background: var(--el-bg-color);
    border-radius: 8px;
    box-shadow: 0 2px 8px var(--el-box-shadow-light);
  }

  :deep(.el-tabs__item) {
    height: var(--tab-height);
    padding: 0 20px !important;
    font-size: 15px;
    color: var(--el-text-color-regular);
    transition: all 0.3s var(--el-transition-function-ease-in-out-bezier);

    .el-icon {
      vertical-align: middle;
      margin-right: 8px;
      font-size: 18px;
    }

    &:hover {
      color: blue;
      transform: translateY(-2px);
    }

    &.is-active {
      color: blue;
      font-weight: 600;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        width: 40%;
        height: 2px;
        background: var(--el-color-primary);
        transform: translateX(-50%);
      }
    }
  }

  :deep(.el-tabs__active-bar) {
    display: none;
  }
}

/* 左侧分类栏 */
.home-container .el-col.left-aside {
  position: sticky;
  top: 80px;
  height: calc(100vh - 100px);
  overflow-y: auto;
  z-index: 1000;
  flex: 0 0 20% !important;
  max-width: 20% !important;
  width: 20% !important;
  margin-right: 0px;
  transition: all 0.3s;

  .el-card {
    margin-bottom: 16px;
  }
}

/* 主内容区 */
.main-content {
  margin: 0 20px;
  flex: 1;
  min-width: 0;
}

/* 右侧边栏 */
.right-aside {
  position: sticky;
  top: 80px;
  height: calc(100vh - 100px);
  overflow-y: auto;
  z-index: 1000;
  flex: 0 0 20% !important;
  max-width: 20% !important;
  width: 20% !important;
  margin-left: 0px;
  background: var(--el-bg-color);
  backdrop-filter: blur(5px);
}

.goods-image {
  height: 200px;
  object-fit: cover;
  transition: transform 0.3s ease;

  &:hover {
    transform: scale(1.03);
  }
}

.price-section {
  display: flex;
  gap: 8px;
  align-items: baseline;
  margin: 12px 0;

  .original-price {
    font-size: 12px;
    color: #999;
    text-decoration: line-through;
  }

  .current-price {
    font-size: 18px;
    color: #f56c6c;
    font-weight: 600;
  }
}

/* 骨架屏样式 */
.skeleton-container {
  padding: $--md;

  .el-skeleton {
    padding: $--md;
    background: white;
    border-radius: 4px;
    margin-bottom: $--md;
  }
}

/* 商品图片 */
.goods-image {
  width: 100%;
  aspect-ratio: 16/9;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

/* 价格区域 */
.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;

  .current-price {
    color: var(--el-color-primary);
    font-size: 18px;
    font-weight: 600;
  }
}


/* 右侧热销商品样式 */
.hot-goods-card {
  .card-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid var(--el-border-color-light);

    .el-icon {
      margin-right: 8px;
      font-size: 18px;
      color: var(--el-color-primary);
    }

    span {
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
}

.hot-goods-list {
  padding: 12px;

  .hot-item {
    display: flex;
    align-items: center;
    padding: 12px;
    margin-bottom: 10px;
    border-radius: 8px;
    transition: all 0.3s;
    cursor: pointer;
    background: var(--el-bg-color);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }

    .hot-image {
      width: 80px;
      height: 80px;
      border-radius: 6px;
      object-fit: cover;
      flex-shrink: 0;
      margin-right: 12px;
    }

    .hot-info {
      flex: 1;
      min-width: 0;

      .hot-title {
        font-size: 14px;
        color: var(--el-text-color-primary);
        @include text-ellipsis(2);
        line-height: 1.4;
        margin-bottom: 6px;
      }

      .hot-price {
        font-size: 16px;
        color: var(--el-color-primary);
        font-weight: 600;
      }
    }

    &:last-child {
      margin-bottom: 0;
    }
  }
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .home-container .el-col.left-aside {
    position: static;
    flex: 0 0 100% !important;
    max-width: 100% !important;
    width: 100% !important;
    margin-right: 0;
    height: auto;
  }

  .main-content {
    margin: 20px 0;
  }

  .right-aside {
    display: none;
  }
}

@media (max-width: 768px) {
  .category-tabs {
    top: 60px;
  }
}

/* 新增全局主题色 */
.home-container {
  --tb-orange: #FF6A00; /* 淘宝主橙色 */
  --tb-orange-light: #FFF7EB; /* 浅色背景 */
}

/* 分类标签页激活状态 */
.category-tabs {
  :deep(.el-tabs__item) {
    &.is-active::after {
      background: var(--tb-orange);
    }
  }
}

/* 价格显示 */
.price-section .current-price {
  color: var(--tb-orange);
}

/* 右侧热销商品卡片 */
.hot-goods-card {
  .card-header {
    .el-icon {
      color: var(--tb-orange);
    }
  }

  .hot-price {
    color: var(--tb-orange);
  }
}

/* 商品卡片样式 */
.card-item {
  margin-bottom: 20px; /* 添加间距 */
}

.product-image {
  width: 100%;
  height: 200px;
  border-radius: 4px;
  margin-bottom: 12px;
}

.product-name {
  font-size: 14px;
  margin: 0 0 8px;
  color: #333;
  @include text-ellipsis(2);
}

/* 商品悬停效果 */
.hot-item:hover {
  box-shadow: 0 2px 12px rgba(255, 106, 0, 0.15);
}


</style>


