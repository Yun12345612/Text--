<template>
  <div class="detail-manage-container">
    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="细项名称">
        <el-input
          v-model="searchForm.detailName"
          placeholder="请输入细项名称"
          clearable
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select
          v-model="searchForm.detailStatus"
          placeholder="全部状态"
          clearable
        >
          <el-option label="启用" value="1"></el-option>
          <el-option label="禁用" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮区域 -->
    <div class="action-buttons">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd"
        >新增细项</el-button
      >
    </div>

    <!-- 表格区域 -->
    <div class="table-container">
      <el-table 
        :data="tableData" 
        border 
        style="width: 100%" 
        align="center"
        :max-height="tableMaxHeight"
        v-loading="loading"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="detailName" label="细项名称" align="center" min-width="120" />
        <el-table-column prop="detailUnit" label="单位" align="center" width="100" />
        <el-table-column prop="departmentName" label="科室" align="center" min-width="120" />
        <el-table-column prop="minValue" label="最小值" align="center" width="100">
          <template slot-scope="scope">
            {{ scope.row.minValue || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="maxValue" label="最大值" align="center" width="100">
          <template slot-scope="scope">
            {{ scope.row.maxValue || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="detailDesc" label="描述" align="center" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.detailStatus === '1' ? 'success' : 'warning'"
            >
              {{ scope.row.detailStatus === "1" ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              type="text"
              style="color: #f56c6c"
              @click="handleDelete(scope.row)"
              >删除</el-button
            >
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
        :page-sizes="[10, 20, 30]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :visible.sync="dialogVisible"
      :title="isAdd ? '新增细项' : '编辑细项'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="细项名称" prop="detailName">
          <el-input v-model="form.detailName" placeholder="请输入细项名称" />
        </el-form-item>
        <el-form-item label="单位" prop="detailUnit">
          <el-input v-model="form.detailUnit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="科室" prop="departmentName">
          <el-select
            v-model="form.departmentName"
            placeholder="请选择科室"
            clearable
            style="width: 100%"
            :loading="isDeptLoading"
          >
            <el-option
              v-for="dept in departmentList"
              :key="dept.departmentId"
              :label="dept.departmentName"
              :value="dept.departmentName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="最小值" prop="minValue">
          <el-input-number
            v-model="form.minValue"
            placeholder="请输入最小值"
            :precision="2"
            :step="0.1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="最大值" prop="maxValue">
          <el-input-number
            v-model="form.maxValue"
            placeholder="请输入最大值"
            :precision="2"
            :step="0.1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.detailDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入描述信息"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.detailStatus">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message, MessageBox } from "element-ui";
import {
  addExaminationItemDetail,
  deleteExaminationItemDetail,
  updateExaminationItemDetail,
  getExaminationItemDetailsByCondition,
  getExaminationItemDetailsByPage,
  getDepartmentList,
} from "@/api/detail.js";

export default {
  name: "DetailManage",
  data() {
    return {
      departmentList: [],
      isDeptLoading: false,
      searchForm: {
        detailName: "",
        detailStatus: "",
      },
      tableData: [],
      loading: false,
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      dialogVisible: false,
      form: {
        detailId: "",
        detailName: "",
        detailUnit: "",
        minValue: null,
        maxValue: null,
        detailDesc: "",
        detailStatus: "1",
        departmentName: "",
      },
      isAdd: true,
      rules: {
        detailName: [
          { required: true, message: "请输入细项名称", trigger: "blur" },
        ],
        detailUnit: [
          { required: true, message: "请输入单位", trigger: "blur" },
        ],
        departmentName: [
          { required: true, message: "请选择科室", trigger: "change" },
        ],
        minValue: [
          { required: true, message: "请输入最小值", trigger: "blur" },
        ],
        maxValue: [
          {
            required: true,
            message: "请输入最大值",
            trigger: "blur",
          },
          {
            validator: (rule, value, callback) => {
              if (
                this.form.minValue !== null &&
                value !== null &&
                value <= this.form.minValue
              ) {
                callback(new Error("最大值必须大于最小值"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
      },
      tableMaxHeight: 400
    };
  },
  created() {
    this.loadDepartmentList();
    this.loadDetailList(); 
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
      const headerHeight = 60; // 搜索区域高度
      const searchHeight = 60; // 操作按钮高度
      const paginationHeight = 60; // 分页高度
      const padding = 80; // 内边距和间距
      
      let calculatedHeight = windowHeight - headerHeight - searchHeight - paginationHeight - padding;
      
      // 设置最小和最大高度限制
      if (calculatedHeight < 200) {
        calculatedHeight = 200;
      } else if (calculatedHeight > 600) {
        calculatedHeight = 600;
      }
      
      this.tableMaxHeight = calculatedHeight;
    },

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
    
    loadDetailList() {
      this.loading = true;
      const params = {
        pageNum: this.pagination.currentPage,
        pageSize: this.pagination.pageSize,
      };

      if (this.searchForm.detailName || this.searchForm.detailStatus) {
        const condition = { ...this.searchForm };
        getExaminationItemDetailsByCondition(condition)
          .then((listData) => {
            console.log("条件查询响应:", listData);
            this.tableData = listData || [];
            this.pagination.total = this.tableData.length;
          })
          .catch((err) => {
            console.error("加载细项列表出错:", err);
            this.tableData = [];
            this.pagination.total = 0;
          })
          .finally(() => {
            this.loading = false;
          });
      } else {
        getExaminationItemDetailsByPage(params)
          .then((pageData) => {
            console.log("分页查询响应:", pageData);
            this.tableData = pageData.list || [];
            this.pagination.total = pageData.total || 0;
          })
          .catch((err) => {
            console.error("加载细项列表出错:", err);
            this.tableData = [];
            this.pagination.total = 0;
          })
          .finally(() => {
            this.loading = false;
          });
      }
    },

    handleSearch() {
      this.pagination.currentPage = 1;
      this.loadDetailList();
    },

    handleReset() {
      this.searchForm = {
        detailName: "",
        detailStatus: "",
      };
      this.pagination.currentPage = 1;
      this.loadDetailList();
    },

    handleAdd() {
      this.isAdd = true;
      this.resetForm();
      this.dialogVisible = true;
    },

    handleEdit(row) {
      this.isAdd = false;
      this.form = {
        ...row,
        detailStatus: row.detailStatus.toString(),
        departmentName: row.departmentName || "",
      };
      console.log("编辑数据:", this.form);
      this.dialogVisible = true;
    },

    handleDelete(row) {
      MessageBox.confirm(
        `确定删除细项【${row.detailName}】吗？删除后不可恢复`,
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          deleteExaminationItemDetail(row.detailId)
            .then(() => {
              Message.success("删除成功");
              this.loadDetailList();
            })
            .catch((err) => {
              console.error("删除细项出错:", err);
            });
        })
        .catch(() => {});
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
      this.loadDetailList();
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadDetailList();
    },

    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          console.log("提交数据:", this.form);

          const submitData = {
            ...this.form,
            minValue: this.form.minValue !== null ? Number(this.form.minValue) : 0,
            maxValue: this.form.maxValue !== null ? Number(this.form.maxValue) : 0,
            detailStatus: Number(this.form.detailStatus),
          };

          const apiMethod = this.isAdd
            ? addExaminationItemDetail
            : updateExaminationItemDetail;

          apiMethod(submitData)
            .then(() => {
              Message.success(this.isAdd ? "添加成功" : "编辑成功");
              this.dialogVisible = false;
              this.loadDetailList();
            })
            .catch((err) => {
              console.error(`${this.isAdd ? "添加" : "编辑"}细项出错:`, err);
            });
        }
      });
    },

    resetForm() {
      this.form = {
        detailId: "",
        detailName: "",
        detailUnit: "",
        minValue: null,
        maxValue: null,
        detailDesc: "",
        detailStatus: "1",
        departmentName: "",
      };
      if (this.$refs.formRef) {
        this.$refs.formRef.clearValidate();
      }
    },
  },
};
</script>

<style scoped>
.detail-manage-container {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-sizing: border-box;
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

.action-buttons {
  flex-shrink: 0;
  margin-bottom: 20px;
}

/* 表格容器 - 关键样式 */
.table-container {
  flex: 1;
  overflow: hidden;
  margin-bottom: 0;
  min-height: 200px;
}

/* 表格样式 */
.el-table {
  width: 100%;
  margin-bottom: 0;
}

/* 分页容器 */
.pagination-container {
  flex-shrink: 0;
  margin-top: 20px;
  text-align: right;
  padding: 10px 0;
}

.dialog-footer {
  text-align: right;
}

/* 自定义滚动条样式 */
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

/* 响应式设计 */
@media (max-height: 700px) {
  .table-container {
    min-height: 150px;
  }
  
  .detail-manage-container {
    padding: 15px;
  }
}

@media (max-width: 1200px) {
  .search-form .el-form-item {
    margin-right: 15px;
    margin-bottom: 8px;
  }
}

/* 小屏幕适配 */
@media (max-width: 768px) {
  .search-form {
    display: flex;
    flex-wrap: wrap;
  }
  
  .search-form .el-form-item {
    margin-right: 10px;
    margin-bottom: 10px;
    flex: 1;
    min-width: 150px;
  }
  
  .action-buttons {
    text-align: center;
  }
  
  /* 表格在小屏幕上显示滚动条 */
  .table-container {
    overflow-x: auto;
  }
}

/* 弹窗内容滚动条 */
.el-dialog__body {
  max-height: 60vh;
  overflow-y: auto;
}

.el-dialog__body::-webkit-scrollbar {
  width: 6px;
}

.el-dialog__body::-webkit-scrollbar-track {
  background: #f8f9fa;
}

.el-dialog__body::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}
</style>