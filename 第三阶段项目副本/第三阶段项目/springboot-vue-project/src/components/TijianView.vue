<template>
  <div class="examination-result-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <el-page-header @back="handleBack" content="体检结果查询">
      </el-page-header>
    </div>

    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="姓名">
        <el-input
          v-model="searchForm.userName"
          placeholder="请输入姓名"
          clearable
        />
      </el-form-item>
      <el-form-item label="订单编号">
        <el-input
          v-model="searchForm.orderNo"
          placeholder="请输入订单编号"
          clearable
        />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input
          v-model="searchForm.userPhone"
          placeholder="请输入手机号"
          clearable
        />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input
          v-model="searchForm.userCard"
          placeholder="请输入身份证号"
          clearable
        />
      </el-form-item>
      <el-form-item label="性别">
        <el-select
          v-model="searchForm.userGender"
          placeholder="请选择性别"
          clearable
        >
          <el-option label="男" value="1"></el-option>
          <el-option label="女" value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格区域 -->
    <div class="table-container">
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        align="center"
        v-loading="loading"
        :max-height="tableMaxHeight"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column
          prop="orderNo"
          label="订单编号"
          align="center"
          min-width="150"
        />
        <el-table-column
          prop="userName"
          label="姓名"
          align="center"
          min-width="100"
        />
        <el-table-column
          prop="userAge"
          label="年龄"
          align="center"
          width="80"
        />
        <el-table-column
          prop="userGender"
          label="性别"
          align="center"
          width="80"
        >
          <template slot-scope="scope">
            {{
              scope.row.userGender === "1"
                ? "男"
                : scope.row.userGender === "2"
                ? "女"
                : "未知"
            }}
          </template>
        </el-table-column>
        <el-table-column
          prop="userCard"
          label="身份证号"
          align="center"
          width="180"
        />
        <el-table-column
          prop="userPhone"
          label="手机号"
          align="center"
          width="120"
        />
        <el-table-column
          prop="orderType"
          label="订单类型"
          align="center"
          min-width="120"
        />
        <el-table-column
          prop="totalAmount"
          label="总金额"
          align="center"
          width="100"
        >
          <template slot-scope="scope">
            <span style="color: #e6a23c; font-weight: bold">
              ¥{{ scope.row.totalAmount?.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="订单状态"
          align="center"
          width="100"
        >
          <template slot-scope="scope">
            <el-tag :type="getPayStatusType(scope.row.status)">
              {{ getPayStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          align="center"
          width="160"
        >
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="handleSummary(scope.row)"
            >
              体检小结
            </el-button>
            <el-button
              type="text"
              size="small"
              style="color: #67c23a"
              @click="handleConclusion(scope.row)"
            >
              体检总结
            </el-button>
            <el-button
              type="text"
              size="small"
              style="color: #409eff"
              @click="handleReport(scope.row)"
            >
              总报告
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页区域 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      />
    </div>

    <!-- 体检小结弹窗 - 修改为显示详细小结数据 -->
    <el-dialog
      title="体检小结详情"
      :visible.sync="summaryDialogVisible"
      width="900px"
    >
      <div v-if="currentRecord" class="summary-content">
        <div class="user-info">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单编号">{{
              currentRecord.orderNo
            }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{
              currentRecord.userName
            }}</el-descriptions-item>
            <el-descriptions-item label="年龄"
              >{{ currentRecord.userAge }}岁</el-descriptions-item
            >
            <el-descriptions-item label="性别">
              {{ currentRecord.userGender === "1" ? "男" : "女" }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">{{
              currentRecord.userPhone
            }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{
              currentRecord.userCard
            }}</el-descriptions-item>
            <el-descriptions-item label="订单类型">{{
              currentRecord.orderType
            }}</el-descriptions-item>
            <el-descriptions-item label="总金额"
              >¥{{
                currentRecord.totalAmount?.toFixed(2)
              }}</el-descriptions-item
            >
            <el-descriptions-item label="订单状态">
              <el-tag
                :type="getPayStatusType(currentRecord.status)"
                size="small"
              >
                {{ getPayStatusText(currentRecord.status) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="summary-details" style="margin-top: 20px">
          <h3>体检细项小结 (共{{ summaryDetails.length }}项)</h3>
          <div v-if="summaryDetails.length > 0">
            <el-table
              :data="summaryDetails"
              border
              style="width: 100%"
              max-height="400"
            >
              <el-table-column
                type="index"
                label="序号"
                width="60"
                align="center"
              />
              <el-table-column
                prop="detailName"
                label="细项名称"
                align="center"
                min-width="120"
              />
              <el-table-column
                prop="checkResult"
                label="检查结果"
                align="center"
                width="100"
              >
                <template slot-scope="scope">
                  <span
                    :style="{
                      color:
                        scope.row.checkResult === '正常'
                          ? '#67c23a'
                          : '#e6a23c',
                      fontWeight: 'bold',
                    }"
                  >
                    {{ scope.row.checkResult }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column
                prop="resultDescription"
                label="结果说明"
                align="center"
                min-width="200"
              />
              <el-table-column
                prop="doctorName"
                label="检查医生"
                align="center"
                width="100"
              />
              <el-table-column
                prop="updateTime"
                label="更新时间"
                align="center"
                width="160"
              >
                <template slot-scope="scope">
                  {{ formatDate(scope.row.updateTime) }}
                </template>
              </el-table-column>
            </el-table>

            <!-- 小结统计 -->
            <div
              style="
                margin-top: 15px;
                text-align: right;
                color: #666;
                padding: 10px;
                background: #f8f9fa;
                border-radius: 4px;
              "
              
              
            >
              <span style="color: #67c23a">正常: {{ normalCount }}项</span> |
              <span style="color: #e6a23c">异常: {{ abnormalCount }}项</span> |
              总计: {{ summaryDetails.length }}项
            </div>
          </div>
          <div v-else class="no-data">
            <el-empty
              description="暂无体检小结数据"
              :image-size="80"
            ></el-empty>
            <p style="color: #909399; margin-top: 10px">
              请在"体检细项小结录入"页面为该订单录入体检小结
            </p>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="summaryDialogVisible = false"
          >关闭</el-button
        >
      </div>
    </el-dialog>

    <!-- 体检总结弹窗 -->
    <el-dialog
      title="体检总结"
      :visible.sync="conclusionDialogVisible"
      width="700px"
    >
      <div v-if="currentRecord" class="conclusion-content">
        <div class="user-info">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单编号">{{
              currentRecord.orderNo
            }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{
              currentRecord.userName
            }}</el-descriptions-item>
            <el-descriptions-item label="年龄"
              >{{ currentRecord.userAge }}岁</el-descriptions-item
            >
            <el-descriptions-item label="性别">
              {{ currentRecord.userGender === "1" ? "男" : "女" }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">{{
              currentRecord.userPhone
            }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{
              currentRecord.userCard
            }}</el-descriptions-item>
            <el-descriptions-item label="订单类型">{{
              currentRecord.orderType
            }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="conclusion-text" style="margin-top: 20px">
          <h3>体检总结</h3>
          <el-input
            type="textarea"
            :rows="6"
            v-model="conclusionForm.conclusion"
            placeholder="请输入体检总结..."
          />
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="conclusionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveConclusion">保存</el-button>
      </div>
    </el-dialog>

    <!-- 总报告弹窗 -->
    <el-dialog
      title="体检总报告"
      :visible.sync="reportDialogVisible"
      width="800px"
    >
      <div v-if="currentRecord" class="report-content">
        <div
          class="report-header"
          style="text-align: center; margin-bottom: 20px"
        >
          <h2>体检总报告</h2>
          <p style="color: #666; margin-top: 10px">
            订单编号：{{ currentRecord.orderNo }}
          </p>
        </div>

        <div class="report-body">
          <el-descriptions title="基本信息" :column="2" border>
            <el-descriptions-item label="姓名">{{
              currentRecord.userName
            }}</el-descriptions-item>
            <el-descriptions-item label="年龄"
              >{{ currentRecord.userAge }}岁</el-descriptions-item
            >
            <el-descriptions-item label="性别">
              {{ currentRecord.userGender === "1" ? "男" : "女" }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">{{
              currentRecord.userPhone
            }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{
              currentRecord.userCard
            }}</el-descriptions-item>
            <el-descriptions-item label="订单类型">{{
              currentRecord.orderType
            }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{
              formatDate(currentRecord.createTime)
            }}</el-descriptions-item>
          </el-descriptions>

          <div style="margin-top: 20px">
            <h3>报告内容</h3>
            <p style="color: #666; line-height: 1.6">
              这里是体检总报告的详细内容，包含所有检查项目的汇总分析和健康建议。
            </p>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="reportDialogVisible = false">关闭</el-button>
        <el-button type="success" @click="handlePrintReport"
          >打印报告</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message } from "element-ui";
