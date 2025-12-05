<template>
  <div class="input-field-page">
    <!-- 页面标题 -->
    <el-page-header @back="handleBack" content="体检细项小结录入">
    </el-page-header>

    <!-- 搜索区 -->
    <div class="search-card">
      <el-row :gutter="20" justify="center">
        <el-col :span="12">
          <el-input
            v-model="searchForm.orderNo"
            placeholder="请输入订单编号"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="handleSearch"
          >
            <el-button
              slot="append"
              icon="el-icon-search"
              type="primary"
              @click="handleSearch"
              :loading="searchLoading"
            >
              查询订单
            </el-button>
          </el-input>
        </el-col>
      </el-row>
    </div>

    <!-- 订单信息 -->
    <el-card v-if="currentOrder.orderNo" class="order-card" shadow="never">
      <div class="order-header">
        <h3>订单信息</h3>
        <div class="progress-info">
          已完成: {{ completedItemsCount }}/{{ examinationItems.length }} 项
        </div>
      </div>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="订单编号">
          {{ currentOrder.orderNo }}
        </el-descriptions-item>
        <el-descriptions-item label="用户姓名">
          {{ currentOrder.userName }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ currentOrder.userPhone || "暂无" }}
        </el-descriptions-item>
        <el-descriptions-item label="商品名称">
          {{ currentOrder.productName || "体检项目" }}
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">
          ¥{{ currentOrder.totalAmount?.toFixed(2) || "0.00" }}
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 体检细项列表 -->
    <el-card
      v-if="examinationItems.length > 0"
      class="items-card"
      shadow="never"
    >
      <div class="items-header">
        <h3>体检细项 (共{{ examinationItems.length }}项)</h3>
        <el-button
          type="primary"
          size="small"
          @click="handleViewAllSummaries"
          :disabled="completedItemsCount === 0"
        >
          查看所有小结 ({{ completedItemsCount }})
        </el-button>
      </div>

      <div class="table-container">
        <el-table
          :data="examinationItems"
          border
          style="width: 100%"
          align="center"
          :max-height="tableMaxHeight"
          v-loading="itemsLoading"
        >
          <el-table-column
            type="index"
            label="序号"
            width="80"
            align="center"
          />
          <el-table-column
            prop="detailName"
            label="细项名称"
            align="center"
            min-width="120"
          >
            <template slot-scope="scope">
              {{ scope.row.detailName || "未知细项" }}
            </template>
          </el-table-column>
          <el-table-column
            prop="detailUnit"
            label="单位"
            align="center"
            width="100"
          >
            <template slot-scope="scope">
              {{ scope.row.detailUnit || "-" }}
            </template>
          </el-table-column>
          <el-table-column
            prop="departmentName"
            label="科室"
            align="center"
            min-width="120"
          >
            <template slot-scope="scope">
              {{ scope.row.departmentName || "通用科室" }}
            </template>
          </el-table-column>
          <el-table-column label="参考范围" align="center" width="150">
            <template slot-scope="scope">
              {{
                scope.row.minValue !== undefined && scope.row.minValue !== null
                  ? scope.row.minValue
                  : "-"
              }}
              ~
              {{
                scope.row.maxValue !== undefined && scope.row.maxValue !== null
                  ? scope.row.maxValue
                  : "-"
              }}
            </template>
          </el-table-column>

          <!-- 检查结果列 -->
          <el-table-column label="检查结果" align="center" width="120">
            <template slot-scope="scope">
              <span v-if="scope.row.checkResult" class="result-value">
                {{ scope.row.checkResult }}
              </span>
              <span v-else class="no-result">-</span>
            </template>
          </el-table-column>

          <!-- 检查状态列 -->
          <el-table-column label="小结状态" width="120" align="center">
            <template slot-scope="scope">
              <el-tag
                :type="scope.row.hasResult ? 'success' : 'warning'"
                size="small"
              >
                {{ scope.row.hasResult ? "已录入" : "待录入" }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            prop="updateTime"
            label="更新时间"
            align="center"
            width="150"
          >
            <template slot-scope="scope">
              {{ scope.row.updateTime || "-" }}
            </template>
          </el-table-column>

          <!-- 操作列 -->
          <el-table-column
            label="操作"
            width="200"
            align="center"
            fixed="right"
          >
            <template slot-scope="scope">
              <el-button
                v-if="!scope.row.hasResult"
                type="primary"
                size="small"
                @click="handleInputDetail(scope.row)"
              >
                录入小结
              </el-button>
              <el-button
                v-else
                type="text"
                size="small"
                @click="handleViewDetail(scope.row)"
              >
                查看小结
              </el-button>
              <el-button
                v-if="scope.row.hasResult"
                type="text"
                size="small"
                @click="handleEditDetail(scope.row)"
              >
                修改
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 暂无数据提示 -->
    <el-card v-if="showNoData" class="no-data-card" shadow="never">
      <div class="no-data">
        <i class="el-icon-search" style="font-size: 48px; color: #c0c4cc"></i>
        <p>{{ noDataMessage }}</p>
      </div>
    </el-card>

    <!-- 录入/编辑细项小结弹窗 -->
    <el-dialog
      :title="`${isEditMode ? '修改' : '录入'}细项小结 - ${
        currentDetail.detailName
      }`"
      :visible.sync="detailDialogVisible"
      :width="'600px'"
      :close-on-click-modal="false"
    >
      <el-form
        :model="detailForm"
        :rules="detailRules"
        ref="detailFormRef"
        label-width="120px"
      >
        <el-form-item label="细项名称">
          <el-input v-model="currentDetail.detailName" disabled></el-input>
        </el-form-item>

        <el-form-item label="参考范围">
          <el-input
            :value="`${currentDetail.minValue || '-'} ~ ${
              currentDetail.maxValue || '-'
            } ${currentDetail.detailUnit || ''}`"
            disabled
          ></el-input>
        </el-form-item>

        <el-form-item label="检查结果" prop="checkResult">
          <el-input
            v-model="detailForm.checkResult"
            :placeholder="`请输入${currentDetail.detailName}的检查结果`"
            clearable
          ></el-input>
        </el-form-item>

        <el-form-item label="结果说明" prop="resultDescription">
          <el-input
            type="textarea"
            v-model="detailForm.resultDescription"
            :rows="3"
            :placeholder="`请输入${currentDetail.detailName}的结果说明和分析...`"
            maxlength="200"
            show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item label="检查医生" prop="doctorName">
          <el-input
            v-model="detailForm.doctorName"
            placeholder="请输入检查医生姓名"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleSaveDetail"
          :loading="detailLoading"
        >
          {{ detailLoading ? "保存中..." : "保存小结" }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 查看细项小结弹窗 -->
    <el-dialog
      :title="`查看细项小结 - ${viewDetail.detailName}`"
      :visible.sync="viewDialogVisible"
      :width="'600px'"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="细项名称">
          {{ viewDetail.detailName }}
        </el-descriptions-item>
        <el-descriptions-item label="参考范围">
          {{ viewDetail.minValue || "-" }} ~ {{ viewDetail.maxValue || "-" }}
          {{ viewDetail.detailUnit || "" }}
        </el-descriptions-item>
        <el-descriptions-item label="检查结果">
          <span class="result-value">{{ viewDetail.checkResult }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="结果说明">
          {{ viewDetail.resultDescription }}
        </el-descriptions-item>
        <el-descriptions-item label="检查医生">
          {{ viewDetail.doctorName }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ viewDetail.updateTime }}
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="viewDialogVisible = false"
          >关闭</el-button
        >
      </div>
    </el-dialog>

    <!-- 查看所有小结弹窗 -->
    <el-dialog
      title="所有细项小结"
      :visible.sync="allSummariesDialogVisible"
      :width="'800px'"
    >
      <div v-if="completedItems.length > 0">
        <div
          v-for="(item, index) in completedItems"
          :key="item.detailId"
          class="summary-item"
        >
          <h4>{{ index + 1 }}. {{ item.detailName }}</h4>
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="检查结果">
              <span class="result-value">{{ item.checkResult }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="结果说明">
              {{ item.resultDescription }}
            </el-descriptions-item>
            <el-descriptions-item label="检查医生">
              {{ item.doctorName }}
            </el-descriptions-item>
            <el-descriptions-item label="更新时间">
              {{ item.updateTime }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <div v-else class="no-summary">
        <p>暂无已录入的细项小结</p>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="allSummariesDialogVisible = false"
          >关闭</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message } from "element-ui";
import { getOrderDetailsByPath, getOrderDetailsByParam } from "@/api/order.js";
import { saveExaminationDetailWithDoctor } from "@/api/examinationDetailSummary.js";

export default {
  name: "InputFieldView",
  data() {
    return {
      searchForm: {
        orderNo: "",
      },
      searchLoading: false,
      itemsLoading: false,
      detailLoading: false,
      showNoData: false,
      noDataMessage: "请输入订单编号查询体检细项",

      currentOrder: {
        orderNo: "",
        userName: "",
        userPhone: "",
        productName: "",
        totalAmount: 0,
        status: 1,
      },
      examinationItems: [],

      // 细项小结相关
      detailDialogVisible: false,
      viewDialogVisible: false,
      allSummariesDialogVisible: false,
      isEditMode: false,
      currentDetail: {},
      viewDetail: {},
      detailForm: {
        checkResult: "", // 对应后端实体 checkResult
        resultDescription: "", // 对应后端实体 resultDescription
        doctorName: "", // 对应后端实体 doctorName
      },
      detailRules: {
        checkResult: [
          { required: true, message: "请输入检查结果", trigger: "blur" },
        ],
        resultDescription: [
          { required: true, message: "请输入结果说明", trigger: "blur" },
        ],
        doctorName: [
          { required: true, message: "请输入检查医生姓名", trigger: "blur" },
        ],
      },

      tableMaxHeight: 400,
    };
  },
  computed: {
    completedItemsCount() {
      return this.examinationItems.filter((item) => item.hasResult).length;
    },
    completedItems() {
      return this.examinationItems.filter((item) => item.hasResult);
    },
  },
  mounted() {
    this.calculateTableHeight();
    window.addEventListener("resize", this.calculateTableHeight);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.calculateTableHeight);
  },
  methods: {
    calculateTableHeight() {
      const windowHeight = window.innerHeight;
      const headerHeight = 60;
      const searchHeight = 80;
      const orderInfoHeight = 120;
      const itemsHeaderHeight = 60;
      const padding = 100;

      let calculatedHeight =
        windowHeight -
        headerHeight -
        searchHeight -
        orderInfoHeight -
        itemsHeaderHeight -
        padding;

      if (calculatedHeight < 200) {
        calculatedHeight = 200;
      } else if (calculatedHeight > 600) {
        calculatedHeight = 600;
      }

      this.tableMaxHeight = calculatedHeight;
    },

    async handleSearch() {
      if (!this.searchForm.orderNo.trim()) {
        Message.warning("请输入订单编号");
        return;
      }

      this.searchLoading = true;
      this.showNoData = false;

      try {
        let response;

        try {
          response = await getOrderDetailsByPath(this.searchForm.orderNo);
        } catch (error) {
          response = await getOrderDetailsByParam(this.searchForm.orderNo);
        }

        console.log("=== 后端API完整响应 ===", response);

        let details = [];

        if (Array.isArray(response)) {
          details = response;
        } else if (response && response.code === 200) {
          details = response.data || [];
        } else if (response && response.data && Array.isArray(response.data)) {
          details = response.data;
        } else if (response && typeof response === "object") {
          const possibleArrays = Object.values(response).filter((item) =>
            Array.isArray(item)
          );
          if (possibleArrays.length > 0) {
            details = possibleArrays[0];
          } else {
            details = [response];
          }
        }

        console.log("=== 解析出的细项数据 ===", details);

        if (details && details.length > 0) {
          const firstDetail = details[0];

          this.currentOrder = {
            orderNo: firstDetail.orderNo || this.searchForm.orderNo,
            userName: firstDetail.userName || "未知用户",
            userPhone: firstDetail.userPhone || firstDetail.phone || "",
            productName:
              firstDetail.productName || firstDetail.itemName || "体检项目",
            totalAmount:
              firstDetail.totalAmount || firstDetail.orderAmount || 0,
            status: firstDetail.status || 1,
            summaryId: firstDetail.summaryId || 0,
          };

          this.examinationItems = details.map((item, index) => {
            const mappedItem = {
              id: item.id || index,
              detailId: item.detailId || item.id,
              detailName:
                item.detailName || item.detail_name || `细项${index + 1}`,
              detailUnit:
                item.detailUnit || item.detail_unit || item.unit || "",
              departmentName:
                item.departmentName ||
                item.department ||
                item.department_name ||
                "通用科室",
              minValue: item.minValue || item.min_value || item.referenceMin,
              maxValue: item.maxValue || item.max_value || item.referenceMax,
              updateTime: item.updateTime || item.update_time || "-",
              detailDesc:
                item.detailDesc || item.detail_desc || item.description || "",
              checkResult: item.checkResult || item.result || "",
              resultDescription:
                item.resultDescription || item.result_desc || "",
              doctorName: item.doctorName || item.doctor_name || "",
              hasResult: !!(item.checkResult || item.result),
            };

            return mappedItem;
          });

          Message.success(`查询成功，共${this.examinationItems.length}条细项`);
        } else {
          this.showNoData = true;
          this.noDataMessage = "该订单暂无体检细项数据";
          Message.warning("未找到该订单的细项数据");
        }
      } catch (error) {
        console.error("查询失败:", error);
        this.showNoData = true;
        this.noDataMessage = "查询失败，请检查网络连接或订单编号是否正确";
        Message.error("查询失败: " + (error.message || "请检查订单编号"));
      } finally {
        this.searchLoading = false;
      }
    },

    // 录入细项小结
    handleInputDetail(detail) {
      this.isEditMode = false;
      this.currentDetail = { ...detail };
      this.detailForm = {
        checkResult: "",
        resultDescription: "",
        doctorName: "",
      };
      this.detailDialogVisible = true;
    },

    // 查看细项小结
    handleViewDetail(detail) {
      this.viewDetail = { ...detail };
      this.viewDialogVisible = true;
    },

    // 修改细项小结
    handleEditDetail(detail) {
      this.isEditMode = true;
      this.currentDetail = { ...detail };
      this.detailForm = {
        checkResult: detail.checkResult || "",
        resultDescription: detail.resultDescription || "",
        doctorName: detail.doctorName || "",
      };
      this.detailDialogVisible = true;
    },

    // 查看所有小结
    handleViewAllSummaries() {
      if (this.completedItemsCount === 0) {
        Message.warning("暂无已录入的细项小结");
        return;
      }
      this.allSummariesDialogVisible = true;
    },

    async handleSaveDetail() {
      this.$refs.detailFormRef.validate(async (valid) => {
        if (valid) {
          this.detailLoading = true;

          const submitData = {
            summaryId: this.currentOrder.summaryId, 
            detailId: this.currentDetail.detailId,
            checkResult: this.detailForm.checkResult,
            resultDescription: this.detailForm.resultDescription,
            doctorName: this.detailForm.doctorName,
            orderOn: this.currentOrder.orderNo
          };

          console.log("=== 提交的数据 ===", submitData);

          try {
            const re = await saveExaminationDetailWithDoctor(submitData);
            const response = re.data
            console.log("=== 后端完整响应 ===", response);
            // console.log("=== 响应类型 ===", typeof response);
            // console.log("=== 响应所有属性 ===", Object.keys(response));
            // console.log("=== success字段值 ===", response.success);
            // console.log("=== code字段值 ===", response.code);
            // console.log("=== msg字段值 ===", response.msg);
            

            // 使用更灵活的判断方式

              Message.success(
                this.isEditMode ? "细项小结修改成功" : "细项小结保存成功"
              );
              this.detailDialogVisible = false;

              // 更新本地列表数据
              const index = this.examinationItems.findIndex(
                (item) => item.detailId === this.currentDetail.detailId
              );
              if (index !== -1) {
                this.examinationItems[index].checkResult = this.detailForm.checkResult;
                this.examinationItems[index].resultDescription = this.detailForm.resultDescription;
                this.examinationItems[index].doctorName = this.detailForm.doctorName;
                this.examinationItems[index].hasResult = true;
                this.examinationItems[index].updateTime = new Date().toLocaleString();
              }

          } catch (error) {
            console.error("保存细项小结失败:", error);
            Message.error("保存失败，请重试: " + (error.message || "未知错误"));
          } finally {
            this.detailLoading = false;
          }
        }
      });
    },

    handleBack() {
      this.$router.push({ name: "HomeDashboard" });
    },

    getStatusText(status) {
      const statusMap = {
        0: "待支付",
        1: "已支付",
        2: "已完成",
        3: "已取消",
        4: "已退款",
      };
      return statusMap[status] || "未知状态";
    },

    getStatusType(status) {
      const typeMap = {
        0: "warning",
        1: "success",
        2: "info",
        3: "danger",
        4: "info",
      };
      return typeMap[status] || "info";
    },
  },
};
</script>

<style scoped>
.input-field-page {
  padding: 20px;
}

.search-card {
  padding: 20px;
  margin-bottom: 20px;
  background: white;
  border-radius: 8px;
}

.order-card,
.items-card,
.no-data-card {
  margin-top: 20px;
  border-radius: 8px;
}

.order-header,
.items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
}

.order-header h3,
.items-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.progress-info {
  color: #666;
  font-size: 14px;
}

.table-container {
  margin-bottom: 0;
}

.no-data-card {
  text-align: center;
  padding: 60px 20px;
}

.no-data {
  color: #909399;
}

.no-data p {
  margin: 16px 0 0 0;
  font-size: 16px;
}

.dialog-footer {
  text-align: right;
}

.result-value {
  color: #409eff;
  font-weight: bold;
}

.no-result {
  color: #c0c4cc;
}

.summary-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.summary-item h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.no-summary {
  text-align: center;
  padding: 40px;
  color: #909399;
}
</style>