<template>
  <div class="project-management-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">订单列表</h1>
        <p class="page-subtitle">
          您的订单一目了然 轻松管理您的订单 订单信息随时查看 快速浏览订单详情
        </p>
      </div>
    </div>
    
    <!-- 订单表格 -->
    <div class="container">
      <div class="table-container">
        <el-table 
          :data="tableData" 
          border 
          style="width: 100%" 
          v-loading="loading"
          :max-height="tableHeight"
        >
          <!-- 表格 -->
          <el-table-column prop="createTime" label="创建时间" width="180" align="center">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="orderNo" label="订单编号" width="200" align="center">
          </el-table-column>
          <el-table-column prop="orderType" label="订单类型" width="120" align="center">
          </el-table-column>
          <el-table-column prop="productName" label="商品名称" width="200" align="center">
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" align="center">
          </el-table-column>
          <el-table-column prop="price" label="单价" width="100" align="center">
            <template slot-scope="scope">
              ¥{{ scope.row.price?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="总金额" width="120" align="center">
            <template slot-scope="scope">
              <span style="color: #e6a23c; font-weight: bold;">
                ¥{{ scope.row.totalAmount?.toFixed(2) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="用户姓名" width="120" align="center">
          </el-table-column>
          <el-table-column prop="userPhone" label="手机号" width="150" align="center">
          </el-table-column>
          <el-table-column prop="status" label="订单状态" width="120" align="center">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { getAllOrders } from '@/api/order.js'
import { Message } from 'element-ui'

export default {
  name: 'OrderList',
  data() {
    return {
      tableData: [], // 存储订单数据
      loading: false,
      tableHeight: 600, // 表格最大高度
      lastFetchTime: 0, // 记录上次获取数据的时间
      fetchInterval: 30000, // 30秒内不重复请求
      pollingTimer: null // 轮询定时器
    }
  },
  methods: {
    // 获取订单信息（增加缓存控制）
    async fetchOrders(forceRefresh = false) {
      const now = Date.now();
      
      // 如果不是强制刷新且距离上次请求时间很短，则使用缓存
      if (!forceRefresh && (now - this.lastFetchTime < this.fetchInterval)) {
        return;
      }
      
      this.loading = true;
      try {
        const response = await getAllOrders()
        console.log('获取订单列表:', response)
        
        if (response && Array.isArray(response)) {
          this.tableData = response
          this.lastFetchTime = now; // 更新最后获取时间
        } else {
          this.tableData = []
          Message.warning('暂无订单数据')
        }
      } catch (error) {
        console.error('获取订单信息失败:', error)
        Message.error('获取订单列表失败')
        this.tableData = []
      } finally {
        this.loading = false
      }
    },

    // 强制刷新订单列表
    forceRefreshOrders() {
      console.log('强制刷新订单列表');
      this.fetchOrders(true);
    },

    // 格式化日期
    formatDate(timestamp) {
      if (!timestamp) return '-'
      const date = new Date(timestamp)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '未支付',
        1: '已支付', 
        2: '已完成',
        3: '已取消',
        4: '已退款'
      }
      return statusMap[status] || '未知状态'
    },

    // 获取状态标签类型
    getStatusType(status) {
      const typeMap = {
        0: 'warning',  // 未支付 - 黄色
        1: 'success',  // 已支付 - 绿色
        2: 'info',     // 已完成 - 蓝色
        3: 'danger',   // 已取消 - 红色
        4: 'info'      // 已退款 - 蓝色
      }
      return typeMap[status] || 'info'
    },

    // 动态计算表格高度
    calculateTableHeight() {
      const windowHeight = window.innerHeight
      this.tableHeight = windowHeight - 300 // 根据页面高度动态调整
    },

    // 处理存储事件（跨标签页通信）
    handleStorageEvent(event) {
      if (event.key === 'refreshOrders' || event.key === 'paymentSuccess') {
        this.forceRefreshOrders();
      }
    },

    // 处理自定义事件
    handleCustomEvent(event) {
      if (event.detail && event.detail.type === 'refreshOrders') {
        this.forceRefreshOrders();
      }
    }
  },

  created() {
    this.fetchOrders(true); // 创建时强制刷新
  },

  mounted() {
    // 窗口大小改变时重新计算高度
    window.addEventListener('resize', this.calculateTableHeight);
    
    // 监听存储事件（跨标签页通信）
    window.addEventListener('storage', this.handleStorageEvent);
    
    // 监听自定义事件
    window.addEventListener('refreshOrders', this.handleCustomEvent);
    
    // 添加轮询机制，每30秒自动刷新一次
    this.pollingTimer = setInterval(() => {
      this.fetchOrders();
    }, 30000);

    // 监听路由变化，当从支付页面返回时刷新
    this.$watch(() => this.$route, (to, from) => {
      if (from.path === '/buy' && to.path === '/orders') {
        setTimeout(() => {
          this.forceRefreshOrders();
        }, 1000);
      }
    });
  },

  beforeDestroy() {
    window.removeEventListener('resize', this.calculateTableHeight);
    window.removeEventListener('storage', this.handleStorageEvent);
    window.removeEventListener('refreshOrders', this.handleCustomEvent);
    
    if (this.pollingTimer) {
      clearInterval(this.pollingTimer);
    }
  }
}
</script>

<style scoped>
/* 样式保持不变，与方案1相同 */
.project-management-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  padding: 60px 0;
  text-align: center;
  margin-bottom: 0;
}

.page-title {
  font-size: 36px;
  margin: 0 0 16px 0;
  font-weight: bold;
}

.page-subtitle {
  font-size: 18px;
  margin: 0;
  opacity: 0.9;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin: 20px 0;
  padding: 20px;
  overflow: hidden;
}

/* 自定义滚动条样式 */
:deep(.el-table__body-wrapper) {
  scrollbar-width: thin;
  scrollbar-color: #c0c4cc #f5f5f5;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: #f5f5f5;
  border-radius: 4px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: #c0c4cc;
  border-radius: 4px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb:hover) {
  background: #909399;
}

@media (max-width: 768px) {
  .container {
    padding: 0 10px;
  }
  
  .table-container {
    padding: 10px;
  }
}
</style>