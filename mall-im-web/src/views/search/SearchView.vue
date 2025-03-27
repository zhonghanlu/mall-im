<template>
  <div class="search-container">
    <el-card class="search-results">
      <h3 class="search-title">"{{ route.query.q }}" 的搜索结果</h3>

      <el-row :gutter="20" v-loading="loading">
        <el-col
            v-for="product in productList"
            :key="product.id"
            :xs="12"
            :sm="8"
            :md="8"
            class="product-card"
        >
          <el-card
              :body-style="{ padding: '15px' }"
              shadow="hover"
              class="card-item"
          >
            <el-image
                :src="product.goodsFaceUrl"
                fit="cover"
                class="product-image"
            />
            <div class="product-info">
              <h4 class="product-name">{{ product.goodsName }}</h4>
              <div class="price-section">
                <span class="original-price">原价：¥{{ product.oldPrice.toFixed(2) }}</span>
                <span class="current-price">现价：¥{{ (product.newPrice).toFixed(2) }}</span>
                <span class="sales">库存 {{ product.goodsStock }}</span>
              </div>
              <div class="actions">
                <el-button
                    type="primary"
                    size="small"
                    @click="viewDetail(product.id)"
                >查看详情
                </el-button>
                <el-button
                    type="success"
                    size="small"
                    @click="handleBuy(product)"
                >立即购买
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, onUnmounted, watch} from 'vue'
import {useRoute} from 'vue-router'
import {ElMessage} from "element-plus";
import {getGoodsList} from "@/api/goods.ts";
import {useRouter} from 'vue-router';

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const productList = ref<Product[]>([])
const searchKeyword = ref<string>(route.query.q as string || '')

interface Product {
  id: number
  goodsName: string
  newPrice: number
  oldPrice: number
  goodsFaceUrl: string
  goodsStock: number
}

onMounted(async () => {
  try {
    await getSearchResult();
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  // 清空搜索条件和产品列表
  searchKeyword.value = ''
  productList.value = []
})

watch(
    () => route.query.q,
    async (newKeyword) => {
      console.log(newKeyword)
      loading.value = true
      try {
        searchKeyword.value = newKeyword as string || ''
        await getSearchResult();
      } catch (error) {
        console.error('搜索失败:', error)
      } finally {
        loading.value = false
      }
    },
    {immediate: true} // 立即执行一次
)

const getSearchResult = async () => {
  const response = await getGoodsList({
    goodsName: route.query.q,
  });
  console.log(response.rows);
  productList.value = response.rows.map(item => ({
    ...item,
    goodsFaceUrl: import.meta.env.VITE_API_BASEURL + item.goodsFaceUrl,
  }));
}

const viewDetail = (productId: number) => {
  window.open(`/detail/${productId}`, '_blank')
}

const handleBuy = (goods: any) => {
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
.search-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-title {
  margin-bottom: 20px;
  color: #303133;
  font-size: 18px;
}

.search-results {
  padding: 20px;

  .product-card {
    margin-bottom: 20px;
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-5px);
    }
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
      font-size: 16px;
      color: #67c23a;
      font-weight: 600;
    }
  }

  .sales {
    color: #999;
    font-size: 12px;
  }

  .actions {
    display: flex;
    justify-content: space-between;
    gap: 8px;
  }
}

.price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 500;
}
</style>
