<template>
  <div class="cart-container">
    <el-table
        :data="cartStore.items"
        style="width: 100%"
        @selection-change="cartStore.setSelectedItems"
    >
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="goodsName" label="商品名称"/>
      <el-table-column prop="price" label="单价" width="120">
        <template #default="scope">
          ¥{{ scope.row.newPrice }}
        </template>
      </el-table-column>
      <el-table-column label="数量" width="150">
        <template #default="scope">
          <el-input-number
              v-model="scope.row.quantity"
              :min="1"
              @change="cartStore.updateQuantity(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="danger" @click="cartStore.removeItem(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="total-section">
      <span class="total-label">总金额：</span>
      <span class="total-amount">¥{{ selectedTotal }}</span>
      <el-button type="success" @click="goToOrder">去结算</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useCartStore} from '@/stores/cart'
import {useRouter} from 'vue-router'
import {onMounted, ref, watch} from "vue";

const cartStore = useCartStore()
cartStore.loadCart();
const router = useRouter()
const selectedTotal = ref(0)

onMounted(() => {
  cartStore.selectedItems = []
})

watch(
    () => cartStore.selectedItems,
    (selectedItems) => {
      selectedTotal.value = (selectedItems.reduce((total, item) => total + item.newPrice * item.quantity, 0)).toFixed(2)
    },
    {immediate: true, deep: true}
)

const goToOrder = () => {
  const goodsList = cartStore.selectedItems.map(item => ({
    goodsId: item.id,
    goodsName: item.goodsName,
    goodsPrice: item.newPrice,
    quantity: item.quantity,
    totalPrice: item.newPrice * item.quantity
  }));

  console.log(goodsList)

  router.push({
    name: 'order',
    query: {
      goodsList: JSON.stringify(goodsList)
    }
  })

  // 删除购物车数据
  goodsList.map(item => {
    cartStore.removeItem(item.goodsId)
  })
}
</script>

<style scoped>
.cart-container {
  padding: 20px;
}

.total-section {
  margin-top: 20px;
  text-align: right;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.total-label {
  font-size: 16px;
  margin-right: 10px;
}

.total-amount {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
  margin-right: 20px;
}
</style>
