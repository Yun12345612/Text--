<template>
  <div class="dashboard-container">
    <!-- 顶部欢迎区域 -->
    <div class="dashboard-header">
      <div class="welcome-section">
        <h2>体检健康管理系统</h2>
        <p>欢迎使用系统，{{ currentTime }} {{ userName }}，祝您工作愉快！</p>
      </div>
      <div class="quick-actions">
        <el-button type="primary" icon="el-icon-plus" @click="handleQuickAdd">
          快速登记
        </el-button>
        <el-button icon="el-icon-search" @click="handleQuickSearch">
          快速查询
        </el-button>
        <el-button icon="el-icon-document" @click="handleReport">
          生成报告
        </el-button>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="card-icon">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="card-content">
              <div class="card-value">{{ stats.todayPatients || 0 }}</div>
              <div class="card-title">今日体检人数</div>
              <div class="card-trend">
                <span class="trend-up" v-if="stats.todayTrend > 0">
                  +{{ stats.todayTrend }}%
                </span>
                <span class="trend-down" v-else-if="stats.todayTrend < 0">
                  {{ stats.todayTrend }}%
                </span>
                <span v-else>持平</span>
                较昨日
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="stat-card success">
            <div class="card-icon">
              <i class="el-icon-document-checked"></i>
            </div>
            <div class="card-content">
              <div class="card-value">{{ stats.pendingReports || 0 }}</div>
              <div class="card-title">待审核报告</div>
              <div class="card-trend">需及时处理</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="card-value">{{ stats.monthTotal || 0 }}</div>
            <div class="card-title">本月体检总量</div>
            <div class="card-trend">
              已完成 {{ stats.monthCompleted || 0 }} / 目标 {{ stats.monthTarget || 0 }}
            </div>
            <el-progress 
              :percentage="completionRate" 
              :show-text="false"
              class="progress-bar"
            ></el-progress>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="stat-card info">
            <div class="card-icon">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="card-content">
              <div class="card-value">{{ stats.activeDepartments || 0 }}</div>
              <div class="card-title">活跃科室</div>
              <div class="card-trend">共 {{ stats.totalDepartments || 0 }} 个科室</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表和详细信息区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 体检趋势图表 -->
        <el-col :span="12">
          <div class="chart-card">
            <div class="chart-header">
              <h3>体检趋势分析</h3>
              <el-radio-group v-model="trendPeriod" size="mini" @change="loadChartData">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="quarter">本季</el-radio-button>
              </el-radio-group>
            </div>
            <div class="chart-container">
              <!-- 这里可以接入 ECharts 图表 -->
              <div class="chart-placeholder">
                <i class="el-icon-data-analysis"></i>
                <p>体检数据趋势图表</p>
                <small>可展示每日/每周体检人数变化趋势</small>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 科室分布 -->
        <el-col :span="12">
          <div class="chart-card">
            <div class="chart-header">
              <h3>科室体检分布</h3>
              <el-button type="text" @click="viewDepartmentDetails">查看详情</el-button>
            </div>
            <div class="chart-container">
              <div class="dept-distribution">
                <div 
                  v-for="dept in departmentStats" 
                  :key="dept.name"
                  class="dept-item"
                >
                  <div class="dept-info">
                    <span class="dept-name">{{ dept.name }}</span>
                    <span class="dept-count">{{ dept.count }}人</span>
                  </div>
                  <el-progress 
                    :percentage="dept.percentage" 
                    :show-text="false"
                    :color="dept.color"
                  ></el-progress>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷功能和待办事项 -->
    <div class="bottom-section">
      <el-row :gutter="20">
        <!-- 快捷功能 -->
        <el-col :span="8">
          <div class="function-card">
            <h3>快捷功能</h3>
            <div class="function-grid">
              <div class="function-item" @click="navigateTo('patient')">
                <i class="el-icon-user"></i>
                <span>患者管理</span>
              </div>
              <div class="function-item" @click="navigateTo('appointment')">
                <i class="el-icon-date"></i>
                <span>预约管理</span>
              </div>
              <div class="function-item" @click="navigateTo('report')">
                <i class="el-icon-document"></i>
                <span>报告管理</span>
              </div>
              <div class="function-item" @click="navigateTo('statistics')">
                <i class="el-icon-data-line"></i>
                <span>数据统计</span>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 待办事项 -->
        <el-col :span="8">
          <div class="todo-card">
            <h3>待办事项</h3>
            <div class="todo-list">
              <div 
                v-for="todo in todoList" 
                :key="todo.id"
                class="todo-item"
                :class="{ urgent: todo.priority === 'high' }"
              >
                <el-checkbox v-model="todo.completed" @change="updateTodo(todo)">
                  {{ todo.title }}
                </el-checkbox>
                <span class="todo-time">{{ todo.time }}</span>
              </div>
            </div>
            <div class="todo-actions">
              <el-button type="text" @click="addTodo">添加待办</el-button>
              <el-button type="text" @click="clearCompleted">清除已完成</el-button>
            </div>
          </div>
        </el-col>

        <!-- 系统公告 -->
        <el-col :span="8">
          <div class="notice-card">
            <h3>系统公告</h3>
            <div class="notice-list">
              <div 
                v-for="notice in notices" 
                :key="notice.id"
                class="notice-item"
              >
                <div class="notice-title">
                  <i class="el-icon-message-solid"></i>
                  {{ notice.title }}
                </div>
                <div class="notice-time">{{ notice.time }}</div>
                <div class="notice-content">{{ notice.content }}</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: "HomeDashboard",
  data() {
    return {
      currentTime: this.getCurrentTime(),
      userName: "管理员",
      trendPeriod: 'week',
      stats: {
        todayPatients: 28,
        todayTrend: 12,
        pendingReports: 12,
        monthTotal: 326,
        monthCompleted: 256,
        monthTarget: 400,
        activeDepartments: 8,
        totalDepartments: 12
      },
      departmentStats: [
        { name: '内科', count: 45, percentage: 25, color: '#409EFF' },
        { name: '外科', count: 38, percentage: 21, color: '#67C23A' },
        { name: '检验科', count: 52, percentage: 29, color: '#E6A23C' },
        { name: '放射科', count: 28, percentage: 16, color: '#F56C6C' },
        { name: '其他', count: 17, percentage: 9, color: '#909399' }
      ],
      todoList: [
        { id: 1, title: '审核待处理体检报告', time: '09:00', completed: false, priority: 'high' },
        { id: 2, title: '更新科室排班表', time: '10:30', completed: false, priority: 'medium' },
        { id: 3, title: '检查设备维护记录', time: '14:00', completed: true, priority: 'low' },
        { id: 4, title: '准备月度统计报表', time: '16:00', completed: false, priority: 'high' }
      ],
      notices: [
        { 
          id: 1, 
          title: '系统维护通知', 
          time: '2024-01-15',
          content: '本周六凌晨2:00-4:00进行系统维护，期间系统可能无法访问。'
        },
        { 
          id: 2, 
          title: '新功能上线', 
          time: '2024-01-10',
          content: '体检报告自动分析功能已上线，欢迎使用体验。'
        }
      ]
    };
  },
  computed: {
    completionRate() {
      const { monthCompleted, monthTarget } = this.stats;
      return Math.round((monthCompleted / monthTarget) * 100);
    }
  },
  mounted() {
    // 更新时间
    this.timeInterval = setInterval(() => {
      this.currentTime = this.getCurrentTime();
    }, 60000);
    
    // 模拟加载数据
    this.loadDashboardData();
  },
  beforeDestroy() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval);
    }
  },
  methods: {
    getCurrentTime() {
      const now = new Date();
      const hours = now.getHours();
      if (hours < 6) return '凌晨好';
      if (hours < 12) return '上午好';
      if (hours < 14) return '中午好';
      if (hours < 18) return '下午好';
      return '晚上好';
    },
    
    loadDashboardData() {
      // 这里可以调用API获取实时数据
      console.log('加载仪表板数据...');
    },
    
    loadChartData() {
      console.log('加载图表数据，周期:', this.trendPeriod);
    },
    
    handleQuickAdd() {
      this.$router.push('/patient/registration');
    },
    
    handleQuickSearch() {
      this.$router.push('/search');
    },
    
    handleReport() {
      this.$router.push('/report/generate');
    },
    
    viewDepartmentDetails() {
      this.$router.push('/statistics/department');
    },
    
    navigateTo(module) {
      const routes = {
        patient: '/patient/manage',
        appointment: '/appointment',
        report: '/report/manage',
        statistics: '/statistics'
      };
      this.$router.push(routes[module]);
    },
    
    updateTodo(todo) {
      console.log('更新待办事项:', todo);
    },
    
    addTodo() {
      this.$prompt('请输入待办事项', '添加待办', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (value) {
          this.todoList.unshift({
            id: Date.now(),
            title: value,
            time: '待安排',
            completed: false,
            priority: 'medium'
          });
        }
      });
    },
    
    clearCompleted() {
      this.todoList = this.todoList.filter(todo => !todo.completed);
    }
  }
};
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.welcome-section h2 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.welcome-section p {
  color: #606266;
  margin: 0;
  font-size: 14px;
}

