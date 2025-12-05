<template>
  <div class="buy-view">
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">确认订单</h1>
        <p class="page-subtitle">请确认您的购买信息</p>
      </div>
    </div>

    <div class="container">
      <div class="order-content">
        <!-- 收货地址 -->
        <el-card class="address-card" shadow="never">
          <div class="card-header">
            <h3>收货地址（电子卡号 卡密钥以短信形式发送至以下手机号）</h3>
          </div>
          <div class="address-info">
            <div class="phone-info">
              <span class="label">* 手机号：</span>
              <span class="phone-number">{{ userPhone }}</span>
              <el-button type="text" class="change-btn">更换</el-button>
            </div>
            <div class="delivery-info">
              <i class="el-icon-time"></i>
              <span>支付后，预计48小时内发货</span>
            </div>
          </div>
        </el-card>

        <!-- 商品信息 -->
        <el-card class="product-card" shadow="never">
          <div class="card-header">
            <h3>商品信息</h3>
          </div>
          <div class="product-table">
            <div class="table-header">
              <div class="col-product">商品</div>
              <div class="col-price">单价</div>
              <div class="col-quantity">数量</div>
              <div class="col-subtotal">小计</div>
            </div>
            <div class="table-body">
              <div class="product-item">
                <div class="col-product">
                  <div class="product-info">
                    <div class="product-name">
                      {{ selectedItem.name }}
                    </div>
                    <div class="product-desc">
                      {{
                        selectedItem.description ||
                        "专业的体检服务，保障您的健康"
                      }}
                    </div>
                    <div class="product-supplier">
                      {{ selectedItem.departmentName || "通用科室" }}
                    </div>
                  </div>
                </div>
                <div class="col-price">
                  <span class="price"
                    >¥{{ selectedItem.price.toFixed(2) }}</span
                  >
                </div>
                <div class="col-quantity">
                  <el-input-number
                    v-model="quantity"
                    :min="1"
                    :max="10"
                    size="small"
                    @change="updateSubtotal"
                  ></el-input-number>
                </div>
                <div class="col-subtotal">
                  <span class="subtotal">¥{{ subtotal.toFixed(2) }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 订单汇总 -->
        <el-card class="summary-card" shadow="never">
          <div class="order-summary">
            <div class="summary-row">
              <span class="label">商品总数：</span>
              <span class="value">{{ quantity }}件</span>
            </div>
            <div class="summary-row">
              <span class="label">商品总价：</span>
              <span class="value">¥{{ subtotal.toFixed(2) }}</span>
            </div>
            <div class="summary-row total">
              <span class="label">应付总额：</span>
              <span class="value total-price">¥{{ subtotal.toFixed(2) }}</span>
            </div>
          </div>
          <div class="payment-actions">
            <el-button
              type="primary"
              size="large"
              class="pay-btn"
              @click="handlePayment"
              :loading="payLoading"
            >
              <i class="el-icon-wallet"></i>
              立即支付
            </el-button>
            <el-button size="large" class="back-btn" @click="Fanghui()">
              返回上页
            </el-button>
          </div>
        </el-card>

        <!-- 支付方式选择弹框 -->
        <el-dialog
          title="选择支付方式"
          :visible.sync="payDialogVisible"
          width="500px"
          :before-close="handlePayDialogClose"
        >
          <div class="payment-methods">
            <div class="payment-option">
              <el-radio v-model="selectedPayMethod" label="alipay"
                >支付宝支付</el-radio
              >
            </div>
            <div class="payment-option">
              <el-radio v-model="selectedPayMethod" label="wechat"
                >微信支付</el-radio
              >
            </div>
          </div>
          <div slot="footer" class="dialog-footer">
            <el-button @click="payDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmPayment"
              >确认支付</el-button
            >
          </div>
        </el-dialog>

        <!-- 支付结果弹框 -->
        <el-dialog
          :title="paySuccess ? '支付成功' : '支付失败'"
          :visible.sync="payResultVisible"
          width="400px"
          :before-close="handleResultDialogClose"
        >
          <div class="payment-result">
            <i :class="paySuccess ? 'el-icon-success success-icon' : 'el-icon-error error-icon'"></i>
            <p>{{ payResultMessage }}</p>
          </div>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="handleResultDialogClose">
              {{ paySuccess ? '查看订单' : '重新支付' }}
            </el-button>
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import { getItemList } from "@/api/item.js";
import { examinationGetAll } from "@/api/examinationPackage";
import { alipayPay, checkOrderStatus } from "@/api/pay.js";
import { createOrder } from "@/api/order.js";
import { Message } from "element-ui";

export default {
  name: "BuyView",
  data() {
    return {
      payDialogVisible: false,
      payResultVisible: false,
      selectedPayMethod: "alipay",
      loading: false,
      payLoading: false,
      paySuccess: false,
      payResultMessage: "",
      selectedItem: {
        name: "",
        price: 0,
        description: "",
        departmentName: "",
        type: "",
      },
      quantity: 1,
      subtotal: 0,
      userPhone: "15659394053",
      currentOrderNo: "",
      pollTimer: null,
    };
  },

  created() {
    this.initUserInfo();
    this.loadPurchaseData();
  },

  beforeDestroy() {
    if (this.pollTimer) {
      clearInterval(this.pollTimer);
    }
  },

  methods: {
    // 从localStorage获取用户信息
    initUserInfo() {
      const localUserInfo = JSON.parse(localStorage.getItem("userInfo") || "{}");
      if (localUserInfo.userPhone) {
        this.userPhone = localUserInfo.userPhone;
        console.log("✅ 使用localStorage手机号:", this.userPhone);
      } else {
        console.error("❌ 找不到手机号");
      }
    },

    // 返回
    Fanghui() {
      if (this.selectedItem.type === "package") {
        this.$router.push("/packages");
      } else {
        this.$router.push("/Examination");
      }
    },

    // 加载项目/套餐数据
    async loadPurchaseData() {
      const id = this.$route.query.id;
      const type = this.$route.query.type;

      if (!id) {
        Message.error("未选择购买内容");
        this.$router.back();
        return;
      }

      this.loading = true;

      try {
        if (type === "package") {
          await this.loadPackageData(id);
        } else {
          await this.loadProjectData(id);
        }
      } catch (error) {
        console.error("加载数据失败:", error);
        Message.error("加载失败");
        this.$router.back();
      } finally {
        this.loading = false;
      }
    },

    // 加载套餐数据
    async loadPackageData(packageId) {
      try {
        const response = await examinationGetAll();
        const packages = response.map((item) => ({
          id: item.packageId,
          name: item.packageName,
          price: item.packagePrice,
          description: item.packageDesc,
          departmentName: "体检套餐",
          type: "package",
        }));

        const packageItem = packages.find((p) => p.id == packageId);
        if (packageItem) {
          this.selectedItem = packageItem;
          this.subtotal = packageItem.price;
        } else {
          Message.error("套餐不存在");
          this.$router.back();
        }
      } catch (error) {
        console.error("获取套餐失败:", error);
        throw error;
      }
    },

    // 加载项目数据
    async loadProjectData(projectId) {
      const params = {
        pageNum: 0,
        pageSize: 100,
        itemStatus: "active",
      };

      try {
        const pageData = await getItemList(params);
        const projects = pageData.list || [];
        const project = projects.find((p) => p.itemId == projectId);

        if (project) {
          this.selectedItem = {
            id: project.itemId,
            name: project.itemName,
            price: project.itemPrice,
            description: project.detailDesc,
            departmentName: project.departmentName,
            type: "project",
          };
          this.subtotal = project.itemPrice;
        } else {
          Message.error("项目不存在");
          this.$router.back();
        }
      } catch (error) {
        console.error("获取项目失败:", error);
        throw error;
      }
    },

    // 更新小计
    updateSubtotal() {
      this.subtotal = this.selectedItem.price * this.quantity;
    },

    // 处理支付
    handlePayment() {
      this.payDialogVisible = true;
    },

    // 确认支付
    confirmPayment() {
      if (!this.selectedPayMethod) {
        this.$message.warning("请选择支付方式");
        return;
      }

      this.payDialogVisible = false;

      this.$confirm(
        `确定要使用${this.getPayMethodText()}支付 ¥${this.subtotal.toFixed(
          2
        )} 购买「${this.selectedItem.name}」吗？`,
        "确认支付",
        {
          confirmButtonText: "确定支付",
          cancelButtonText: "再想想",
          type: "warning",
        }
      )
        .then(() => {
          this.processPayment();
        })
        .catch(() => {
          this.$message.info("已取消支付");
        });
    },

    // 处理支付逻辑
    async processPayment() {
      this.payLoading = true;

      try {
        // 准备支付数据
        const payData = {
          totalAmount: this.subtotal,
          subject: `${this.selectedItem.name} - ${
            this.selectedItem.type === "package" ? "体检套餐" : "体检项目"
          }`,
          itemId: this.selectedItem.id,
          itemType: this.selectedItem.type,
          quantity: this.quantity,
          userPhone: this.userPhone,
          userId: this.getUserId(),
          userName: this.getUserName(),
        };

        console.log("发起支付请求:", payData);

        // 1. 先创建订单
        const orderResponse = await createOrder(payData);
        console.log("订单创建成功", orderResponse);

        if (!orderResponse.success) {
          throw new Error(orderResponse.message || "订单创建失败");
        }

        const traceNo = orderResponse.traceNo;
        this.currentOrderNo = traceNo;

        console.log("使用后端订单号:", traceNo);

        // 2. 调用支付宝支付接口
        const payRequestData = {
          traceNo: traceNo,
          totalAmount: this.subtotal,
          subject: payData.subject,
        };

        const response = await alipayPay(payRequestData);
        console.log("支付宝接口调用成功");

        // 打开支付宝支付页面
        this.openAlipayPage(response);

        // 开始轮询支付状态
        this.startPollingPaymentStatus(traceNo);
      } catch (error) {
        console.error("支付请求失败:", error);
        this.$message.error(error.message || "支付请求失败，请重试");
        this.payLoading = false;
      }
    },

    // 获取用户ID
    getUserId() {
      const userInfo = JSON.parse(localStorage.getItem("userInfo") || "{}");
      return userInfo.userId || "";
    },

    // 获取用户名
    getUserName() {
      const userInfo = JSON.parse(localStorage.getItem("userInfo") || "{}");
      return userInfo.userName || "";
    },

    // 获取支付方式文本
    getPayMethodText() {
      return this.selectedPayMethod === "alipay" ? "支付宝" : "微信";
    },

    // 打开支付宝支付页面
    openAlipayPage(htmlContent) {
      const newWindow = window.open("", "_blank");
      newWindow.document.write(htmlContent);
      newWindow.document.close();

      setTimeout(() => {
        const form = newWindow.document.querySelector("form");
        if (form) {
          form.submit();
        }
      }, 100);
    },

    // 开始轮询支付状态
    startPollingPaymentStatus(orderNo) {
      if (this.pollTimer) {
        clearInterval(this.pollTimer);
      }

      let pollCount = 0;
      const maxPollCount = 60;

      this.pollTimer = setInterval(async () => {
        try {
          pollCount++;
          const response = await checkOrderStatus(orderNo);
          console.log('支付状态检查:', response);

          if (response.paid) {
            clearInterval(this.pollTimer);
            this.handlePaymentSuccess();
          } else if (response.expired || pollCount >= maxPollCount) {
            clearInterval(this.pollTimer);
            this.handlePaymentExpired();
          }
        } catch (error) {
          console.error('查询支付状态失败:', error);
          if (pollCount >= maxPollCount) {
            clearInterval(this.pollTimer);
            this.handlePaymentExpired();
          }
        }
      }, 5000);
    },

    // 处理支付成功
    handlePaymentSuccess() {
      this.payLoading = false;
      this.paySuccess = true;
      this.payResultMessage = "支付成功！我们将尽快为您处理订单。";
      this.payResultVisible = true;
      this.$message.success("支付成功！");

      // 关键修改：通知订单列表刷新
      this.notifyOrderRefresh();
    },

    // 处理支付过期
    handlePaymentExpired() {
      this.payLoading = false;
      this.paySuccess = false;
      this.payResultMessage = "订单已过期，请重新下单";
      this.payResultVisible = true;
      this.$message.warning("订单已过期");
    },

    // 通知订单列表刷新
    notifyOrderRefresh() {
      // 方法1: 使用 localStorage 事件（跨标签页）
      localStorage.setItem('refreshOrders', Date.now().toString());
      
      // 方法2: 使用自定义事件
      window.dispatchEvent(new CustomEvent('refreshOrders', {
        detail: { type: 'paymentSuccess', orderNo: this.currentOrderNo }
      }));
      
      // 方法3: 直接设置标记
      sessionStorage.setItem('shouldRefreshOrders', 'true');
      
      console.log('已发送订单刷新通知');
    },

    // 处理支付弹框关闭
    handlePayDialogClose(done) {
      this.$confirm("确定要取消支付吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "继续支付",
        type: "warning",
      })
        .then(() => {
          done();
        })
        .catch(() => {});
    },

    // 支付结果弹框关闭处理
    handleResultDialogClose() {
      this.payResultVisible = false;
      if (this.paySuccess) {
        this.$router.push("/orders");
      }
    },
  },
};
</script>

