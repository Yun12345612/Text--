<template>
  <div class="dept-manage-container">
    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="科室名称">
        <el-input
          v-model="searchForm.departmentName"
          placeholder="请输入科室名称"
          clearable
        />
      </el-form-item>
      <!--状态查询条件 -->
      <el-form-item label="科室状态">
        <el-select
          v-model="searchForm.departmentStatus"
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
        >新增科室</el-button
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
        <el-table-column prop="departmentName" label="科室名称" align="center" min-width="120" />
        <el-table-column prop="departmentLocation" label="位置" align="center" min-width="120" />
        <el-table-column label="状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.departmentStatus === 1 ? 'success' : 'warning'"
            >
              {{ scope.row.departmentStatus === 1 ? "启用" : "禁用" }}
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
    <el-dialog :visible.sync="dialogVisible" :title="isAdd ? '新增科室' : '编辑科室'" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="科室名称" prop="departmentName">
          <el-input
            v-model="form.departmentName"
            placeholder="请输入科室名称"
          />
        </el-form-item>
        <el-form-item label="位置" prop="departmentLocation">
          <el-input
            v-model="form.departmentLocation"
            placeholder="请输入位置"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.departmentStatus">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
import { getDeptList, addDept, updateDept, deleteDept } from "@/api/dept";

export default {
  name: "DeptManage",
  data() {
    return {
      searchForm: {
        departmentName: "",
        departmentStatus: "",
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
        departmentId: "",
        departmentName: "",
        departmentLocation: "",
        departmentStatus: 1,
      },
      isAdd: true,
      rules: {
        departmentName: [
          { required: true, message: "请输入科室名称", trigger: "blur" },
        ],
        departmentLocation: [
          { required: true, message: "请输入位置信息", trigger: "blur" },
        ],
      },
      tableMaxHeight: 400
    };
  },
  mounted() {
    this.loadDeptList();
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

    loadDeptList() {
      this.loading = true;
      const params = {
        currentPage: this.pagination.currentPage,
        pageSize: this.pagination.pageSize,
        ...this.searchForm,
      };
      console.log("加载科室列表参数:", params);

      getDeptList(params)
        .then((listData) => {
          this.tableData = listData.list || [];
          this.pagination.total = listData.total || 0;
        })
        .catch((err) => {
          console.error("加载科室列表出错:", err);
          this.tableData = [];
          this.pagination.total = 0;
        })
        .finally(() => {
          this.loading = false;
        });
    },

    handleSearch() {
      this.pagination.currentPage = 1;
      this.loadDeptList();
    },

    handleReset() {
      this.searchForm = { departmentName: "", departmentStatus: "" };
      this.pagination.currentPage = 1;
      this.loadDeptList();
    },

    handleAdd() {
      this.isAdd = true;
      this.resetForm();
      this.dialogVisible = true;
    },

    handleEdit(row) {
      this.isAdd = false;
      this.form = { ...row };
      this.dialogVisible = true;
    },

    handleDelete(row) {
      MessageBox.confirm(
        `确定删除科室【${row.departmentName}】吗？删除后不可恢复`,
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          deleteDept(row.departmentId)
            .then(() => {
              Message.success("删除成功");
              this.loadDeptList();
            })
            .catch((err) => {
              console.error("删除科室出错:", err);
            });
        })
        .catch(() => {});
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
      this.loadDeptList();
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadDeptList();
    },

    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          const apiMethod = this.isAdd ? addDept : updateDept;
          apiMethod(this.form)
            .then(() => {
              Message.success(this.isAdd ? "添加成功" : "编辑成功");
              this.dialogVisible = false;
              this.loadDeptList();
            })
            .catch((err) => {
              console.error(`${this.isAdd ? "添加" : "编辑"}科室出错:`, err);
            });
        }
      });
    },

    resetForm() {
      this.form = {
        departmentId: "",
        departmentName: "",
        departmentLocation: "",
        departmentStatus: 1,
      };
      if (this.$refs.formRef) {
        this.$refs.formRef.clearValidate();
      }
    },
  },
};
</script>

<style scoped>
.dept-manage-container {
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
  
  .dept-manage-container {
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
}
</style>