<template>
  <div class="package-management-page">
    <!-- 页面标题 -->
    <el-page-header @back="handleBack" content="套餐管理"> </el-page-header>
    
    <!-- 操作区和搜索区 -->
    <div class="search-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchForm.packageName"
            placeholder="请输入套餐名称"
            clearable
            prefix-icon="el-icon-search"
          ></el-input>
        </el-col>
        <el-col :span="8">
          <el-select
            v-model="searchForm.packageStatus"
            placeholder="请选择套餐状态"
            clearable
          >
            <el-option label="全部" value=""></el-option>
            <el-option label="启用" value="active"></el-option>
            <el-option label="停用" value="inactive"></el-option>
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="handleSearch" icon="el-icon-search">
            搜索
          </el-button>
          <el-button @click="resetSearch" style="margin-left: 10px">
            重置
          </el-button>
        </el-col>
      </el-row>

      <!-- 价格区间筛选 -->
      <el-row :gutter="20" style="margin-top: 15px">
        <el-col :span="12">
          <el-slider
            v-model="priceRange"
            :min="0"
            :max="20000"
            :step="100"
            range
            @change="handlePriceChange"
          ></el-slider>
          <div class="price-range-text">
            价格区间: ¥{{ priceRange[0] }} - ¥{{ priceRange[1] }}
          </div>
        </el-col>
        <el-col :span="12" class="text-right">
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="handleAddPackage"
          >
            新增套餐
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 套餐列表 -->
    <div class="table-card" style="margin-top: 20px">
      <el-table
        :data="paginatedPackages"
        border
        stripe
        :loading="loading"
        style="width: 100%"
      >
      <!-- “序号”列 -->
    <!-- “序号”列，添加 align="center" 实现居中 -->
