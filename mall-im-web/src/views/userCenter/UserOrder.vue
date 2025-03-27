<template>
  <div class="user-order">
    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="id" label="订单号" width="180"/>
      <el-table-column prop="orderCreateTime" label="日期" width="180"/>
      <el-table-column prop="payAmount" label="金额"/>
      <el-table-column label="状态">
        <template #default="scope">
          <el-tag>
            {{ statusType(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
<!--      <el-table-column label="操作">-->
<!--        <template #default="scope">-->
<!--          <el-button type="primary" @click="handleEvaluate(scope.row.id)">-->
<!--            去评价-->
<!--          </el-button>-->
<!--        </template>-->
<!--      </el-table-column>-->
    </el-table>

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
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {orderList} from "@/api/order.ts";

const reviewDialogVisible = ref(false)
const reviewForm = ref({})

const orders = ref([
  {id: '20230821001', orderCreateTime: '2023-08-21', payAmount: 158, orderStatus: '已发货'},
])

onMounted(() => {
  getOrderList();
})

const getOrderList = () => {
  orderList().then(res => {
    orders.value = res.rows;
  })
}

const handleEvaluate = (id: string) => {
  reviewDialogVisible.value = true
}

const handleClose = () => {
  reviewDialogVisible.value = false
}

// 提交
const submitReview = () => {

}


const statusType = (status: string) => {
  switch (status) {
    case "1":
      return '待付款';
    case "2":
      return '待发货';
    case "3":
      return '待收货';
    case "4":
      return '待评价';
    case "5":
      return '已完成';
    case "6":
      return '已关闭';
    case "7":
      return '已退款';
  }
}
</script>
