<template>
  <div class="project-management-page">
    <!-- 页面标题 -->
    <el-page-header @back="handlePackageBack" content="项目管理"> </el-page-header>

    <!-- 操作区和搜索区 -->
    <div class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.itemName"
            placeholder="请输入项目名称"
            clearable
            prefix-icon="el-icon-search"
          ></el-input>
        </el-col>

        <!-- 科室筛选：改为通过科室ID关联，这里暂用输入框演示，实际需下拉选择 -->
        <el-col :span="6">
          <el-input
            v-model="searchForm.departmentName"
            placeholder="请输入科室名称"
            clearable
            prefix-icon="el-icon-building"
          ></el-input>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="searchForm.itemStatus"
            placeholder="请选择项目状态"
            clearable
          >
            <el-option label="全部" value=""></el-option>
            <el-option label="启用" value="active"></el-option>
            <el-option label="停用" value="inactive"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
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
            :max="10000"
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
            @click="handleAddProject"
          >
            新增项目
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 项目列表 -->
    <div class="table-container">
      <el-table
        :data="paginatedProjects"
        border
        stripe
        :loading="loading"
        style="width: 100%"
        :max-height="tableMaxHeight"
      >
      <!-- “序号”列 -->
    <!-- “序号”列，添加 align="center" 实现居中 -->
