<template>
  <div class="order-page">
    <!-- 顶部导航 -->
    <van-nav-bar
      title="我的订单"
      left-arrow
      @click-left="$router.back()"
      class="custom-nav-bar"
    />

    <!-- 订单状态筛选和日期查询 -->
    <div class="order-filter">
      <van-tabs
        v-model="activeTab"
        @change="handleTabChange"
        class="status-tabs"
        swipeable
      >
        <van-tab title="全部订单" name="all" />
        <van-tab title="未支付" name="unpaid" />
        <van-tab title="已支付" name="paid" />
      </van-tabs>

      <!-- 日期范围查询 -->
      <van-cell-group class="date-filter" border="false">
        <van-field
          label="日期范围"
          :value="dateRangeText"
          is-link
          @click="showDatePicker = true"
          placeholder="选择日期范围"
          class="date-field"
        />
      </van-cell-group>

      <!-- 日期选择器弹窗 -->
      <van-popup
        v-model="showDatePicker"
        position="bottom"
        closeable
        close-icon-position="top-right"
        class="date-popup"
      >
        <van-datetime-picker
          type="dateRange"
          title="选择订单日期范围"
          :min-date="minDate"
          :max-date="maxDate"
          v-model="dateRange"
          @confirm="onDateConfirm"
         @cancel="showDatePicker = false"
          :formatter="dateFormatter"
        />
      </van-popup>
    </div>

    <!-- 订单列表（带分页） -->
    <van-list
      v-model="loading"
      :finished="finished"
      finished-text="没有更多订单啦"
      @load="fetchOrders"
      class="order-list"
      immediate-check
      :offset="100"
    >
      <!-- 空状态 -->
      <div v-if="orders.length === 0 && !loading" class="empty-container">
        <van-empty
          :image="emptyImage"
          :description="emptyDescription"
          class="custom-empty"
        >
          <van-button
            round
            type="primary"
            size="small"
            @click="resetFilter"
            class="reset-btn"
          >
            重置筛选条件
          </van-button>
        </van-empty>
      </div>

      <!-- 订单卡片 -->
      <van-cell-group v-else border="false">
        <van-swipe-cell
          v-for="order in orders"
          :key="order.id"
          :right-width="65"
          class="order-card"
        >
          <van-cell is-link @click="gotoDetail(order.id)" class="order-cell">
            <!-- 订单头部信息 -->
            <div slot="title" class="order-header">
              <span class="order-id">订单号: {{ order.id }}</span>
              <van-tag
                :type="order.paymentStatus === 0 ? 'warning' : 'success'"
                class="order-status-tag"
              >
                {{ order.paymentStatus === 0 ? "未支付" : "已支付" }}
              </van-tag>
            </div>
            <!-- 订单详情信息 -->
            <div slot="label" class="order-detail">
              <div class="order-info">
                <span class="car-number"
                  >车牌号: {{ order.carNumber || "未知车牌" }}</span
                >
                <span class="parking-space"
                  >车位: {{ order.spaceNumber || "未知车位" }}</span
                >
              </div>
              <div class="order-time">
                入场时间: {{ formatTime(order.entryTime) }}
                <span v-if="order.exitTime" class="split-line">|</span>
                <span v-if="order.exitTime"
                  >出场时间: {{ formatTime(order.exitTime) }}</span
                >
              </div>
            </div>
            <!-- 订单金额和操作 -->
            <div class="order-footer">
              <span class="order-amount"
                >¥{{ (order.amount || 0).toFixed(2) }}</span
              >
              <van-button
                v-if="order.paymentStatus === 0"
                type="primary"
                size="small"
                @click.stop="payOrder(order)"
                class="pay-btn"
              >
                立即支付
              </van-button>
            </div>
          </van-cell>
          <!-- 删除按钮 -->
          <van-button
            slot="right"
            type="danger"
            icon="delete"
            @click="deleteOrder(order.id)"
            class="delete-btn"
          />
        </van-swipe-cell>
      </van-cell-group>
    </van-list>

    <!-- 底部导航栏 -->
    <van-tabbar v-model="active" class="tabbar">
      <van-tabbar-item icon="wap-home-o" text="首页" />
      <van-tabbar-item icon="logistics" to="/car" text="座驾" />
      <van-tabbar-item icon="records-o" to="/order" text="订单" />
      <van-tabbar-item
        icon="friends-o"
        to="/PersonalCenter"
        text="个人中心"
      />
    </van-tabbar>

    <!-- 支付成功提示 -->
    <van-toast
      v-model="paySuccess"
      type="success"
      message="支付成功"
      duration="2000"
    />
    <!-- 删除确认对话框 -->
    <van-dialog
      v-model="deleteDialog"
      title="确认删除"
      message="确定要删除该订单吗？删除后不可恢复"
      @confirm="confirmDelete"
      @cancel="deleteDialog = false"
      confirm-button-color="#ff4d4f"
    />
  </div>