<style scoped>
.buy-view {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 40px;
}

.page-header {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  padding: 40px 0;
  text-align: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 32px;
  margin: 0 0 12px 0;
  font-weight: bold;
}

.page-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.order-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.address-card,
.product-card,
.summary-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
}

.card-header {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 20px;
  margin-bottom: 0;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.address-info {
  padding: 20px;
}

.phone-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.label {
  color: #666;
  font-size: 14px;
}

.phone-number {
  color: #333;
  font-size: 16px;
  font-weight: 500;
  margin: 0 12px;
}

.change-btn {
  color: #409eff;
  font-size: 14px;
}

.delivery-info {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 14px;
}

.delivery-info i {
  margin-right: 6px;
  color: #409eff;
}

.product-table {
  padding: 0;
}

.table-header {
  display: flex;
  background: #fafafa;
  padding: 12px 20px;
  border-bottom: 1px solid #e8e8e8;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.table-body {
  padding: 0;
}

.product-item {
  display: flex;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.col-product {
  flex: 3;
  text-align: left;
}

.col-price,
.col-quantity,
.col-subtotal {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-info {
  text-align: left;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.product-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 8px;
}

.product-supplier {
  font-size: 12px;
  color: #999;
}

.price,
.subtotal {
  font-size: 16px;
  font-weight: 600;
  color: #e6a23c;
}

.order-summary {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.summary-row.total {
  border-top: 1px solid #e8e8e8;
  padding-top: 12px;
  margin-top: 12px;
  font-size: 18px;
  font-weight: 600;
}

.total-price {
  color: #e6a23c;
  font-size: 20px;
}

.payment-actions {
  padding: 20px;
  text-align: center;
}

.pay-btn {
  width: 200px;
  height: 50px;
  font-size: 18px;
  margin-right: 20px;
}

.back-btn {
  width: 120px;
  height: 50px;
  font-size: 16px;
}

.payment-result {
  text-align: center;
  padding: 20px;
}

.success-icon {
  font-size: 48px;
  color: #67c23a;
  margin-bottom: 16px;
}

.error-icon {
  font-size: 48px;
  color: #f56c6c;
  margin-bottom: 16px;
}

.payment-result p {
  font-size: 16px;
  color: #333;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }

  .table-header {
    display: none;
  }

  .product-item {
    flex-direction: column;
    gap: 15px;
  }

  .col-product,
  .col-price,
  .col-quantity,
  .col-subtotal {
    flex: none;
    justify-content: flex-start;
    width: 100%;
  }

  .col-price::before {
    content: "单价：";
    color: #666;
    margin-right: 8px;
  }

  .col-quantity::before {
    content: "数量：";
    color: #666;
    margin-right: 8px;
  }

  .col-subtotal::before {
    content: "小计：";
    color: #666;
    margin-right: 8px;
  }

  .payment-actions {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .pay-btn,
  .back-btn {
    width: 100%;
    margin-right: 0;
  }
}
</style>