import { getAllOrders, searchOrders } from "@/api/tijain.js";
import { getExaminationDetailsByOrder } from "@/api/examinationDetailSummary.js"; // 导入查询API

export default {
  name: "ExaminationResultView",
  data() {
    return {
      searchForm: {
        userName: "",
        orderNo: "",
        userPhone: "",
        userCard: "",
        userGender: "",
      },
      tableData: [],
      loading: false,
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },

      // 弹窗控制
      summaryDialogVisible: false,
      conclusionDialogVisible: false,
      reportDialogVisible: false,

      // 当前操作的记录
      currentRecord: null,

      // ✅ 新增：存储小结数据
      summaryDetails: [],

      // 表单数据
      summaryForm: {
        summary: "",
      },
      conclusionForm: {
        conclusion: "",
      },

      // 表格高度
      tableMaxHeight: 400,
    };
  },
  computed: {
    // ✅ 新增：计算正常和异常的小结数量
    normalCount() {
      return this.summaryDetails.filter((item) => item.checkResult === "正常")
        .length;
    },
    abnormalCount() {
      return this.summaryDetails.filter((item) => item.checkResult !== "正常")
        .length;
    },
  },
  mounted() {
    this.loadExamResults();
    this.calculateTableHeight();
    window.addEventListener("resize", this.calculateTableHeight);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.calculateTableHeight);
  },
  methods: {
    handleBack() {
      this.$router.go(-1);
    },

    // 计算表格高度
    calculateTableHeight() {
      const windowHeight = window.innerHeight;
      const headerHeight = 80;
      const searchHeight = 80;
      const paginationHeight = 60;
      const padding = 60;

      let calculatedHeight =
        windowHeight - headerHeight - searchHeight - paginationHeight - padding;

      if (calculatedHeight < 200) {
        calculatedHeight = 200;
      } else if (calculatedHeight > 600) {
        calculatedHeight = 600;
      }

      this.tableMaxHeight = calculatedHeight;
    },

    // 加载体检结果列表
    async loadExamResults() {
      this.loading = true;
      try {
        const response = await getAllOrders();

        if (response && Array.isArray(response)) {
          this.tableData = response;
          this.pagination.total = response.length;
        } else {
          this.tableData = [];
          this.pagination.total = 0;
          Message.warning("暂无订单数据");
        }
      } catch (error) {
        console.error("获取订单列表失败:", error);
        Message.error("获取订单列表失败");
        this.tableData = [];
        this.pagination.total = 0;
        this.loadMockData();
      } finally {
        this.loading = false;
      }
    },

    // 模拟数据作为fallback
    loadMockData() {
      this.tableData = [
        {
          id: 1,
          orderNo: "ORD20241105001",
          userName: "张三",
          userAge: "35",
          userGender: "1",
          userCard: "110101199001011234",
          userPhone: "13800138000",
          orderType: "体检套餐",
          totalAmount: 299.0,
          status: 1,
          createTime: new Date("2024-11-05 10:30:00").getTime(),
        },
        {
          id: 2,
          orderNo: "ORD20241105002",
          userName: "李四",
          userAge: "28",
          userGender: "2",
          userCard: "110101199501012345",
          userPhone: "13900139000",
          orderType: "体检项目",
          totalAmount: 150.0,
          status: 0,
          createTime: new Date("2024-11-05 11:20:00").getTime(),
        },
      ];
      this.pagination.total = this.tableData.length;
    },

    // 搜索功能
    async handleSearch() {
      this.loading = true;
      this.pagination.currentPage = 1;

      try {
        const searchParams = {
          userName: this.searchForm.userName || null,
          orderNo: this.searchForm.orderNo || null,
          userPhone: this.searchForm.userPhone || null,
          userCard: this.searchForm.userCard || null,
          userGender: this.searchForm.userGender || null,
        };

        Object.keys(searchParams).forEach((key) => {
          if (searchParams[key] === null || searchParams[key] === "") {
            delete searchParams[key];
          }
        });

        let response;
        if (Object.keys(searchParams).length === 0) {
          response = await getAllOrders();
        } else {
          response = await searchOrders(searchParams);
        }

        if (response && Array.isArray(response)) {
          this.tableData = response;
          this.pagination.total = response.length;
          Message.success(`查询到 ${response.length} 条记录`);
        } else {
          this.tableData = [];
          this.pagination.total = 0;
          Message.warning("未找到符合条件的订单");
        }
      } catch (error) {
        console.error("搜索订单失败:", error);
        Message.error("搜索失败");
        this.tableData = [];
        this.pagination.total = 0;
      } finally {
        this.loading = false;
      }
    },

    // 重置搜索条件
    handleReset() {
      this.searchForm = {
        userName: "",
        orderNo: "",
        userPhone: "",
        userCard: "",
        userGender: "",
      };
      this.pagination.currentPage = 1;
      this.loadExamResults();
    },

    // 格式化日期
    formatDate(timestamp) {
      if (!timestamp) return "-";
      const date = new Date(timestamp);
      return date.toLocaleString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      });
    },

    // 支付状态显示
    getPayStatusType(status) {
      const typeMap = {
        0: "warning",
        1: "success",
        2: "info",
        3: "danger",
        4: "info",
      };
      return typeMap[status] || "info";
    },

    getPayStatusText(status) {
      const textMap = {
        0: "未支付",
        1: "已支付",
        2: "已完成",
        3: "已取消",
        4: "已退款",
      };
      return textMap[status] || "未知";
    },

    // ✅ 修改：体检小结 - 查询并显示小结数据
    async handleSummary(row) {
      this.currentRecord = row;
      this.summaryDialogVisible = true;

      try {
        console.log("开始查询小结，订单号:", row.orderNo);

        // 调用API查询该订单的小结数据
        const response = await getExaminationDetailsByOrder(row.orderNo);
        console.log("查询小结响应:", response);

        // 处理响应数据
        if (response) {
          this.summaryDetails = response;
          if (this.summaryDetails.length > 0) {
            Message.success(`查询到 ${this.summaryDetails.length} 条小结记录`);
          } else {
            Message.info("该订单暂无体检小结数据");
          }
        } else {
          this.summaryDetails = [];
          Message.warning(response?.message || "该订单暂无体检小结数据");
        }
      } catch (error) {
        console.error("查询小结失败:", error);
        this.summaryDetails = [];
        Message.error("查询小结数据失败: " + (error.message || "未知错误"));

        // 如果API调用失败，可以使用模拟数据测试
        // 注意：这里不要定义未使用的orderNo变量
        // this.summaryDetails = this.generateMockSummaryData(row.orderNo);
        // Message.info('使用模拟数据展示');
      }
    },

    // 生成模拟数据的方法（备用）
    generateMockSummaryData() {
      return [
        {
          id: 1,
          detailName: "血常规",
          checkResult: "正常",
          resultDescription: "各项指标均在正常范围内",
          doctorName: "张医生",
          updateTime: new Date().getTime(),
        },
        {
          id: 2,
          detailName: "尿常规",
          checkResult: "异常",
          resultDescription: "尿蛋白偏高，建议复查",
          doctorName: "李医生",
          updateTime: new Date().getTime(),
        },
        {
          id: 3,
          detailName: "心电图",
          checkResult: "正常",
          resultDescription: "心率正常，无异常波形",
          doctorName: "王医生",
          updateTime: new Date().getTime(),
        },
      ];
    },

    handleConclusion(row) {
      this.currentRecord = row;
      this.conclusionForm.conclusion = row.conclusion || "";
      this.conclusionDialogVisible = true;
    },

    handleReport(row) {
      this.currentRecord = row;
      this.reportDialogVisible = true;
    },

    // 保存操作
    handleSaveSummary() {
      if (!this.summaryForm.summary.trim()) {
        Message.warning("请输入体检小结");
        return;
      }

      Message.success("小结保存成功");
      this.summaryDialogVisible = false;
    },

    handleSaveConclusion() {
      if (!this.conclusionForm.conclusion.trim()) {
        Message.warning("请输入体检总结");
        return;
      }

      Message.success("总结保存成功");
      this.conclusionDialogVisible = false;
    },

    // 打印功能
    handlePrintReport() {
      Message.info("打印功能开发中...");
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.loadExamResults();
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadExamResults();
    },
  },
};
</script>

