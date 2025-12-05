<script>
import { getItemList } from "@/api/item.js";
import { examinationGetAll } from "@/api/examinationPackage";
import { alipayPay, checkOrderStatus } from "@/api/pay.js";
import { Message } from "element-ui";

export default {
  name: "BuyView",
  data() {
    return {
      payDialogVisible: false,
      payResultVisible: false,
      selectedPayMethod: "alipay", // 默认选择支付宝
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
    this.loadPurchaseData();
  },
  beforeDestroy() {
    if (this.pollTimer) {
      clearInterval(this.pollTimer);
    }
  },
  methods: {
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
          price: item.packagePrice / 100,
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
        const traceNo = this.generateOrderNo();
        this.currentOrderNo = traceNo;

        const payData = {
          traceNo: traceNo,
          totalAmount: this.subtotal.toFixed(2),
          subject: `${this.selectedItem.name} - ${
            this.selectedItem.type === "package" ? "体检套餐" : "体检项目"
          }`,
          itemId: this.selectedItem.id,
          itemType: this.selectedItem.type,
          quantity: this.quantity,
          phone: this.userPhone,
        };

        console.log("发起支付请求:", payData);

        // 调用支付宝支付接口
        const response = await alipayPay(payData);

        // 打开支付宝支付页面
        this.openAlipayPage(response);

        // 开始轮询支付状态
        this.startPollingPaymentStatus(traceNo);
      } catch (error) {
        console.error("支付请求失败:", error);
        this.$message.error("支付请求失败，请重试");
        this.payLoading = false;
      }
    },

    // 获取支付方式文本
    getPayMethodText() {
      return this.selectedPayMethod === 'alipay' ? '支付宝' : '微信';
    },

    // 生成订单号
    generateOrderNo() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 10000);
      return `ORDER_${timestamp}_${random}`;
    },

    // 打开支付宝支付页面
    openAlipayPage(htmlContent) {
      const newWindow = window.open('', '_blank');
      newWindow.document.write(htmlContent);
      newWindow.document.close();
      
      setTimeout(() => {
        const form = newWindow.document.querySelector('form');
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
      
      this.pollTimer = setInterval(async () => {
        try {
          const response = await checkOrderStatus(orderNo);
          
          if (response.paid) {
            clearInterval(this.pollTimer);
            this.handlePaymentSuccess();
          } else if (response.expired) {
            clearInterval(this.pollTimer);
            this.handlePaymentExpired();
          }
        } catch (error) {
          console.error('查询支付状态失败:', error);
        }
      }, 3000);
    },

    // 处理支付成功
    handlePaymentSuccess() {
      this.payLoading = false;
      this.paySuccess = true;
      this.payResultMessage = '支付成功！我们将尽快为您处理订单。';
      this.showPaymentResult();
      this.$message.success('支付成功！');
      
      setTimeout(() => {
        this.$router.push('/orders');
      }, 2000);
    },

    // 处理支付过期
    handlePaymentExpired() {
      this.payLoading = false;
      this.paySuccess = false;
      this.payResultMessage = '订单已过期，请重新下单';
      this.showPaymentResult();
      this.$message.warning('订单已过期');
    },

    // 显示支付结果
    showPaymentResult() {
      this.payResultVisible = true;
    },

    // 处理支付弹框关闭
    handlePayDialogClose(done) {
      this.$confirm('确定要取消支付吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '继续支付',
        type: 'warning'
      }).then(() => {
        done();
      }).catch(() => {});
    },

    // 支付结果弹框关闭处理
    handleResultDialogClose() {
      this.payResultVisible = false;
      if (this.paySuccess) {
        this.$router.push('/orders');
      }
    }
  }
};
</script>