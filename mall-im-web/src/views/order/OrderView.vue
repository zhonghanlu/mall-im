<template>
  <div class="order-container">
    <el-steps :active="activeStep" align-center class="steps">
      <el-step title="确认订单"/>
      <el-step title="支付"/>
      <el-step title="完成"/>
    </el-steps>

    <el-form ref="formRef" :model="form" label-width="120px" v-show="activeStep === 0">
      <el-form-item label="收货地址" prop="address">
        <el-select v-model="form.address" placeholder="请选择收货地址">
          <el-option
              v-for="item in addressList"
              :key="item.id"
              :label="item.userName + ' ' + item.phone + ' ' + item.address"
              :value="item"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="商品信息" prop="goods">
        <el-table :data="goodsList" class="goods-table">
          <el-table-column prop="goodsName" label="商品名称"/>
          <el-table-column prop="goodsPrice" label="单价" width="120">
            <template #default="scope">¥{{ scope.row.goodsPrice }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="120"/>
          <el-table-column label="小计" width="120">
            <template #default="scope">¥{{ scope.row.quantity * scope.row.goodsPrice }}</template>
          </el-table-column>
        </el-table>

      </el-form-item>
      <el-form-item label="支付方式" prop="payment">
        <el-radio-group v-model="form.payment">
          <el-radio label="alipay">支付宝</el-radio>
          <el-radio disabled label="wechat">微信支付</el-radio>
        </el-radio-group>
      </el-form-item>
      <div class="total-amount">
        总金额：¥{{ totalAmount }}
      </div>
      <el-form-item>
        <el-button type="primary" @click="submitOrder">提交订单</el-button>
      </el-form-item>
    </el-form>

    <div v-show="activeStep === 1" class="payment-box">
      <h3>请扫码完成支付</h3>
      <el-button type="success" @click="completePayment">已完成支付</el-button>
    </div>

    <div v-show="activeStep === 2" class="success-box">
      <el-result icon="success" title="支付成功" sub-title="订单将在24小时内发货">
        <template #extra>
          <el-button type="primary" @click="backToHome">返回首页</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useCartStore} from '@/stores/cart'
import {addOrder} from "@/api/order.ts";
import {ElMessage} from "element-plus";
import {alipay} from "@/api/alipay.ts";
import {getAddressList} from "@/api/address.ts";

const router = useRouter()
const route = useRoute()

const cartStore = useCartStore()
const activeStep = ref(0)
const form = ref({
  address: null,
  payment: 'alipay'
})

const addressList = ref([])
const goodsList = ref()

onMounted(() => {
  console.log(route.query.goodsList)
  if (route.query.goodsList) {
    goodsList.value = JSON.parse(route.query.goodsList)
  }

  console.log(route.query.activeStep)

  if (route.query.activeStep) {
    activeStep.value = Number(route.query.activeStep)
  }

  getAddressList().then(res => {
    if (200 === res.code) {
      addressList.value = res.data
    }
  })
})

const totalAmount = computed(() => {
  if (goodsList.value)
    return (goodsList.value.reduce((sum, item) => sum + item.goodsPrice * item.quantity, 0)).toFixed(2)
})

// 提交订单
const submitOrder = () => {

  if (!form.value.address) {
    ElMessage.error("请选择收货地址")
  }

  let orderForm = {
    receiver: form.value.address.userName,
    receiverPhone: form.value.address.phone,
    receiverAddress: form.value.address.address,
    payAmount: totalAmount.value,
    payMethod: '2',
    mallOrderGoodsList: goodsList.value.map(item => ({
      goodsId: item.goodsId,
      title: item.goodsName,
      price: item.goodsPrice,
      value: item.quantity
    })),
  }

  addOrder(orderForm).then(res => {
    if (200 === res.code) {
      // alipay(res.data.orderId)
      window.open(import.meta.env.VITE_API_BASEURL + '/alipay/pay?orderId=' + res.data)
    }
    activeStep.value = 1
  }).catch(err => {
    ElMessage.error("订单创建失败" + err.message)
  })

}

const completePayment = () => {
  activeStep.value = 2
}

const backToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.order-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.steps {
  margin-bottom: 40px;
}

.payment-box {
  text-align: center;
}

.qrcode {
  width: 200px;
  margin: 20px 0;
}

.success-box {
  text-align: center;
}

.goods-table {
  margin-bottom: 20px;
}

.total-amount {
  text-align: right;
  font-size: 16px;
  font-weight: bold;
  margin-top: 20px;
}
</style>