<el-table-column label="序号" min-width="80" align="center">
  <template slot-scope="scope">
    {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
  </template>
</el-table-column>
        <el-table-column
          prop="packageName"
          label="套餐名称"
          min-width="180"
        ></el-table-column>
        <el-table-column
          prop="packageDesc"
          label="套餐描述"
          min-width="300"
        ></el-table-column>
        <el-table-column
          prop="itemName"
          label="包含项目"
          min-width="120"
        ></el-table-column>
        <el-table-column prop="packageStatus" label="套餐状态" min-width="120">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.packageStatus === 'active' ? 'success' : 'info'"
            >
              {{ scope.row.packageStatus === "active" ? "启用" : "停用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="packagePrice" label="套餐价格" min-width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.packagePrice.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleEditPackage(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              :style="
                scope.row.packageIsDelete === 1
                  ? 'color: #606266'
                  : 'color: #F56C6C'
              "
              @click="handleDeletePackage(scope.row)"
              :disabled="scope.row.packageIsDelete === 1"
            >
              {{ scope.row.packageIsDelete === 1 ? "已删除" : "删除" }}
            </el-button>
            <el-button
              size="mini"
              type="text"
              v-if="scope.row.packageIsDelete === 1"
              @click="handleRestorePackage(scope.row)"
            >
              恢复
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        >
        </el-pagination>
      </div>
    </div>

    <!-- 新增/编辑套餐弹窗 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :width="'800px'"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <el-form-item label="套餐名称" prop="packageName">
          <el-input v-model="form.packageName"></el-input>
        </el-form-item>
        <el-form-item label="套餐描述" prop="packageDesc">
          <el-input type="textarea" v-model="form.packageDesc"></el-input>
        </el-form-item>
        <el-form-item label="套餐状态" prop="packageStatus">
          <el-select v-model="form.packageStatus">
            <el-option label="启用" value="active"></el-option>
            <el-option label="停用" value="inactive"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="套餐价格" prop="packagePrice">
          <el-input
            v-model.number="form.packagePrice"
            type="number"
            min="0"
            step="0.01"
          >
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>

        <!-- 穿梭框表单项：用于选择包含的项目 -->
        <el-form-item label="包含项目" class="transfer-item">
          <div class="transfer-container">
            <el-transfer
              v-model="form.selectedItems"
              :data="transferData"
              :titles="['可选项目', '已选项目']"
              :button-texts="['移除', '添加']"
              filterable
              filter-placeholder="请输入项目名称"
              @change="handleTransferChange"
              v-loading="isTransferLoading"
            ></el-transfer>
          </div>
          <div class="transfer-tips">
            已选择
            {{ form.selectedItems ? form.selectedItems.length : 0 }} 个项目
            <span v-if="isTransferLoading" style="color: #409eff"
              >（加载中...）</span
            >
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePackage">保存</el-button>
      </div>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog
      title="确认删除"
      :visible.sync="deleteDialogVisible"
      :width="'400px'"
      :close-on-click-modal="false"
    >
      <p>确定要删除该套餐吗？删除后可以在列表中恢复。</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确定删除</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  examinationPage,
  examinationAdd,
  examinationUpdate,
  examinationDelete,
  getItemList,
} from "@/api/package.js";
import { Message } from "element-ui";

export default {
  data() {
    return {
      // 穿梭框相关数据
      transferData: [], // 穿梭框数据源
      isTransferLoading: false, // 穿梭框加载状态

      packages: [], // 原始套餐列表
      searchForm: {
        packageName: "",
        packageStatus: "",
      },
      priceRange: [0, 20000],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: "新增套餐",
      form: {
        packageId: null,
        packageName: "",
        packageDesc: "",
        packageStatus: "active",
        packagePrice: 0,
        // 选中的项目ID数组（用于穿梭框）
        selectedItems: [],
        // 传递给后端的项目ID数组
        itemIds: [],
      },
      rules: {
        packageName: [
          { required: true, message: "请输入套餐名称", trigger: "blur" },
          {
            min: 2,
            max: 50,
            message: "长度在 2 到 50 个字符",
            trigger: "blur",
          },
        ],
        packageDesc: [
          { required: true, message: "请输入套餐描述", trigger: "blur" },
        ],
        packageStatus: [
          { required: true, message: "请选择套餐状态", trigger: "change" },
        ],
        packagePrice: [
          { required: true, message: "请输入套餐价格", trigger: "blur" },
          {
            type: "number",
            min: 0,
            message: "价格不能为负数",
            trigger: "blur",
          },
        ],
      },
      deleteDialogVisible: false,
      currentPackage: null,
    };
  },
  computed: {
    filteredPackages() {
      const searchName = (this.searchForm.packageName || "").toLowerCase();
      return this.packages.filter((pkg) => {
        const packageName = pkg.packageName || "";
        const nameMatch = packageName.toLowerCase().includes(searchName);
        const statusMatch =
          !this.searchForm.packageStatus ||
          pkg.packageStatus === this.searchForm.packageStatus;
        const priceMatch =
          pkg.packagePrice >= this.priceRange[0] &&
          pkg.packagePrice <= this.priceRange[1];
        return nameMatch && statusMatch && priceMatch;
      });
    },
    paginatedPackages() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      return this.filteredPackages.slice(
        startIndex,
        startIndex + this.pageSize
      );
    },
  },
  created() {
    this.fetchPackages();
    // 获取所有项目列表用于穿梭框
    this.loadTransferData();
  },
  methods: {
    // ========== 穿梭框相关方法 ==========

    /**
     * 获取项目明细列表并生成穿梭框数据
     * 从API获取所有项目，转换为穿梭框需要的格式
     */
    loadTransferData() {
      this.isTransferLoading = true;
      getItemList()
        .then((response) => {
          console.log("获取的项目列表:", response);
          // 根据实际API响应结构调整数据获取
          const items = response.data || response.list || response || [];

          // 生成穿梭框数据格式
          this.transferData = items.map((item) => ({
            key: item.itemId, // 项目ID作为唯一标识
            label: `${item.itemName} (¥${item.itemPrice || 0})`, // 显示项目名称和价格
            disabled: false, // 是否禁用
            originalData: item, // 保存原始数据以备后用
          }));

          console.log("生成的穿梭框数据:", this.transferData);
        })
        .catch((err) => {
          console.error("加载项目列表失败:", err);
          Message.error("加载项目数据失败，请重试");
        })
        .finally(() => {
          this.isTransferLoading = false;
        });
    },

    /**
     * 穿梭框变化事件处理
     * @param {Array} value - 当前选中的项目ID数组
     * @param {string} direction - 移动方向：'left' 或 'right'
     * @param {Array} movedKeys - 被移动的项目ID数组
     */
    handleTransferChange(value, direction, movedKeys) {
      console.log("穿梭框变化:", {
        value: value, // 当前所有选中的项目ID
        direction: direction, // 移动方向
        movedKeys: movedKeys, // 本次移动的项目ID
      });

      // 更新表单中的项目ID数组
      this.form.itemIds = value;

      // 根据选中的项目自动计算套餐价格（可选功能）
      this.calculatePackagePrice();
    },

    /**
     * 根据选中的项目自动计算套餐总价
     * 遍历选中的项目，累加价格
     */
    calculatePackagePrice() {
      if (this.form.selectedItems && this.form.selectedItems.length > 0) {
        let totalPrice = 0;
        this.form.selectedItems.forEach((itemId) => {
          // 在穿梭框数据中查找对应的项目
          const transferItem = this.transferData.find(
            (item) => item.key === itemId
          );
          if (
            transferItem &&
            transferItem.originalData &&
            transferItem.originalData.itemPrice
          ) {
            totalPrice += transferItem.originalData.itemPrice;
          }
        });
        // 设置套餐价格，可以手动调整
        this.form.packagePrice = parseFloat(totalPrice.toFixed(2));
      } else {
        // 如果没有选中任何项目，价格设为0
        this.form.packagePrice = 0;
      }
    },

    // ========== 原有套餐管理方法 ==========

    fetchPackages() {
      this.loading = true;
      const params = {
        pageNum: this.currentPage - 1,
        pageSize: this.pageSize,
        packageName: this.searchForm.packageName || undefined,
        packageStatus: this.searchForm.packageStatus || undefined,
        minPrice: this.priceRange[0] || undefined,
        maxPrice: this.priceRange[1] || undefined,
      };
      examinationPage(params)
        .then((pageData) => {
          console.log(pageData);
          this.packages = pageData.list || [];
          this.total = pageData.total || 0;
          if (this.packages.length === 0) {
            Message.info("暂无套餐数据");
          }
        })
        .catch((err) => {
          console.error("查询异常:", err);
        })
        .finally(() => {
          this.loading = false;
        });
    },

    handleBack() {
      this.$router.push({ name: "ExaminationItem" });
    },

    handleSearch() {
      this.currentPage = 1;
      this.fetchPackages();
    },

    resetSearch() {
      this.searchForm = { packageName: "", packageStatus: "" };
      this.priceRange = [0, 20000];
      this.currentPage = 1;
      this.fetchPackages();
    },

    handlePriceChange() {
      this.currentPage = 1;
      this.fetchPackages();
    },

    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.fetchPackages();
    },

    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchPackages();
    },

    handleAddPackage() {
      console.log("添加套餐");
      this.dialogTitle = "新增套餐";
      this.dialogVisible = true;
      this.form = {
        packageId: null,
        packageName: "",
        packageDesc: "",
        packageStatus: "active",
        packagePrice: 0,
        selectedItems: [], // 清空已选项目
        itemIds: [], // 清空项目ID数组
      };
      if (this.$refs.form) {
        this.$refs.form.resetFields();
      }
    },

    handleEditPackage(row) {
      this.dialogTitle = "编辑套餐";
      // 设置已选项目（这里需要根据套餐已有的项目来设置）
      const selectedItems = row.itemIds || []; // 假设后端返回了itemIds字段
      this.form = {
        ...row,
        selectedItems: selectedItems,
        itemIds: selectedItems,
      };
      this.dialogVisible = true;
    },

    handleSavePackage() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 生成项目名称字符串 - 关键修改！
          let itemName = "";
          if (this.form.selectedItems && this.form.selectedItems.length > 0) {
            const selectedItemNames = this.form.selectedItems
              .map((itemId) => {
                const transferItem = this.transferData.find(
                  (item) => item.key === itemId
                );
                return transferItem ? transferItem.originalData.itemName : "";
              })
              .filter((name) => name);

            itemName = selectedItemNames.join("、");
          }

          // 准备提交数据
          const formData = {
            packageName: this.form.packageName,
            packageDesc: this.form.packageDesc,
            packageStatus: this.form.packageStatus,
            packagePrice: this.form.packagePrice,
            itemName: itemName, // 关键：传递项目名称字符串
            itemIds: this.form.selectedItems, // 可选：如果需要保存项目ID
          };

          if (this.form.packageId) {
            formData.packageId = this.form.packageId;
          }

          console.log("提交的套餐数据:", formData);

          const api = this.form.packageId ? examinationUpdate : examinationAdd;
          api(formData)
            .then(() => {
              Message.success("操作成功");
              this.dialogVisible = false;
              this.fetchPackages();
            })
            .catch((err) => {
              console.error("操作异常:", err);
            });
        }
      });
    },
    handleDeletePackage(row) {
      this.currentPackage = row;
      this.deleteDialogVisible = true;
    },

    confirmDelete() {
      examinationDelete(this.currentPackage.packageId)
        .then(() => {
          Message.success("删除成功");
          this.deleteDialogVisible = false;
          this.fetchPackages();
        })
        .catch((err) => {
          console.error("删除异常:", err);
        });
    },

    // 若有恢复接口，需补充 handleRestorePackage 方法并调用 examinationRestore
    // handleRestorePackage(row) {
    //   examinationRestore(row.packageId)
    //     .then(() => {
    //       Message.success("恢复成功");
    //       this.fetchPackages();
    //     })
    //     .catch((err) => {
    //       console.error("恢复异常:", err);
    //     });
    // },

    formatDate(dateStr) {
      if (!dateStr) return "";
      const date = new Date(dateStr);
      return `${date.getFullYear()}-${this.padZero(
        date.getMonth() + 1
      )}-${this.padZero(date.getDate())} ${this.padZero(
        date.getHours()
      )}:${this.padZero(date.getMinutes())}:${this.padZero(date.getSeconds())}`;
    },

    padZero(num) {
      return num < 10 ? `0${num}` : num;
    },
  },
};
</script>

<style scoped>
.package-management-page {
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.search-card {
  padding: 20px;
  margin-bottom: 20px;
}

.table-card {
  margin-top: 20px;
}

.price-range-text {
  text-align: center;
  margin-top: 10px;
  color: #606266;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.el-slider__marks-text {
  transform: translateX(-50%);
}

.el-table .cell {
  white-space: nowrap;
}

.el-dialog__body {
  padding: 25px;
}

.el-form-item {
  margin-bottom: 20px;
}

/* 穿梭框样式调整 */
.transfer-item {
  margin-bottom: 0;
}

.transfer-container {
  width: 100%;
}

.transfer-tips {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
  text-align: center;
}

/* 调整穿梭框高度 */
:deep(.el-transfer) {
  display: flex;
  justify-content: center;
}

:deep(.el-transfer-panel) {
  width: 200px;
}

:deep(.el-transfer__buttons) {
  padding: 0 10px;
}
</style>