<el-table-column label="序号" min-width="80" align="center">
  <template slot-scope="scope">
    {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
  </template>
</el-table-column>
        <!-- 项目名称：平级列 -->
        <el-table-column
          prop="itemName"
          label="项目名称"
          min-width="180"
        ></el-table-column>
        <!-- 明细名称：平级列 -->
        <el-table-column
          prop="detailName"
          label="明细名称"
          min-width="180"
        ></el-table-column>
        <!-- 明细描述：平级列 -->
        <el-table-column
          prop="detailDesc"
          label="明细描述"
          min-width="300"
        ></el-table-column>
        <!-- 科室名称：平级列 -->
        <el-table-column
          prop="departmentName"
          label="科室名称"
          min-width="150"
        ></el-table-column>
        <!-- 项目状态：平级列 -->
        <el-table-column prop="itemStatus" label="项目状态" min-width="120">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.itemStatus === 'active' ? 'success' : 'info'"
            >
              {{ scope.row.itemStatus === "active" ? "启用" : "停用" }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 价格：平级列 -->
        <el-table-column prop="itemPrice" label="价格" min-width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.itemPrice.toFixed(2) }}
          </template>
        </el-table-column>
        <!-- 创建时间：平级列 -->
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <!-- 操作列：平级列 -->
        <el-table-column label="操作" min-width="180" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleEditProject(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              :style="
                scope.row.itemIsDelete === 1
                  ? 'color: #606266'
                  : 'color: #F56C6C'
              "
              @click="handleDeleteProject(scope.row)"
              :disabled="scope.row.itemIsDelete === 1"
            >
              {{ scope.row.itemIsDelete === 1 ? "已删除" : "删除" }}
            </el-button>
            <el-button
              size="mini"
              type="text"
              v-if="scope.row.itemIsDelete === 1"
              @click="handleRestoreProject(scope.row)"
            >
              恢复
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

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

    <!-- 新增/编辑项目弹窗 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :width="'800px'"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="form.itemName"></el-input>
        </el-form-item>
        <!--明细名称字段 -->
        <el-form-item label="明细名称" prop="detailName">
          <el-input
            v-model="form.detailName"
            placeholder="请输入明细名称"
          ></el-input>
        </el-form-item>

        <!--明细描述字段 -->
        <el-form-item label="明细描述" prop="detailDesc">
          <el-input
            type="textarea"
            v-model="form.detailDesc"
            placeholder="请输入明细描述"
            :rows="3"
          ></el-input>
        </el-form-item>
        <el-form-item label="科室" prop="departmentId">
          <el-select
            v-model="form.departmentId"
            placeholder="请选择科室"
            clearable
            style="width: 100%"
            :loading="isDeptLoading"
          >
            <el-option
              v-for="dept in departmentList"
              :key="dept.departmentId"
              :label="dept.departmentName"
              :value="dept.departmentId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="项目状态" prop="itemStatus">
          <el-select v-model="form.itemStatus">
            <el-option label="启用" value="active"></el-option>
            <el-option label="停用" value="inactive"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="项目价格" prop="itemPrice">
          <el-input
            v-model.number="form.itemPrice"
            type="number"
            min="0"
            step="0.01"
          >
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>

        <!--穿梭框表单项 -->
        <el-form-item label="项目明细" class="transfer-item">
          <div class="transfer-container">
            <el-transfer
              v-model="form.selectedItems"
              :data="transferData"
              :titles="['可选明细', '已选明细']"
              :button-texts="['移除', '添加']"
              filterable
              filter-placeholder="请输入明细名称"
              @change="handleTransferChange"
            ></el-transfer>
          </div>
          <div class="transfer-tips">
            已选择
            {{ form.selectedItems ? form.selectedItems.length : 0 }} 个明细项目
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProject">保存</el-button>
      </div>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog
      title="确认删除"
      :visible.sync="deleteDialogVisible"
      :width="'400px'"
      :close-on-click-modal="false"
    >
      <p>确定要删除该项目吗？删除后可以在列表中恢复。</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确定删除</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 1. 引入接口文件
import {
  getItemList,
  addItem,
  updateItem,
  softDeleteItem,
  restoreItem,
  getDepartmentList,
  getAllExaminationItemDetails, // 新增：引入获取明细的API
} from "@/api/item.js";
// 2. 引入element-ui的Message
import { Message } from "element-ui";

export default {
  data() {
    return {
      // 科室列表数据
      departmentList: [],
      // 明细列表数据（动态获取）
      examinationDetails: [],
      isDeptLoading: false,
      isDetailLoading: false,
      transferData: [], // 改为空数组，动态填充
      projects: [],
      searchForm: {
        itemName: "",
        departmentName: "",
        itemStatus: "",
      },
      priceRange: [0, 10000],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: "新增项目",
      form: {
        detailName: "", // 新增：明细名称
        detailDesc: "", // 新增：明细描述
        departmentName: "",
        itemId: null,
        itemName: "",
        departmentId: null,
        itemStatus: "active",
        itemPrice: 0,
        selectedItems: [], // 存储选中的明细项目ID
      },
      rules: {
        itemName: [
          { required: true, message: "请输入项目名称", trigger: "blur" },
          {
            min: 2,
            max: 50,
            message: "长度在 2 到 50 个字符",
            trigger: "blur",
          },
        ],
        detailName: [
          // 新增：明细名称验证
          { required: true, message: "请输入明细名称", trigger: "blur" },
          {
            min: 2,
            max: 100,
            message: "长度在 2 到 100 个字符",
            trigger: "blur",
          },
        ],
        detailDesc: [
          // 新增：明细描述验证
          { max: 500, message: "长度不能超过 500 个字符", trigger: "blur" },
        ],
        departmentId: [
          { required: true, message: "请选择科室", trigger: "change" },
        ],
        itemStatus: [
          { required: true, message: "请选择项目状态", trigger: "change" },
        ],
        itemPrice: [
          { required: true, message: "请输入项目价格", trigger: "blur" },
          {
            type: "number",
            min: 0,
            message: "价格不能为负数",
            trigger: "blur",
          },
        ],
      },
      deleteDialogVisible: false,
      currentProject: null,
      tableMaxHeight: 400
    };
  },
  computed: {
    filteredProjects() {
      return this.projects.filter((project) => {
        const itemName = project.itemName || "";
        const searchItemName = this.searchForm.itemName || "";
        const nameMatch = itemName
          .toString()
          .toLowerCase()
          .includes(searchItemName.toLowerCase());

        const statusMatch =
          !this.searchForm.itemStatus ||
          project.itemStatus === this.searchForm.itemStatus;
        const priceMatch =
          project.itemPrice >= this.priceRange[0] &&
          project.itemPrice <= this.priceRange[1];
        return nameMatch && statusMatch && priceMatch;
      });
    },

    paginatedProjects() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      return this.filteredProjects.slice(
        startIndex,
        startIndex + this.pageSize
      );
    },
  },
  created() {
    this.fetchProjects();
    this.loadDepartmentList();
    this.loadExaminationDetails(); // 新增：加载明细数据
  },
  mounted() {
    this.calculateTableHeight();
    window.addEventListener('resize', this.calculateTableHeight);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.calculateTableHeight);
  },
  methods: {
    // 计算表格高度
    calculateTableHeight() {
      const windowHeight = window.innerHeight;
      const headerHeight = 80;
      const searchHeight = 120;
      const paginationHeight = 60;
      const padding = 80;
      
      let calculatedHeight = windowHeight - headerHeight - searchHeight - paginationHeight - padding;
      
      if (calculatedHeight < 200) {
        calculatedHeight = 200;
      } else if (calculatedHeight > 600) {
        calculatedHeight = 600;
      }
      
      this.tableMaxHeight = calculatedHeight;
    },

    // 1. 获取科室列表
    loadDepartmentList() {
      this.isDeptLoading = true;
      getDepartmentList()
        .then((deptData) => {
          console.log("科室API响应:", deptData);
          this.departmentList = deptData.list || [];
          console.log("科室数据:", this.departmentList);
        })
        .catch((err) => {
          console.error("加载科室列表出错:", err);
        })
        .finally(() => {
          this.isDeptLoading = false;
        });
    },

    // 2. 新增：动态获取检查项目明细
    loadExaminationDetails() {
      this.isDetailLoading = true;
      getAllExaminationItemDetails()
        .then((response) => {
          console.log("明细API响应:", response);

          // 根据实际API响应结构调整
          const details = response.data || response.list || response || [];
          this.examinationDetails = details;

          // 生成穿梭框数据
          this.transferData = details.map((detail, index) => ({
            key: detail.detailId || detail.id || index, // 使用明细ID作为key
            label: detail.detailName || detail.name || `明细${index + 1}`, // 使用明细名称
            disabled: false,
            // 可以添加其他需要的字段
            originalData: detail, // 保存原始数据以备后用
          }));

          console.log("生成的穿梭框数据:", this.transferData);
        })
        .catch((err) => {
          console.error("加载检查项目明细出错:", err);
          Message.error("加载明细数据失败");
        })
        .finally(() => {
          this.isDetailLoading = false;
        });
    },

    handleTransferChange(value, direction, movedKeys) {
      console.log("穿梭框变化:", value, direction, movedKeys);
      console.log("当前选中的明细ID:", value);

      // 如果需要，可以在这里处理选中变化逻辑
      if (direction === "right") {
        // 添加操作
        console.log("添加的明细:", movedKeys);
      } else if (direction === "left") {
        // 移除操作
        console.log("移除的明细:", movedKeys);
      }
    },

    fetchProjects() {
      this.loading = true;

      const params = {
        pageNum: this.currentPage - 1,
        pageSize: this.pageSize,
        itemName: this.searchForm.itemName || undefined,
        departmentId: this.searchForm.departmentId || undefined,
        itemStatus: this.searchForm.itemStatus || undefined,
        minPrice: this.priceRange[0] || undefined,
        maxPrice: this.priceRange[1] || undefined,
      };
      console.log("请求参数:", params);

      getItemList(params)
        .then((pageData) => {
          console.log("API响应:", pageData);
          this.projects = pageData.list || [];
          this.total = pageData.total || 0;

          if (this.projects.length === 0) {
            Message.info("暂无项目数据");
          }
        })
        .catch((err) => {
          console.error("查询异常:", err);
        })
        .finally(() => {
          this.loading = false;
        });
    },

    //跳转到套餐管理页面
    handlePackageBack() {
      this.$router.push({ name: "PackageItem" });
    },

    handleSearch() {
      this.currentPage = 1;
      this.fetchProjects();
    },

    resetSearch() {
      this.searchForm = { itemName: "", departmentId: "", itemStatus: "" };
      this.priceRange = [0, 10000];
      this.currentPage = 1;
      this.fetchProjects();
    },

    handlePriceChange() {
      this.currentPage = 1;
      this.fetchProjects();
    },

    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.fetchProjects();
    },

    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchProjects();
    },

    handleAddProject() {
      this.dialogTitle = "新增项目";
      this.form = {
        detailName: "", // 确保包含明细名称
        detailDesc: "", // 确保包含明细描述
        itemId: null,
        itemName: "",
        departmentId: null,
        itemStatus: "active",
        itemPrice: 0,
        selectedItems: [], // 清空已选明细
      };
      this.dialogVisible = true;
    },

    handleEditProject(row) {
    
      this.dialogTitle = "编辑项目";
      // 根据实际情况设置已选项目
      // 这里需要根据项目已有的明细来设置 selectedItems
       
      const selectedItems = row.detailIds || []; // 假设后端返回了detailIds字段
      this.form = {
        ...row,
        departmentId: row.departmentId,
        selectedItems: selectedItems,
      };
      this.dialogVisible = true;
    },

    handleSaveProject() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          console.log("选中的明细ID:", this.form.selectedItems);

          // 准备提交数据
          const formData = {
            itemId: this.form.itemId,
            itemName: this.form.itemName,
            detailName: this.form.detailName, // 确保包含明细名称
            detailDesc: this.form.detailDesc, // 确保包含明细描述
            departmentId: this.form.departmentId,
            itemStatus: this.form.itemStatus,
            itemPrice: this.form.itemPrice,
            selectedItems: this.form.selectedItems,
            ...this.form,
            // 可以根据需要添加明细相关字段
            detailIds: this.form.selectedItems, // 传递选中的明细ID数组
          };

          const api = this.form.itemId ? updateItem : addItem;
          api(formData)
            .then(() => {
              Message.success("操作成功");
              this.dialogVisible = false;
              this.fetchProjects();
            })
            .catch((err) => {
              console.error("操作异常:", err);
              if (err.response && err.response.data) {
                Message.error(
                  `操作失败: ${err.response.data.message || "未知错误"}`
                );
              }
            });
        }
      });
    },

    // 其他方法...
    handleDeleteProject(row) {
      this.currentProject = row;
      this.deleteDialogVisible = true;
    },

    confirmDelete() {
      softDeleteItem(this.currentProject.itemId)
        .then(() => {
          Message.success("删除成功");
          this.deleteDialogVisible = false;
          this.fetchProjects();
        })
        .catch((err) => {
          console.error("删除异常:", err);
        });
    },

    handleRestoreProject(row) {
      restoreItem(row.itemId)
        .then(() => {
          Message.success("恢复成功");
          this.fetchProjects();
        })
        .catch((err) => {
          console.error("恢复异常:", err);
        });
    },

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
.project-management-page {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.search-card {
  flex-shrink: 0;
  padding: 20px;
  margin-bottom: 20px;
}

.table-container {
  flex: 1;
  overflow: hidden;
  margin-bottom: 0;
  min-height: 200px;
}

.pagination-container {
  flex-shrink: 0;
  margin-top: 20px;
  text-align: right;
  padding: 10px 0;
}

.price-range-text {
  text-align: center;
  margin-top: 10px;
  color: #606266;
}

.text-right {
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