.quick-actions {
  display: flex;
  gap: 12px;
}

/* 统计卡片样式 */
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  border-left: 4px solid;
  height: 120px;
}

.stat-card.primary {
  border-left-color: #409EFF;
}

.stat-card.success {
  border-left-color: #67C23A;
}

.stat-card.warning {
  border-left-color: #E6A23C;
}

.stat-card.info {
  border-left-color: #909399;
}

.card-icon {
  font-size: 48px;
  color: #409EFF;
  margin-right: 16px;
  opacity: 0.8;
}

.card-content {
  flex: 1;
}

.card-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.card-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.card-trend {
  font-size: 12px;
  color: #909399;
}

.trend-up {
  color: #F56C6C;
}

.trend-down {
  color: #67C23A;
}

.progress-bar {
  margin-top: 8px;
}

/* 图表区域 */
.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 300px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.chart-container {
  height: calc(100% - 50px);
}

.chart-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px dashed #dcdfe6;
}

.chart-placeholder i {
  font-size: 48px;
  margin-bottom: 12px;
}

.dept-distribution {
  height: 100%;
}

.dept-item {
  margin-bottom: 16px;
}

.dept-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.dept-name {
  color: #303133;
}

.dept-count {
  color: #909399;
}

/* 底部区域 */
.bottom-section {
  margin-bottom: 24px;
}

.function-card,
.todo-card,
.notice-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 300px;
}

.function-card h3,
.todo-card h3,
.notice-card h3 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 16px;
}

.function-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.function-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.function-item:hover {
  border-color: #409EFF;
  background-color: #f0f7ff;
}

.function-item i {
  font-size: 24px;
  color: #409EFF;
  margin-bottom: 8px;
}

.function-item span {
  font-size: 14px;
  color: #606266;
}

.todo-list {
  max-height: 200px;
  overflow-y: auto;
}

.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-item.urgent {
  background-color: #fef0f0;
  margin: 0 -24px;
  padding: 12px 24px;
}

.todo-time {
  font-size: 12px;
  color: #909399;
}

.todo-actions {
  margin-top: 16px;
  text-align: center;
}

.notice-list {
  max-height: 200px;
  overflow-y: auto;
}

.notice-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-title {
  display: flex;
  align-items: center;
  font-weight: 500;
  margin-bottom: 4px;
}

.notice-title i {
  color: #409EFF;
  margin-right: 8px;
}

.notice-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.notice-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}
</style>