<template>
  <div class="vehicle-plate-page">
    <!-- 顶部导航栏 - 包含返回键 -->
    <van-nav-bar
      title="余额管理"
      left-text="返回"
      left-arrow
      @click-left="goBack"
    />
    <!-- 余额卡片 -->
    <div class="balance-card-container">
      <div class="balance-card">
        <div class="balance-header">
          <span class="balance-label">当前余额</span>
          <van-button
            prop="name"
            size="small"
            type="primary"
            plain
            class="recharge-btn"
            @click="handleRecharge"
          >
            充值
          </van-button>
        </div>
        <div class="balance-amount">¥{{ balance.toFixed(2) }}</div>
      </div>
    </div>

    <!-- 充值记录区域 -->
    <div class="recharge-history-container">
      <div class="list-header">
        <h2>充值记录</h2>
      </div>

      <!-- 充值记录列表 -->
      <div class="history-list">
        <div
          v-for="(record, index) in rechargeRecords"
          :key="index"
          class="history-item"
        >
          <div class="record-info">
            <div class="record-title">充值 {{ record.amount }}元</div>
            <div class="record-status">{{ record.status }}</div>
          </div>
          <div class="record-time">{{ record.time }}</div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="rechargeRecords.length === 0" class="empty-state">
        <van-empty image="search" description="暂无充值记录">
          <van-button round type="primary" @click="handleRecharge">
            立即充值
          </van-button>
        </van-empty>
      </div>

      <!-- 没有更多数据提示 -->
      <div v-if="rechargeRecords.length > 0" class="no-more-data">
        没有更多数据
      </div>
    </div>
    <!-- 底部导航栏 -->
    <van-tabbar v-model="active" class="tabbar">
      <van-tabbar-item icon="wap-home-o" text="首页" />
      <van-tabbar-item icon="logistics" to="/car" text="座驾" />
      <van-tabbar-item icon="records-o" to="/order" text="订单" />
      <van-tabbar-item icon="friends-o" to="/PersonalCenter" text="个人中心" />
    </van-tabbar>
  </div>
</template>

<script>
import { payOrder, getBalance } from "@/api/login/order.js";
import { getInfo } from "@/api/login/index";
import { NavBar, Button, Empty, Toast } from "vant";

export default {
  name: "BalanceManagementPage",
  components: {
    [NavBar.name]: NavBar,
    [Button.name]: Button,
    [Empty.name]: Empty,
  },
  data() {
    return {
      // 余额信息
      balance: 1250.5,
      sms: 100,
      name:"",

      // 充值记录相关
      rechargeRecords: [
        { amount: 100, status: "成功", time: "2023-10-15 14:30" },
        { amount: 200, status: "成功", time: "2023-09-20 10:15" },
        { amount: 500, status: "成功", time: "2023-08-05 16:45" },
      ],
    };
  },
  mounted() {
    this.loadUserInfo();
    this.loadLatestBalance();
  },
  methods: {
    // 返回首页
    goBack() {
      this.$router.push("/home");
    },

    loadUserInfo() {
      getInfo().then((res) => {
        console.log(res);
      });
    },

    // 加载最新余额
    loadLatestBalance() {
      const userId = localStorage.getItem("userId");
      getBalance(userId)
        .then((res) => {
          if (res && res.data && res.data.account) {
            this.balance = parseFloat(res.data.account);
          }
        })
        .catch((err) => {
          console.error("获取余额失败：", err);
          Toast.fail("获取余额失败，请重试");
        });
    },

    // 充值余额
    handleRecharge() {
      const trademo = {
        tradeNO: localStorage.getItem("userId"),
        tradePrice: this.sms,
        tradeName: "余额充值",
        tradeMsg: "支付宝在线支付",
         type: 1, // 充值场景标识，后端依赖该字段
          orderId: null // 充值无订单ID，传null（后端会生成交易号）
          

      };

      payOrder(trademo)
        .then((res) => {
          const payWindow = window.open("", "_blank");
          payWindow.document.write(res);
          payWindow.document.close();

          // 监听支付窗口关闭，请求最新余额
          const checkWindow = setInterval(() => {
            if (payWindow.closed) {
              clearInterval(checkWindow);
              this.loadLatestBalance();
              // 添加新的充值记录
              this.rechargeRecords.unshift({
                amount: this.sms,
                status: "成功",
                time: new Date().toLocaleString(),
              });
              Toast.success("充值成功");
            }
          }, 500);
        })
        .catch((error) => {
          console.error("充值请求失败：", error);
          Toast.fail("充值失败，请稍后再试");
        });
    },
  },
};
</script>

<style scoped>
.vehicle-plate-page {
  background-color: #f8f9fa;
  min-height: 100vh;
  padding-top: 46px; /* 为固定导航栏留出空间 */
}

/* 导航栏样式 - 确保返回键可见 */
.van-nav-bar {
  background: #1989fa !important;
  color: #fff !important;
  border-bottom: none !important;
}

.van-nav-bar__title {
  color: #fff !important;
  font-size: 17px;
  font-weight: 600;
}

.van-nav-bar__left {
  padding: 0 16px;
}

.van-nav-bar__text {
  color: #fff !important;
  font-size: 16px;
}

.van-nav-bar .van-icon {
  color: #fff !important;
  font-size: 18px;
}

/* 确保返回箭头和文字都显示 */
.van-nav-bar__arrow {
  margin-right: 4px;
}

/* 余额卡片样式 */
.balance-card-container {
  padding: 20px 15px;
}

.balance-card {
  background: linear-gradient(135deg, #409eff 0%, #1989fa 100%);
  color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 6px 16px rgba(25, 137, 250, 0.2);
}

.balance-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.balance-label {
  font-size: 16px;
  font-weight: 500;
}

.recharge-btn {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
}

.recharge-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

.balance-amount {
  font-size: 36px;
  font-weight: bold;
  letter-spacing: 1px;
}

/* 充值记录样式 */
.recharge-history-container {
  background-color: white;
  border-radius: 16px 16px 0 0;
  min-height: calc(100vh - 200px);
}

.list-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.list-header h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

/* 充值记录列表样式 */
.history-list {
  padding: 0 20px;
}

.history-item {
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-item:last-child {
  border-bottom: none;
}

.record-info {
  flex: 1;
}

.record-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.record-status {
  font-size: 14px;
  color: #07c160;
  font-weight: 500;
}

.record-time {
  font-size: 13px;
  color: #999;
}

/* 空状态样式 */
.empty-state {
  padding: 40px 20px;
}

/* 没有更多数据提示 */
.no-more-data {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
  border-top: 1px solid #f5f5f5;
}
/* 底部导航栏样式 */
.tabbar {
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}
</style>