<template>
  <div class="admin-manage-container">
    <!-- 搜索区域：按“用户名（name）”搜索 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="用户名">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入用户名"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮区域 -->
    <div class="action-buttons">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd"
        >新增管理员</el-button
      >
    </div>

    <!-- 表格区域：显示“用户名（name）” -->
    <el-table :data="tableData" border style="width: 100%" align="center">
      <el-table-column type="index" label="序号" width="80" align="center" />
      <!-- 关键：用户名绑定实体类的name字段 -->
      <el-table-column prop="name" label="用户名" align="center" />
      <el-table-column prop="roleName" label="角色" align="center" />
      <el-table-column label="状态" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
            {{ scope.row.status === 1 ? "启用" : "禁用" }}
          </el-tag>
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
      style="margin-top: 20px; text-align: right"
    />

    <!-- 新增/编辑弹窗：区分账号（account）和用户名（name） -->
    <el-dialog :visible.sync="dialogVisible" title="管理员信息" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <!-- 账号（account）：登录用，唯一 -->
        <el-form-item label="账号" prop="account">
          <el-input v-model="form.account" placeholder="请输入登录账号" />
        </el-form-item>
        <!-- 用户名（name）：显示用 -->
        <el-form-item label="用户名" prop="name">
          <el-input v-model="form.name" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="isAdd">
          <el-input
            type="password"
            v-model="form.password"
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色">
            <el-option label="超级管理员" :value="1"></el-option>
            <el-option label="普通管理员" :value="6"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
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
import {
  adminLogin, // 管理员登录（后端：/admin/login）
  adminUpdate, // 管理员修改（后端：/admin/update）
  adminList, // 管理员列表查询（后端：/admin/list）
  getCode, // 获取验证码（后端：/get/code）
  adminDelete, // 管理员软删除（后端：/admin/delete/{id}）
  adminAdd, // 新增管理员（后端：/admin/add）
} from "@/api/admin.js";

export default {
  name: "AdminManage",
  data() {
    return {
      // 搜索条件：按用户名（name）搜索
      searchForm: {
        name: "",
      },
      tableData: [], // 表格数据：后端返回的Admin列表，包含name字段
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      dialogVisible: false,
      // 表单数据：区分账号（account）和用户名（name）
      form: {
        id: "", // 实体类：Long
        account: "", // 实体类：账号（登录用）
        name: "", // 实体类：用户名（显示用）
        password: "", // 实体类：密码
        //roleName: 1,         // 实体类：角色ID（long）
        roleId: 1,
        status: 1, // 实体类：状态（long）
      },
      isAdd: true,
      // 校验规则：同时校验账号和用户名
      rules: {
        account: [{ required: true, message: "请输入账号", trigger: "blur" }],
        name: [{ required: true, message: "请输入用户名", trigger: "blur" }],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "密码长度至少6位", trigger: "blur" },
        ],
        roleId: [{ required: true, message: "请选择角色", trigger: "blur" }],
      },
    };
  },
  mounted() {
    this.loadAdminList();
  },
  methods: {
    /**
     * 加载管理员列表：使用拦截器后的新写法
     */
    loadAdminList() {
      const params = {
        pageNum: this.pagination.currentPage,
        pageSize: this.pagination.pageSize,
        name: this.searchForm.name,
      };
      console.log("查询参数:", params);

      adminList(params)
        .then((listData) => {
          // ✅ 拦截器已经处理了响应格式，直接使用业务数据
          this.tableData = listData.list || [];
          this.pagination.total = listData.total || 0;
        })
        .catch((err) => {
          console.error("查询异常:", err);
          // ❌ 错误信息拦截器已经统一处理了
        });
    },

    handleSearch() {
      this.pagination.currentPage = 1;
      this.loadAdminList();
    },

    handleReset() {
      this.searchForm = { name: "" };
      this.pagination.currentPage = 1;
      this.loadAdminList();
    },

    handleAdd() {
      this.isAdd = true;
      this.resetForm();
      this.dialogVisible = true;
    },

    /**
     * 编辑回显：正确绑定账号（account）和用户名（name）
     */
    handleEdit(row) {
      this.isAdd = false;
      this.form = {
        id: row.id,
        account: row.account,
        name: row.name,
        roleId: row.roleId,
        status: row.status,
        password: "",
      };
      this.dialogVisible = true;
    },

    /**
     * 删除提示：显示用户名（name）
     */
    handleDelete(row) {
      MessageBox.confirm(
        `确定删除管理员【${row.name}】吗？`,
        "警告",
        { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" }
      )
        .then(() => {
          adminDelete(row.id)
            .then(() => {
              // ✅ 成功情况：拦截器已经处理，直接执行成功逻辑
              Message.success("删除成功");
              this.loadAdminList();
            })
            .catch((err) => {
              console.error("删除异常:", err);
              // ❌ 错误信息拦截器已经提示过了
            });
        })
        .catch(() => {});
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.loadAdminList();
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadAdminList();
    },

    /**
     * 提交表单：使用拦截器后的新写法
     */
    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          const apiMethod = this.isAdd ? adminAdd : adminUpdate;
          apiMethod(this.form)
            .then(() => {
              // ✅ 成功情况：拦截器已经处理了响应
              Message.success(this.isAdd ? "新增成功" : "编辑成功");
              this.dialogVisible = false;
              this.loadAdminList();
            })
            .catch((err) => {
              console.error("操作异常:", err);
              // ❌ 错误信息拦截器已经提示过了
            });
        }
      });
    },

    /**
     * 重置表单：清空账号和用户名
     */
    resetForm() {
      this.form = {
        id: "",
        account: "",
        name: "",
        password: "",
        roleId: 1,
        status: 1,
      };
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields();
      }
    },

    getVerifyCode() {
      getCode()
        .then(() => {
          // ✅ 直接处理成功逻辑，不需要参数名
          // 验证码逻辑
        })
        .catch((err) => {
          console.error("验证码异常:", err);
        });
    },

    adminLoginHandle(loginForm) {
      adminLogin(loginForm)
        .then(() => {
          // ✅ 直接处理成功逻辑，不需要参数名
          Message.success("登录成功");
          // 处理登录成功逻辑，如跳转页面等
        })
        .catch((err) => {
          console.error("登录异常:", err);
        });
    },
  }}
</script>

<style scoped>
.admin-manage-container {
  padding: 20px;
  min-height: calc(100vh - 40px);
}

.search-form {
  margin-bottom: 20px;
}

.action-buttons {
  margin-bottom: 20px;
}

.el-table {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>