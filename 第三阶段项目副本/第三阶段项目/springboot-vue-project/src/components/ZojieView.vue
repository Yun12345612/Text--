<template>
  <div class="input-field-page">
    <!-- 页面标题 -->
    <el-page-header @back="handleBack" content="体检总结录入"> </el-page-header>
    
    <!-- 搜索区 - 只保留订单编号搜索 -->
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
      </div>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="订单编号">
          {{ currentOrder.orderNo }}
        </el-descriptions-item>
        <el-descriptions-item label="用户姓名">
          {{ currentOrder.userName }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ currentOrder.userPhone }}
        </el-descriptions-item>
        <el-form-item label="商品名称">
          <el-input v-model="currentOrder.productName" disabled></el-input>
        </el-form-item>
        <el-descriptions-item label="订单金额">
          ¥{{ currentOrder.totalAmount?.toFixed(2) }}
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 体检小结信息 -->
    <el-card v-if="currentSummary.orderNo" class="summary-card" shadow="never">
      <div class="summary-header">
        <h3>体检小结信息</h3>
        <el-tag type="success" v-if="currentSummary.summaryContent">
          已录入小结
        </el-tag>
        <el-tag type="warning" v-else>
          未录入小结
        </el-tag>
      </div>
      
      <el-descriptions :column="1" border v-if="currentSummary.summaryContent">
        <el-descriptions-item label="小结内容">
          <div class="summary-content">
            {{ currentSummary.summaryContent }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="医生建议">
          <div class="summary-content">
            {{ currentSummary.doctorAdvice }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="小结结论">
          <el-tag :type="getConclusionType(currentSummary.conclusion)">
            {{ getConclusionText(currentSummary.conclusion) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="录入医生">
          {{ currentSummary.doctorName || '未填写' }}
        </el-descriptions-item>
        <el-descriptions-item label="录入时间">
          {{ formatDate(currentSummary.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div v-else class="no-summary">
        <el-alert
          title="该订单尚未录入体检小结，请先完成小结录入再填写总结"
          type="warning"
          :closable="false"
          show-icon
        ></el-alert>
      </div>
    </el-card>

    <!-- 体检总结录入区域 -->
    <el-card v-if="currentOrder.orderNo && currentSummary.summaryContent" class="conclusion-card" shadow="never">
      <div class="conclusion-header">
        <h3>体检总结录入</h3>
        <el-button 
          type="primary" 
          @click="handleInputConclusion"
          :disabled="!canInputConclusion"
        >
          录入总结
        </el-button>
      </div>

      <!-- 已录入的总结信息 -->
      <div v-if="currentConclusion.orderNo" class="existing-conclusion">
        <el-alert
          title="该订单已录入体检总结"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        ></el-alert>
        
        <el-descriptions :column="1" border>
          <el-descriptions-item label="总结内容">
            <div class="conclusion-content">
              {{ currentConclusion.conclusionContent }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="健康建议">
            <div class="conclusion-content">
              {{ currentConclusion.healthAdvice }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="总体评价">
            <el-tag :type="getConclusionType(currentConclusion.overallEvaluation)">
              {{ getConclusionText(currentConclusion.overallEvaluation) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总结医生">
            {{ currentConclusion.doctorName }}
          </el-descriptions-item>
          <el-descriptions-item label="录入时间">
            {{ formatDate(currentConclusion.createTime) }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div style="text-align: center; margin-top: 20px;">
          <el-button type="warning" @click="handleEditConclusion">
            修改总结
          </el-button>
        </div>
      </div>

      <!-- 未录入总结的提示 -->
      <div v-else class="no-conclusion">
        <el-alert
          title="该订单尚未录入体检总结，请点击上方按钮开始录入"
          type="info"
          :closable="false"
          show-icon
        ></el-alert>
      </div>
    </el-card>

    <!-- 暂无数据提示 -->
    <el-card v-if="showNoData" class="no-data-card" shadow="never">
      <div class="no-data">
        <i class="el-icon-search" style="font-size: 48px; color: #c0c4cc;"></i>
        <p>{{ noDataMessage }}</p>
      </div>
    </el-card>

    <!-- 录入体检总结弹窗 -->
    <el-dialog
      :title="conclusionDialogTitle"
      :visible.sync="conclusionDialogVisible"
      :width="'800px'"
      :close-on-click-modal="false"
    >
      <el-form :model="conclusionForm" :rules="conclusionRules" ref="conclusionFormRef" label-width="120px">
        <el-form-item label="订单编号">
          <el-input v-model="currentOrder.orderNo" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="用户姓名">
          <el-input v-model="currentOrder.userName" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="体检小结">
          <div class="summary-preview">
            <p><strong>小结内容：</strong>{{ currentSummary.summaryContent }}</p>
            <p><strong>医生建议：</strong>{{ currentSummary.doctorAdvice }}</p>
            <p><strong>小结结论：</strong>{{ getConclusionText(currentSummary.conclusion) }}</p>
          </div>
        </el-form-item>
        
        <el-form-item label="总结内容" prop="conclusionContent">
          <el-input
            type="textarea"
            v-model="conclusionForm.conclusionContent"
            :rows="6"
            placeholder="请输入体检总结内容，基于小结信息进行综合分析..."
            maxlength="1000"
            show-word-limit
          ></el-input>
        </el-form-item>
        
        <el-form-item label="健康建议" prop="healthAdvice">
          <el-input
            type="textarea"
            v-model="conclusionForm.healthAdvice"
            :rows="4"
            placeholder="请输入综合健康建议..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>
        
        <el-form-item label="总体评价" prop="overallEvaluation">
          <el-select v-model="conclusionForm.overallEvaluation" placeholder="请选择总体评价" style="width: 100%">
            <el-option label="健康" value="healthy"></el-option>
            <el-option label="基本健康" value="basically_healthy"></el-option>
            <el-option label="亚健康" value="sub_healthy"></el-option>
            <el-option label="需关注" value="need_attention"></el-option>
            <el-option label="需治疗" value="need_treatment"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="总结医生" prop="doctorName">
          <el-input v-model="conclusionForm.doctorName" placeholder="请输入总结医生姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="复查建议">
          <el-input
            type="textarea"
            v-model="conclusionForm.recheckAdvice"
            :rows="3"
            placeholder="请输入复查建议（可选）..."
            maxlength="300"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="conclusionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveConclusion">保存总结</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message } from "element-ui";
import { 
  getExaminationResultByOrderNo, 
  getExaminationSummary,
  getExaminationConclusion,
  saveExaminationConclusion 
} from "@/api/examination.js";

export default {
  name: 'ExaminationConclusionView',
  data() {
    return {
      searchForm: {
        orderNo: "",
      },
      searchLoading: false,
      showNoData: false,
      noDataMessage: "暂无查询结果，请输入正确的订单编号",
      
      currentOrder: {},
      currentSummary: {},
      currentConclusion: {},
      
      conclusionDialogVisible: false,
      conclusionDialogTitle: "录入体检总结",
      isEditing: false,
      
      conclusionForm: {
        conclusionContent: "",
        healthAdvice: "",
        overallEvaluation: "",
        doctorName: "",
        recheckAdvice: ""
      },
      
      conclusionRules: {
        conclusionContent: [
          { required: true, message: "请输入体检总结内容", trigger: "blur" },
          { min: 20, message: "总结内容至少20个字符", trigger: "blur" }
        ],
        healthAdvice: [
          { required: true, message: "请输入健康建议", trigger: "blur" }
        ],
        overallEvaluation: [
          { required: true, message: "请选择总体评价", trigger: "change" }
        ],
        doctorName: [
          { required: true, message: "请输入医生姓名", trigger: "blur" }
        ]
      }
    };
  },
  computed: {
    canInputConclusion() {
      return this.currentOrder.orderNo && this.currentSummary.summaryContent;
    }
  },
  methods: {
    async handleSearch() {
      if (!this.searchForm.orderNo.trim()) {
        Message.warning("请输入订单编号");
        return;
      }

      this.searchLoading = true;
      this.showNoData = false;
      this.currentOrder = {};
      this.currentSummary = {};
      this.currentConclusion = {};

      try {
        // 1. 获取订单信息
        const orderResponse = await getExaminationResultByOrderNo(this.searchForm.orderNo);
        
        if (orderResponse && orderResponse.success) {
          this.currentOrder = orderResponse.data || {};
          
          // 2. 获取体检小结信息
          const summaryResponse = await getExaminationSummary(this.searchForm.orderNo);
          
          if (summaryResponse && summaryResponse.success) {
            this.currentSummary = summaryResponse.data || {};
            
            // 3. 获取体检总结信息
            const conclusionResponse = await getExaminationConclusion(this.searchForm.orderNo);
            
            if (conclusionResponse && conclusionResponse.success) {
              this.currentConclusion = conclusionResponse.data || {};
            }
            
            Message.success("查询成功");
          } else {
            this.showNoData = true;
            this.noDataMessage = "该订单尚未录入体检小结";
          }
        } else {
          this.showNoData = true;
          this.noDataMessage = "未找到对应的订单信息";
        }
      } catch (error) {
        console.error("查询失败:", error);
        this.showNoData = true;
        this.noDataMessage = "查询失败，请检查网络连接";
        Message.error("查询失败");
      } finally {
        this.searchLoading = false;
      }
    },

    handleInputConclusion() {
      this.conclusionForm = {
        conclusionContent: "",
        healthAdvice: "",
        overallEvaluation: "",
        doctorName: "",
        recheckAdvice: ""
      };
      this.conclusionDialogTitle = "录入体检总结";
      this.isEditing = false;
      this.conclusionDialogVisible = true;
    },

    handleEditConclusion() {
      this.conclusionForm = {
        conclusionContent: this.currentConclusion.conclusionContent || "",
        healthAdvice: this.currentConclusion.healthAdvice || "",
        overallEvaluation: this.currentConclusion.overallEvaluation || "",
        doctorName: this.currentConclusion.doctorName || "",
        recheckAdvice: this.currentConclusion.recheckAdvice || ""
      };
      this.conclusionDialogTitle = "修改体检总结";
      this.isEditing = true;
      this.conclusionDialogVisible = true;
    },

    async handleSaveConclusion() {
      this.$refs.conclusionFormRef.validate(async (valid) => {
        if (valid) {
          const submitData = {
            orderNo: this.currentOrder.orderNo,
            conclusionContent: this.conclusionForm.conclusionContent,
            healthAdvice: this.conclusionForm.healthAdvice,
            overallEvaluation: this.conclusionForm.overallEvaluation,
            doctorName: this.conclusionForm.doctorName,
            recheckAdvice: this.conclusionForm.recheckAdvice,
            summaryId: this.currentSummary.id // 关联的小结ID
          };

          try {
            const response = await saveExaminationConclusion(submitData);
            
            if (response && response.success) {
              Message.success(this.isEditing ? "体检总结修改成功" : "体检总结保存成功");
              this.conclusionDialogVisible = false;
              
              // 重新加载总结信息
              const conclusionResponse = await getExaminationConclusion(this.searchForm.orderNo);
              if (conclusionResponse && conclusionResponse.success) {
                this.currentConclusion = conclusionResponse.data || {};
              }
            } else {
              Message.error("保存失败");
            }
          } catch (error) {
            console.error("保存体检总结失败:", error);
            Message.error("保存失败");
          }
        }
      });
    },

    getConclusionText(conclusion) {
      const conclusionMap = {
        'normal': '正常',
        'basically_normal': '基本正常',
        'need_recheck': '需复查',
        'abnormal': '异常',
        'critical_abnormal': '重大异常',
        'healthy': '健康',
        'basically_healthy': '基本健康',
        'sub_healthy': '亚健康',
        'need_attention': '需关注',
        'need_treatment': '需治疗'
      };
      return conclusionMap[conclusion] || conclusion || '未填写';
    },

    getConclusionType(conclusion) {
      const typeMap = {
        'normal': 'success',
        'basically_normal': 'success',
        'need_recheck': 'warning',
        'abnormal': 'warning',
        'critical_abnormal': 'danger',
        'healthy': 'success',
        'basically_healthy': 'success',
        'sub_healthy': 'warning',
        'need_attention': 'warning',
        'need_treatment': 'danger'
      };
      return typeMap[conclusion] || 'info';
    },

    handleBack() {
      this.$router.push({ name: "HomeDashboard" });
    },

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

    getStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'info',
        3: 'danger',
        4: 'info'
      }
      return typeMap[status] || 'info'
    },

    formatDate(timestamp) {
      if (!timestamp) return '-'
      const date = new Date(timestamp)
      return `${date.getFullYear()}-${this.padZero(date.getMonth() + 1)}-${this.padZero(date.getDate())} ${this.padZero(date.getHours())}:${this.padZero(date.getMinutes())}:${this.padZero(date.getSeconds())}`
    },

    padZero(num) {
      return num < 10 ? `0${num}` : num
    },
  },
};
</script>

<style scoped>
.input-field-page {
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.search-card {
  padding: 20px;
  margin-bottom: 20px;
}

.order-card,
.summary-card,
.conclusion-card,
.no-data-card {
  margin-top: 20px;
}

.order-header,
.summary-header,
.conclusion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
}

.order-header h3,
.summary-header h3,
.conclusion-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
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

.summary-content,
.conclusion-content {
  padding: 8px 0;
  line-height: 1.6;
  color: #606266;
}

.summary-preview {
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.summary-preview p {
  margin: 8px 0;
  line-height: 1.5;
}

.no-summary,
.no-conclusion {
  padding: 20px;
  text-align: center;
}

.existing-conclusion {
  padding: 10px 0;
}

.el-dialog__body {
  padding: 25px;
}

.el-form-item {
  margin-bottom: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .input-field-page {
    padding: 10px;
  }
  
  .search-card {
    padding: 15px;
  }
  
  .order-header,
  .summary-header,
  .conclusion-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>