<template>
  <div class="role-manage-container">
    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="角色名">
        <el-input
          v-model="searchForm.roleName"
          placeholder="请输入角色名"
          clearable
        />
      </el-form-item>
      <el-form-item label="角色状态">
        <el-select
          v-model="searchForm.roleStatus"
          placeholder="全部状态"
          clearable
        >
          <el-option label="启用" :value="1"></el-option>
          <el-option label="禁用" :value="0"></el-option>
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
        >新增角色</el-button
      >
    </div>

    <!-- 表格区域 -->
    <el-table 
      :data="tableData" 
      border 
      style="width: 100%" 
      align="center"
    >
      <el-table-column type="index" label="序号" width="80" align="center" />
      <el-table-column prop="roleName" label="角色名" align="center" width="150" />
      <el-table-column prop="roleDesc" label="角色描述" align="center">
        <template slot-scope="scope">
          <div class="desc-cell">{{ scope.row.roleDesc || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="角色状态" align="center" width="120">
        <template slot-scope="scope">
          <el-tag :type="scope.row.roleStatus === 1 ? 'success' : 'warning'">
            {{ scope.row.roleStatus === 1 ? "启用" : "禁用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" align="center" width="180">
        <template slot-scope="scope">
          {{ scope.row.createTime ? formatTime(scope.row.createTime) : "-" }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
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

    <!-- 分页区域 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.currentPage"
      :page-sizes="[10, 20, 30]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      class="pagination-container"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog :visible.sync="dialogVisible" title="角色信息" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名" />
        </el-form-item>
        <el-form-item label="角色描述" prop="roleDesc">
          <el-input
            type="textarea"
            v-model="form.roleDesc"
            placeholder="请输入角色描述（可选）"
            rows="3"
          />
        </el-form-item>
        <el-form-item label="角色状态" prop="roleStatus">
          <el-radio-group v-model="form.roleStatus">
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
// 脚本逻辑保持不变
import { Message, MessageBox } from "element-ui";
import {
  roleGetById,
  roleGetList,
  roleAdd,
  roleUpdate,
  roleSoftDelete,
} from "@/api/role.js";

export default {
  name: "RoleManage",
  data() {
    return {
      searchForm: { roleName: "", roleStatus: "" },
      tableData: [],
      pagination: { currentPage: 1, pageSize: 10, total: 0 },
      dialogVisible: false,
      isAdd: true,
      form: { roleId: "", roleName: "", roleStatus: 1, roleDesc: "" },
      rules: {
        roleName: [
          { required: true, message: "请输入角色名", trigger: "blur" },
          { min: 2, max: 20, message: "角色名长度需在2-20字符之间", trigger: "blur" },
        ],
        roleStatus: [
          { required: true, message: "请选择角色状态", trigger: "change" },
        ],
      },
    };
  },
  mounted() {
    this.loadRoleList();
  },
  methods: {
    formatTime(time) {
      const date = new Date(time);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hour = String(date.getHours()).padStart(2, "0");
      const minute = String(date.getMinutes()).padStart(2, "0");
      const second = String(date.getSeconds()).padStart(2, "0");
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
    },
    loadRoleList() {
      const params = {
        pageNum: (this.pagination.currentPage - 1) * this.pagination.pageSize,
        pageSize: this.pagination.pageSize,
        roleName: this.searchForm.roleName,
        roleStatus: this.searchForm.roleStatus,
      };
      roleGetList(params)
        .then((listData) => {
          this.tableData = listData.list || [];
          this.pagination.total = listData.total || 0;
        })
        .catch((err) => {
          console.error("角色列表查询异常:", err);
        });
    },
    handleSearch() {
      this.pagination.currentPage = 1;
      this.loadRoleList();
    },
    handleReset() {
      this.searchForm = { roleName: "", roleStatus: "" };
      this.pagination.currentPage = 1;
      this.loadRoleList();
    },
    handleAdd() {
      this.isAdd = true;
      this.resetForm();
      this.dialogVisible = true;
    },
    handleEdit(row) {
      this.isAdd = false;
      roleGetById(row.roleId)
        .then((roleData) => {
          this.form = {
            roleId: roleData.roleId,
            roleName: roleData.roleName || "",
            roleStatus: roleData.roleStatus || 1,
            roleDesc: roleData.roleDesc || "",
          };
          this.dialogVisible = true;
        })
        .catch((err) => {
          console.error("获取角色详情异常:", err);
        });
    },
    handleDelete(row) {
      MessageBox.confirm(
        `确定删除角色【${row.roleName}】吗？删除后不可恢复！`,
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          center: true,
        }
      )
        .then(() => {
          roleSoftDelete(row.roleId)
            .then(() => {
              Message.success("角色删除成功");
              this.loadRoleList();
            })
            .catch((err) => {
              console.error("角色删除异常:", err);
            });
        })
        .catch(() => {
          Message.info("已取消角色删除");
        });
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
      this.loadRoleList();
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadRoleList();
    },
    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          const apiMethod = this.isAdd ? roleAdd : roleUpdate;
          apiMethod(this.form)
            .then(() => {
              Message.success(this.isAdd ? "角色新增成功" : "角色编辑成功");
              this.dialogVisible = false;
              this.loadRoleList();
            })
            .catch((err) => {
              console.error(`${this.isAdd ? "新增" : "编辑"}角色异常:`, err);
            });
        }
      });
    },
    resetForm() {
      this.form = {
        roleId: "",
        roleName: "",
        roleStatus: 1,
        roleDesc: "",
      };
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields();
      }
    },
  },
};
</script>

<style scoped>
/* 移除多余的白色背景，只保留必要的样式 */
.role-manage-container {
  padding: 20px;
  min-height: calc(100vh - 40px);
  box-sizing: border-box;
}

/* 搜索区域样式简化 */


/* 操作按钮区域样式 */
.action-buttons {
  margin-bottom: 20px;
}

/* 表格样式优化 */
.el-table {
  margin-bottom: 20px;
}

/* 角色描述列自动换行 */
.desc-cell {
  white-space: normal;
  word-break: break-all;
  line-height: 1.5;
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  text-align: right;
}

/* 弹窗底部按钮样式 */
.dialog-footer {
  text-align: right;
}

</style>