</template>

<script>
import {
  NavBar,
  Tabs,
  Tab,
  List,
  Empty,
  Cell,
  CellGroup,
  Tag,
  Button,
  Toast,
  SwipeCell,
  Popup,
  DatetimePicker,
  Dialog,
  Field,
  Tabbar,
  TabbarItem,
} from "vant";
import {
  fetchOrders,
  payOrder as apiPayOrder,
  deleteOrder as apiDeleteOrder,
  carExit as apiCarExit,
  getLatestUnpaidOrder as apiGetLatestUnpaidOrder,
} from "@/api/login/order";
import { formatDate } from "@/utils/date";

export default {
  name: "UserOrderList",
  components: {
    NavBar,
    Tabs,
    Tab,
    List,
    Empty,
    Cell,
    CellGroup,
    Tag,
    Button,
    Toast,
    SwipeCell,
    Popup,
    DatetimePicker,
    Dialog,
    Field,
    Tabbar,
    TabbarItem,
  },
  data() {
    return {
      // 订单相关
      activeTab: "all",
      orders: [],
      loading: false,
      finished: false,
      page: 1,
      pageSize: 10,
      total: 0,
      orderList: [],
      paySuccess: false,

      // 日期筛选相关
      showDatePicker: false,
      dateRange: [],
      dateRangeText: "",
      minDate: new Date(2020, 0, 1),
      maxDate: new Date(),
      beginTime: "",
      endTime: "",

      // 删除相关
      deleteDialog: false,
      currentDeleteId: null,

      // 空状态相关
      emptyImage: "https://img01.yzcdn.cn/vant/empty-order.png",
      emptyDescription: "暂无订单数据",

      // 底部导航
      active: 2,
    };
  },
  created() {
    this.fetchOrders();
  },
  methods: {
    /**
     * 拉取订单列表（核心：筛选+分页）
     */
    fetchOrders() {
      this.loading = true;

      // 构建请求参数，包含状态筛选、日期筛选和分页信息
      const params = {
        page: this.page,
        pageSize: this.pageSize,
        // 状态筛选逻辑：all不传递status，unpaid传递0，paid传递1
        status:
          this.activeTab === "all"
            ? undefined
            : this.activeTab === "unpaid"
            ? 0
            : 1,
        beginTime: this.beginTime,
        endTime: this.endTime,
      };

      fetchOrders(params)
        .then((res) => {
          if (!res || !res.rows || !Array.isArray(res.rows)) {
            throw new Error("订单数据格式错误");
          }

          this.total = res.total || 0;
          const newOrders = res.rows;

          // 分页数据处理：第一页直接替换，后续页追加
          if (this.page === 1) {
            this.orders = newOrders;
          } else {
            this.orders = [...this.orders, ...newOrders];
          }

          // 判断是否加载完毕（已加载数量 >= 总数量）
          this.finished = this.orders.length >= this.total;

          // 更新空状态描述
          this.updateEmptyDescription();

          // 准备下一页
          this.page++;
        })
        .catch((err) => {
          console.error("拉取订单失败:", err);
          Toast("拉取订单失败，请重试");
          this.finished = false; // 允许重新加载
        })
        .finally(() => {
          this.loading = false;
        });
    },

    /**
     * 切换状态标签
     */
    handleTabChange() {
      // 切换标签时重置日期筛选条件，避免干扰
      this.beginTime = "";
      this.endTime = "";
      this.dateRangeText = "";
      this.dateRange = [];

      // 重置列表并重新加载
      this.resetList();
      this.fetchOrders();
    },

    /**
     * 日期范围确认
     */
    onDateConfirm(date) {
      this.dateRange = date;
      this.showDatePicker = false;

      if (date && date.length === 2) {
        this.beginTime = formatDate(date[0], "yyyy-MM-dd");
        this.endTime = formatDate(date[1], "yyyy-MM-dd");
        this.dateRangeText = `${this.beginTime} 至 ${this.endTime}`;
      } else {
        this.beginTime = "";
        this.endTime = "";
        this.dateRangeText = "";
      }

      // 按新日期筛选重新加载
      this.resetList();
      this.fetchOrders();
    },

    /**
     * 重置列表状态
     */
    resetList() {
      this.page = 1;
      this.finished = false;
      this.orders = [];
    },

    /**
     * 重置所有筛选条件
     */
    resetFilter() {
      this.activeTab = "all";
      this.beginTime = "";
      this.endTime = "";
      this.dateRangeText = "";
      this.dateRange = [];
      this.resetList();
      this.fetchOrders();
    },

    /**
     * 更新空状态描述
     */
    updateEmptyDescription() {
      if (this.beginTime || this.endTime) {
        this.emptyDescription = "暂无匹配该日期范围的订单";
      } else if (this.activeTab === "unpaid") {
        this.emptyDescription = "暂无未支付订单";
      } else if (this.activeTab === "paid") {
        this.emptyDescription = "暂无已支付订单";
      } else {
        this.emptyDescription = "暂无订单数据";
      }
    },

    /**
     * 支付入口：先查最新订单 → 调用出场接口算金额 → 发起支付
     */
    payOrder(order) {
      console.log("当前订单数据:", order);
      // 基础校验：订单ID和车牌号必须有（车牌号用于查最新订单）
      if (!order || !order.id || !order.carNumber) {
        Toast("订单信息异常，无法支付");
        return;
      }

      // 步骤1：先查该车牌的"最新未支付订单"（避免列表中订单信息过时）
      Toast.loading({ message: "查询订单信息中...", forbidClick: true });
      apiGetLatestUnpaidOrder(order.carNumber)
        .then((res) => {
          const latestOrder = res.data; // 后端返回 {data: 订单对象}
          if (!latestOrder) {
            Toast("未找到有效的未支付订单");
            return;
          }

          // 步骤2：调用出场接口，让后端计算停车费用（核心：金额从0变成实际费用）
          this.callCarExitApi(latestOrder.id);
        })
        .catch((err) => {
          Toast.clear(); // 关闭loading
          console.error("查询最新订单失败:", err);
          Toast("查询订单失败，请重试");
        });
    },

    /**
     * 调用出场接口（后端计算费用并更新订单金额）
     */
    callCarExitApi(orderId) {
      Toast.loading({ message: "正在计算停车费用...", forbidClick: true });
      apiCarExit(orderId)
        .then((res) => {
          Toast.clear(); // 关闭loading
          const updatedOrder = res;// 后端返回 {data: 已更新金额的订单}

          // 校验费用是否正常（避免后端计算异常）
          if (updatedOrder.amount === undefined || updatedOrder.amount < 0) {
            Toast("费用计算失败，请重试");
            return;
          }

          // 特殊情况：免费（金额为0），直接提示成功
          if (updatedOrder.amount === 0) {
            Toast.success("停车费用已减免，无需支付");
            this.resetList(); // 刷新订单列表
            this.fetchOrders();
            this.paySuccess = true;
            return;
          }

          // 步骤3：费用正常，发起支付
          this.initiatePayment(updatedOrder);
        })
        .catch((err) => {
          Toast.clear(); // 关闭loading
          console.error("出场接口调用失败:", err);
          Toast("计算停车费用失败，请重试");
        });
    },

    /**
     * 发起支付（和原支付逻辑一致，但用的是"已更新金额的订单"）
     */
    initiatePayment(order) {
      const tradeDemo = {
        type: 2,
        orderId: order.id,
        tradeNo: `ORDER_${order.id}_${Date.now()}`, // 唯一交易号
        tradeName: "车辆出场订单",
        tradePrice: order.amount, // 这里用的是后端计算后的实际金额（不再是0）
        tradeMsg: `车牌号: ${order.carNumber || "未知车牌"}`,
      };

      apiPayOrder(tradeDemo)
        .then((res) => {
          const newWindow = window.open("", "_blank");
          if (!newWindow) {
            Toast("浏览器弹窗被拦截，请允许弹窗后重试");
            return;
          }

          // 后端返回支付页面HTML，写入新窗口
          newWindow.document.write(res.data || res); // 注意：如果后端直接返回HTML，用res或res.data（根据实际调整）
          newWindow.document.close();

          // 监听支付窗口关闭，刷新列表
          const checkWindowClosed = setInterval(() => {
            if (newWindow.closed) {
              clearInterval(checkWindowClosed);
              this.resetList();
              this.fetchOrders();
              this.paySuccess = true;
            }
          }, 1000);
        })
        .catch((err) => {
          console.error("支付请求失败:", err);
          Toast("支付请求失败，请重试");
        });
    },

    /**
     * 跳转到订单详情
     */
    gotoDetail(orderId) {
      if (!orderId) return;
      this.$router.push({
        path: "/order/detail",
        query: { id: orderId },
      });
    },

    /**
     * 格式化时间
     */
    formatTime(time) {
      if (!time) return "未知时间";
      return formatDate(new Date(time), "yyyy-MM-dd HH:mm:ss");
    },

    /**
     * 准备删除订单
     */
    deleteOrder(id) {
      this.currentDeleteId = id;
      this.deleteDialog = true;
    },

    /**
     * 确认删除订单
     */
    confirmDelete() {
      if (!this.currentDeleteId) return;

      apiDeleteOrder(this.currentDeleteId)
        .then(() => {
          Toast("删除成功");
          this.deleteDialog = false;
          this.resetList();
          this.fetchOrders();
        })
        .catch((err) => {
          console.error("删除订单失败:", err);
          Toast("删除失败，请重试");
          this.deleteDialog = false;
        });
    },

    /**
     * 日期格式化
     */
    dateFormatter(type, value) {
      if (type === "year") {
        return `${value}年`;
      }
      if (type === "month") {
        return `${value}月`;
      }
      if (type === "day") {
        return `${value}日`;
      }
      return value;
    },
  },
};
</script>