<style scoped>
.examination-result-container {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-sizing: border-box;
}

.page-header {
  flex-shrink: 0;
  margin-bottom: 20px;
}

.search-form {
  flex-shrink: 0;
  margin-bottom: 20px;
  padding: 0;
}

.search-form .el-form-item {
  margin-right: 20px;
  margin-bottom: 10px;
}

.table-container {
  flex: 1;
  overflow: hidden;
  margin-bottom: 0;
  min-height: 200px;
}

.el-table {
  width: 100%;
  margin-bottom: 0;
}

.pagination-container {
  flex-shrink: 0;
  margin-top: 20px;
  text-align: right;
  padding: 10px 0;
}

.dialog-footer {
  text-align: right;
}

.summary-content {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 10px;
}

.summary-details {
  margin-top: 20px;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.conclusion-content,
.report-content {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}

.table-container ::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.table-container ::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.table-container ::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

.table-container ::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

.summary-content ::-webkit-scrollbar,
.conclusion-content ::-webkit-scrollbar,
.report-content ::-webkit-scrollbar {
  width: 6px;
}

.summary-content ::-webkit-scrollbar-track,
.conclusion-content ::-webkit-scrollbar-track,
.report-content ::-webkit-scrollbar-track {
  background: #f8f9fa;
}

.summary-content ::-webkit-scrollbar-thumb,
.conclusion-content ::-webkit-scrollbar-thumb,
.report-content ::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

@media (max-height: 700px) {
  .table-container {
    min-height: 150px;
  }

  .examination-result-container {
    padding: 15px;
  }
}

@media (max-width: 1200px) {
  .search-form .el-form-item {
    margin-right: 15px;
    margin-bottom: 8px;
  }
}
</style>