<template>
  <div class="user-manage-container">
    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="姓名">
        <el-input
          v-model="searchForm.userName"
          placeholder="请输入姓名"
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

    <!-- 操作按钮区域 -->
    <div class="action-buttons">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd"
        >添加用户</el-button
      >
      <el-button type="success" icon="el-icon-upload2" @click="handleImport"
        >批量导入</el-button
      >
      <el-button
        type="warning"
        icon="el-icon-download"
        @click="handleDownloadTemplate"
        >下载模板</el-button
      >
      <el-button icon="el-icon-download" @click="handleExport"
        >导出数据</el-button
      >
    </div>

    <!-- 表格区域 -->
    <el-table :data="tableData" border style="width: 100%" align="center">
      <el-table-column type="index" label="序号" width="80" align="center" />
      <el-table-column prop="userName" label="姓名" align="center" />
      <el-table-column prop="userAge" label="年龄" align="center" width="80" />
      <el-table-column prop="userGender" label="性别" align="center" width="80">
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
      <el-table-column prop="userEmail" label="邮箱" align="center" />
      <el-table-column prop="userAccount" label="账号" align="center" />
      <el-table-column
        prop="userBalance"
        label="余额"
        align="center"
        width="100"
      >
        <template slot-scope="scope">
          ¥{{ scope.row.userBalance || "0.00" }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.userStatus === 1 ? 'success' : 'warning'">
            {{ scope.row.userStatus === 1 ? "启用" : "禁用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="创建时间"
        align="center"
        width="160"
      />
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
      :page-sizes="[10, 20, 30, 50]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      style="margin-top: 20px; text-align: right"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :visible.sync="dialogVisible"
      :title="isAdd ? '新增用户' : '编辑用户'"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="userPhone">
          <el-input v-model="form.userPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号" prop="userCard">
          <el-input v-model="form.userCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="性别" prop="userGender">
          <el-radio-group v-model="form.userGender">
            <el-radio label="1">男</el-radio>
            <el-radio label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="userAge">
          <el-input-number
            v-model="form.userAge"
            :min="0"
            :max="150"
            placeholder="请输入年龄"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="userEmail">
          <el-input v-model="form.userEmail" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="账号" prop="userAccount">
          <el-input v-model="form.userAccount" placeholder="请输入登录账号" />
        </el-form-item>
        <el-form-item label="密码" prop="userPassword">
          <el-input
            type="password"
            v-model="form.userPassword"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <!--确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            type="password"
            v-model="form.confirmPassword"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="余额" prop="userBalance">
          <el-input-number
            v-model="form.userBalance"
            :min="0"
            :precision="2"
            placeholder="请输入余额"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.userStatus">
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

    <!-- Excel导入弹窗（保持不变） -->
    <el-dialog
      title="批量导入用户"
      :visible.sync="importDialogVisible"
      width="600px"
      @close="handleImportClose"
    >
      <div class="import-container">
        <!-- 步骤提示 -->
        <div class="import-steps">
          <el-steps :active="importStep" align-center>
            <el-step title="下载模板" description="下载Excel导入模板"></el-step>
            <el-step title="上传文件" description="填写数据并上传"></el-step>
            <el-step title="导入结果" description="查看导入结果"></el-step>
          </el-steps>
        </div>

        <!-- 步骤1：下载模板 -->
        <div v-if="importStep === 0" class="step-content">
          <div class="template-info">
            <h4>模板说明：</h4>
            <ul>
              <li>1. 请使用下载的Excel模板填写数据</li>
              <li>2. 红色标头字段为必填项</li>
              <li>3. 性别：1-男，2-女</li>
              <li>4. 状态：1-启用，0-禁用</li>
              <li>5. 手机号和身份证号不能重复</li>
              <li>6. 也支持CSV格式文件导入</li>
              <!-- 新增提示 -->
            </ul>
          </div>
          <div class="download-action">
            <el-button
              type="warning"
              @click="handleDownloadTemplate"
              icon="el-icon-download"
            >
              下载Excel模板
            </el-button>
          </div>
        </div>

        <!-- 步骤2：上传文件 -->
        <div v-if="importStep === 1" class="step-content">
          <!-- 添加格式说明 -->
          <el-alert
            title="格式说明"
            type="info"
            :closable="false"
            description="系统支持Excel(.xlsx/.xls)和CSV(.csv)格式文件导入。建议使用Excel模板以获得最佳体验。"
            style="margin-bottom: 15px"
          />

          <el-upload
            class="upload-demo"
            drag
            action=""
            :auto-upload="false"
            :on-change="handleFileChange"
            :before-upload="beforeUpload"
            :file-list="fileList"
            accept=".xlsx,.xls,.csv"
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">
              将Excel或CSV文件拖到此处，或<em>点击上传</em>
            </div>
            <div class="el-upload__tip" slot="tip">
              支持xlsx/xls/csv格式文件，且不超过10MB
            </div>
          </el-upload>
        </div>

        <!-- 步骤3：导入结果 -->
        <div v-if="importStep === 2" class="step-content">
          <div class="import-result">
            <el-alert
              :title="`导入完成：成功 ${importResult.success} 条，失败 ${importResult.fail} 条`"
              :type="importResult.fail === 0 ? 'success' : 'warning'"
              :closable="false"
            />

            <div v-if="importResult.fail > 0" class="error-list">
              <h4>失败详情：</h4>
              <el-table :data="importResult.errorList" border size="mini">
                <el-table-column
                  prop="row"
                  label="行号"
                  width="60"
                ></el-table-column>
                <el-table-column
                  prop="userName"
                  label="姓名"
                  width="100"
                ></el-table-column>
                <el-table-column
                  prop="userPhone"
                  label="手机号"
                  width="120"
                ></el-table-column>
                <el-table-column
                  prop="reason"
                  label="失败原因"
                ></el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button
          v-if="importStep > 0"
          @click="importStep--"
          :disabled="importStep === 1 && uploading"
          >上一步</el-button
        >
        <el-button
          v-if="importStep === 1"
          type="primary"
          @click="handleUpload"
          :loading="uploading"
          :disabled="!selectedFile"
          >开始导入</el-button
        >
        <el-button
          v-if="importStep === 2"
          type="primary"
          @click="handleImportComplete"
          >完成</el-button
        >
        <el-button @click="importDialogVisible = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message, MessageBox } from "element-ui";
import { getUserList, userAdd, userUpdate, userDelete } from "@/api/user.js";
import {
  downloadTemplate,
  userExport,
  userImport,
} from "@/api/importexport.js";

export default {
  name: "UserManage",
  data() {
    // 手机号验证规则
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入手机号"));
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error("手机号格式不正确"));
      } else {
        callback();
      }
    };

    // 身份证号验证规则
    const validateIdCard = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入身份证号"));
      } else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(value)) {
        callback(new Error("身份证号格式不正确"));
      } else {
        callback();
      }
    };

    return {
      // 搜索条件 - 根据实体类字段调整
      searchForm: {
        userName: "",
        userPhone: "",
        userCard: "",
        userGender: "",
      },
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      dialogVisible: false,
      // 导入相关
      importDialogVisible: false,
      importStep: 0,
      fileList: [],
      selectedFile: null,
      uploading: false,
      importResult: {
        success: 0,
        fail: 0,
        errorList: [],
      },
      // 表单数据 - 根据实体类字段调整
      form: {
        userId: "",
        userName: "",
        userCard: "",
        userAge: null,
        userGender: "1",
        userAccount: "",
        userPassword: "",
        userPhone: "",
        userStatus: 1,
        userBalance: 0,
        userEmail: "",
      },
      isAdd: true,
      // 校验规则 - 根据字段调整
      rules: {
        userName: [
          { required: true, message: "请输入姓名", trigger: "blur" },
          { min: 2, max: 20, message: "姓名长度为2-20个字符", trigger: "blur" },
        ],
        userPhone: [
          { required: true, validator: validatePhone, trigger: "blur" },
        ],
        userCard: [
          { required: true, validator: validateIdCard, trigger: "blur" },
        ],
        userGender: [
          { required: true, message: "请选择性别", trigger: "change" },
        ],
        userEmail: [
          { type: "email", message: "邮箱格式不正确", trigger: "blur" },
        ],
        userAccount: [
          { required: true, message: "请输入账号", trigger: "blur" },
        ],
        userPassword: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "密码长度至少6位", trigger: "blur" },
        ],
      },
    };
  },
  mounted() {
    this.loadUserList();
  },
  methods: {
    
    /**
     * 加载用户列表
     */
    loadUserList() {
      const params = {
        pageNum: this.pagination.currentPage,
        pageSize: this.pagination.pageSize,
        ...this.searchForm,
      };

      Object.keys(params).forEach((key) => {
        if (params[key] === "") {
          delete params[key];
        }
      });

      getUserList(params)
        .then((listData) => {
          this.tableData = listData.list || [];
          this.pagination.total = listData.total || 0;
        })
        .catch((err) => {
          console.error("查询用户列表异常:", err);
        });
    },

    /**
     * 打开导入弹窗
     */
    handleImport() {
      this.importDialogVisible = true;
      this.importStep = 0;
      this.fileList = [];
      this.selectedFile = null;
      this.importResult = {
        success: 0,
        fail: 0,
        errorList: [],
      };
    },

    /**
     * 下载Excel模板
     */
    handleDownloadTemplate() {
      downloadTemplate()
        .then((response) => {
          // 创建blob对象
          const blob = new Blob([response], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          });
          // 创建下载链接
          const downloadElement = document.createElement("a");
          const href = window.URL.createObjectURL(blob);
          downloadElement.href = href;
          downloadElement.download = "用户导入模板.xlsx";
          document.body.appendChild(downloadElement);
          downloadElement.click();
          document.body.removeChild(downloadElement);
          window.URL.revokeObjectURL(href);

          Message.success("模板下载成功");

          // 如果在导入流程中，自动进入下一步
          if (this.importDialogVisible) {
            this.importStep = 1;
          }
        })
        .catch((err) => {
          console.error("下载模板失败:", err);
          Message.error("模板下载失败");
        });
    },

    /**
     * 文件选择变化
     */
    handleFileChange(file) {
      this.selectedFile = file.raw;
      this.fileList = [file];
    },

    /**
     * 上传前校验
     */
    beforeUpload(file) {
      const isExcel =
        file.type === "application/vnd.ms-excel" ||
        file.type ===
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
      const isCsv = file.type === "text/csv" || file.name.endsWith(".csv");
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isExcel && !isCsv) {
        Message.error("只能上传Excel或CSV文件!");
        return false;
      }
      if (!isLt10M) {
        Message.error("文件大小不能超过10MB!");
        return false;
      }
      return true;
    },

    /**
     * 开始导入
     */
    handleUpload() {
      if (!this.selectedFile) {
        Message.warning("请选择要导入的Excel文件");
        return;
      }

      this.uploading = true;
      const formData = new FormData();
      formData.append("multipartFile", this.selectedFile);

      userImport(formData)
        .then((response) => {
          // 注意：现在参数是response而不是result
          this.uploading = false;

          // 从response.data中获取实际的数据
          const result = response.data;

          console.log("完整响应:", response);
          console.log("业务数据:", result);

          // 检查业务响应
          if (result && result.code === 200) {
            this.importResult = result.data;
            this.importStep = 2;
            Message.success(result.msg || "导入完成");
          } else {
            Message.error(result.msg || "导入失败");
          }
        })
        .catch((err) => {
          this.uploading = false;
          console.error("导入失败:", err);
          Message.error("导入失败: " + (err.message || "未知错误"));
        });
    },

    /**
     * 导入完成
     */
    handleImportComplete() {
      this.importDialogVisible = false;
      this.loadUserList(); // 刷新列表
    },

    /**
     * 导入弹窗关闭
     */
    handleImportClose() {
      this.fileList = [];
      this.selectedFile = null;
      this.importStep = 0;
    },

    handleSearch() {
      this.pagination.currentPage = 1;
      this.loadUserList();
    },

    handleReset() {
      this.searchForm = {
        userName: "",
        userPhone: "",
        userCard: "",
        userGender: "",
      };
      this.pagination.currentPage = 1;
      this.loadUserList();
    },

    handleAdd() {
      this.isAdd = true;
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.resetForm();
      });
    },

    /**
     * 编辑回显
     */
    handleEdit(row) {
      this.isAdd = false;
      this.form = {
        userId: row.userId,
        userName: row.userName,
        userCard: row.userCard,
        userAge: row.userAge,
        userGender: row.userGender,
        userAccount: row.userAccount,
        userPhone: row.userPhone,
        userStatus: row.userStatus,
        userBalance: row.userBalance,
        userEmail: row.userEmail,
        userPassword: "", // 编辑时不显示密码
      };
      this.dialogVisible = true;
    },
    //软删除用户
    handleDelete(row) {
      MessageBox.confirm(`确定删除用户【${row.userName}】吗？`, "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          userDelete(row.userId)
            .then(() => {
              Message.success("删除成功");
              this.loadUserList();
            })
            .catch((err) => {
              console.error("删除用户异常:", err);
            });
        })
        .catch(() => {});
    },

    /**
     * 导出用户数据
     */
    handleExport() {
      const params = { ...this.searchForm };
      Object.keys(params).forEach((key) => {
        if (params[key] === "") {
          delete params[key];
        }
      });

      userExport(params)
        .then((response) => {
          const blob = new Blob([response], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          });
          const downloadElement = document.createElement("a");
          const href = window.URL.createObjectURL(blob);
          downloadElement.href = href;
          downloadElement.download = `用户数据_${new Date().getTime()}.xlsx`; // 使用时间戳避免文件名重复
          document.body.appendChild(downloadElement);
          downloadElement.click();
          document.body.removeChild(downloadElement);
          window.URL.revokeObjectURL(href);
          Message.success("Excel文件导出成功"); // 明确提示是Excel文件
        })
        .catch((err) => {
          console.error("导出失败:", err);
          Message.error("导出失败");
        });
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.loadUserList();
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadUserList();
    },

    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          const apiMethod = this.isAdd ? userAdd : userUpdate;
          const submitData = { ...this.form };

          // 如果是编辑，且密码为空，则不提交密码字段
          if (!this.isAdd && !submitData.userPassword) {
            delete submitData.userPassword;
          }

          apiMethod(submitData)
            .then(() => {
              Message.success(this.isAdd ? "新增成功" : "编辑成功");
              this.dialogVisible = false;
              this.loadUserList();
            })
            .catch((err) => {
              console.error("操作用户异常:", err);
            });
        }
      });
    },

    resetForm() {
      this.form = {
        userId: "",
        userName: "",
        userCard: "",
        userAge: null,
        userGender: "1",
        userAccount: "",
        userPassword: "",
        userPhone: "",
        userStatus: 1,
        userBalance: 0,
        userEmail: "",
      };
      if (this.$refs.formRef) {
        this.$refs.formRef.clearValidate();
      }
    },

    handleDialogClose() {
      this.$refs.formRef.clearValidate();
    },
  },
};
</script>

<style scoped>
.user-manage-container {
  padding: 20px;
  min-height: calc(100vh - 40px);
}

.search-form {
  margin-bottom: 20px;
}

.action-buttons {
  margin-bottom: 20px;
}

.action-buttons .el-button {
  margin-right: 10px;
}

.el-table {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}

.search-form .el-form-item {
  margin-right: 20px;
  margin-bottom: 10px;
}

/* 导入相关样式 */
.import-container {
  padding: 10px 0;
}

.import-steps {
  margin-bottom: 30px;
}

.step-content {
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.template-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.template-info h4 {
  margin-top: 0;
  color: #606266;
}

.template-info ul {
  margin: 10px 0;
  padding-left: 20px;
}

.template-info li {
  line-height: 1.8;
  color: #606266;
}

.download-action {
  text-align: center;
}

.upload-demo {
  width: 100%;
}

.import-result {
  text-align: center;
}

.error-list {
  margin-top: 20px;
  text-align: left;
}

.error-list h4 {
  margin-bottom: 10px;
  color: #f56c6c;
}
</style>