<style scoped>
.order-page {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-top: 46px;
  padding-bottom: 50px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica,
    Arial, sans-serif;
}

/* 导航栏样式优化 */
.custom-nav-bar {
  background: linear-gradient(135deg, #1677ff 0%, #0f52ba 100%);
  color: #ffffff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

/* 筛选区域样式优化 */
.order-filter {
  background-color: #fff;
  margin-bottom: 15px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* 状态标签样式优化 */
.status-tabs {
  --van-tab-active-text-color: #1677ff;
  --van-tab-active-border-color: #1677ff;
  --van-tab-text-color: #666;
  --van-tab-font-size: 15px;
  --van-tabs-bottom-bar-height: 3px;
  padding: 0 10px;
}

/* 日期筛选样式优化 */
.date-filter {
  padding: 0 15px 10px;
}

.date-field {
  --van-field-label-width: 100px;
  --van-field-input-text-align: right;
  --van-field-background: #f5f7fa;
  --van-field-border-radius: 8px;
  --van-field-height: 40px;
}

/* 日期弹窗样式 */
.date-popup {
  --van-popup-background: #fff;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
}

/* 列表区域样式 */
.order-list {
  padding: 0 15px;
  margin-bottom: 20px;
}

/* 空状态容器样式 */
.empty-container {
  padding: 60px 0;
  text-align: center;
}

.custom-empty {
  --van-empty-description-color: #8c8c8c;
  --van-empty-image-size: 120px;
}

.reset-btn {
  margin-top: 15px;
  background-color: #1677ff;
  border-color: #1677ff;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  background-color: #0f52ba;
  transform: translateY(-2px);
}

/* 订单卡片样式优化 */
.order-card {
  margin-bottom: 12px;
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.order-card:active {
  transform: translateY(1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.order-cell {
  padding: 16px 15px;
}

/* 订单头部样式 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.order-id {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.order-status-tag {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 20px;
}

/* 订单详情样式 */
.order-detail {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.order-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
}

.order-time {
  font-size: 13px;
  color: #999;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.split-line {
  color: #ddd;
}

/* 订单底部样式 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dashed #f0f0f0;
}

.order-amount {
  font-size: 17px;
  font-weight: 600;
  color: #ff4d4f;
  letter-spacing: 0.5px;
}

.pay-btn {
  padding: 6px 16px;
  font-size: 14px;
  border-radius: 20px;
  background-color: #1677ff;
  border-color: #1677ff;
  transition: all 0.2s ease;
}

.pay-btn:active {
  background-color: #0f52ba;
  transform: scale(0.95);
}

/* 删除按钮样式 */
.delete-btn {
  height: 100%;
  border-radius: 0;
  transition: all 0.2s ease;
}

.delete-btn:active {
  background-color: #d93025;
}

/* 底部导航栏样式 */
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

/* 适配小屏幕 */
@media (max-width: 375px) {
  .order-info {
    flex-direction: column;
    gap: 3px;
  }

  .order-amount {
    font-size: 15px;
  }

  .order-time {
    font-size: 12px;
  }
}

/* 加载动画优化 */
::v-deep .van-loading__spinner {
  width: 24px;
  height: 24px;
}

/* 标签样式增强 */
::v-deep .van-tag--warning {
  background-color: #fff7e6;
  color: #faad14;
  border: 1px solid #ffe8cc;
}

::v-deep .van-tag--success {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #d9f7be;
}
</style>