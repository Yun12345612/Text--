<template>
  <div class="register-container">
    <!-- 系统logo和名称 -->
    <img :src="systemLogo" alt="系统logo" class="logo" />
    <h1>医疗体检系统</h1>
    <h2>用户注册</h2>

    <!-- 注册表单 -->
    <form @submit.prevent="handleRegister">
      <div class="form-item">
        <label>姓名：</label>
        <input
          type="text"
          v-model="registerForm.userName"
          placeholder="请输入真实姓名"
          required
        />
      </div>
      <div class="form-item">
        <label>账号：</label>
        <input
          type="text"
          v-model="registerForm.userAccount"
          placeholder="请输入账号"
          required
        />
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input
          type="password"
          v-model="registerForm.userPassword"
          placeholder="请输入密码"
          required
        />
      </div>
      <div class="form-item">
        <label>确认密码：</label>
        <input
          type="password"
          v-model="registerForm.confirmPassword"
          placeholder="请再次输入密码"
          required
        />
      </div>
      
      <!-- 其他字段 -->
      <div class="form-item">
        <label>手机号：</label>
        <input
          type="tel"
          v-model="registerForm.userPhone"
          placeholder="请输入手机号"
          required
        />
      </div>
      <div class="form-item">
        <label>身份证号：</label>
        <input
          type="text"
          v-model="registerForm.userCard"
          placeholder="请输入身份证号"
          required
        />
      </div>
      <div class="form-item">
        <label>性别：</label>
        <select v-model="registerForm.userGender" required>
          <option value="">请选择性别</option>
          <option value="1">男</option>
          <option value="0">女</option>
        </select>
      </div>
      <div class="form-item">
        <label>年龄：</label>
        <input
          type="number"
          v-model="registerForm.userAge"
          placeholder="请输入年龄"
          min="0"
          max="120"
          required
        />
      </div>
      <div class="form-item">
        <label>邮箱：</label>
        <input
          type="email"
          v-model="registerForm.userEmail"
          placeholder="请输入邮箱"
          required
        />
      </div>
      
      <div class="form-item">
        <label>验证码：</label>
        <div class="code-input-group">
          <input
            type="text"
            v-model="registerForm.code"
            placeholder="请输入验证码"
            required
            class="code-input"
          />
          <img
            :src="imgSrc"
            @click="getCode"
            alt="验证码"
            class="code-image"
            title="点击刷新验证码"
          />
        </div>
      </div>

      <button type="submit" class="register-btn" :disabled="isLoading">
        {{ isLoading ? "注册中..." : "注册" }}
      </button>

      <!-- 错误提示（注册失败时显示） -->
      <div v-if="registerError" class="error-tip">
        {{ registerError }}
      </div>

      <!-- 登录链接 -->
      <div class="login-link">
        <router-link to="/login">已有账号？去登录</router-link>
      </div>
    </form>
  </div>
</template>

<script>
// 引入注册接口
import { userRegister, getCode } from "@/api/user.js";
// 本地静态 logo
import localLogo from "@/assets/images/system-logo.png";

export default {
  data() {
    return {
      registerForm: {
        userName: "", // 新增姓名字段
        userAccount: "",
        userPassword: "",
        confirmPassword: "",
        userPhone: "",
        userCard: "",
        userGender: "",
        userAge: "",
        userEmail: "",
        code: ""
      },
      systemLogo: localLogo,
      imgSrc: "/api/get/code",
      isLoading: false,
      registerError: "",
    };
  },

  mounted() {
    this.getCode();
  },

  methods: {
    getCode() {
      this.imgSrc = `/api/get/code?t=${Date.now()}`;
    },

    async handleRegister() {
      this.isLoading = true;
      this.registerError = "";

      // 前端基础校验
      if (!this.registerForm.userName) {
        this.registerError = "请输入姓名";
        this.isLoading = false;
        return;
      }
      if (!this.registerForm.userAccount) {
        this.registerError = "请输入账号";
        this.isLoading = false;
        return;
      }
      if (!this.registerForm.userPassword) {
        this.registerError = "请输入密码";
        this.isLoading = false;
        return;
      }
      if (this.registerForm.userPassword !== this.registerForm.confirmPassword) {
        this.registerError = "两次输入的密码不一致";
        this.isLoading = false;
        return;
      }
      if (!this.registerForm.userPhone) {
        this.registerError = "请输入手机号";
        this.isLoading = false;
        return;
      }
      if (!this.registerForm.userCard) {
        this.registerError = "请输入身份证号";
        this.isLoading = false;
        return;
      }
      if (!this.registerForm.userGender) {
        this.registerError = "请选择性别";
        this.isLoading = false;
        return;
      }
      if (!this.registerForm.userAge) {
        this.registerError = "请输入年龄";
        this.isLoading = false;
        return;
      }
      if (!this.registerForm.userEmail) {
        this.registerError = "请输入邮箱";
        this.isLoading = false;
        return;
      }
      if (!this.registerForm.code) {
        this.registerError = "请输入验证码";
        this.isLoading = false;
        return;
      }

      // 手机号格式验证
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(this.registerForm.userPhone)) {
        this.registerError = "请输入正确的手机号格式";
        this.isLoading = false;
        return;
      }

      // 身份证号格式验证（简单验证）
      const cardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
      if (!cardRegex.test(this.registerForm.userCard)) {
        this.registerError = "请输入正确的身份证号格式";
        this.isLoading = false;
        return;
      }

      // 邮箱格式验证
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.registerForm.userEmail)) {
        this.registerError = "请输入正确的邮箱格式";
        this.isLoading = false;
        return;
      }

      try {
        // 调用注册接口，传递所有字段
        const res = await userRegister(this.registerForm);

        alert("注册成功！请登录");
        this.$router.push("/login");
      } catch (err) {
        this.registerError = err.response?.data?.msg || "注册失败，请重试";
        console.error("注册失败：", err);
        this.getCode();
        this.registerForm.code = "";
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>

<style scoped>
/* 容器样式 */
.register-container {
  width: 400px;
  margin: 60px auto;
  padding: 25px;
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.logo {
  width: 100px;
  height: 100px;
  margin-bottom: 15px;
  border-radius: 50%;
  object-fit: cover;
}

.register-container h1 {
  font-size: 22px;
  color: #333;
  margin: 0 0 10px;
}
.register-container h2 {
  font-size: 18px;
  color: #666;
  margin: 0 0 25px;
  font-weight: 500;
}

.form-item {
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.form-item label {
  display: inline-block;
  width: 80px;
  text-align: right;
  margin-right: 12px;
  color: #666;
  font-size: 14px;
}
.form-item input,
.form-item select {
  width: 220px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}
.form-item input:focus,
.form-item select:focus {
  border-color: #409eff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.code-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 300px;
}

.code-input {
  flex: 1;
  width: 70px !important;
  padding: 10px;
  height: 19px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.code-image {
  height: 40px;
  cursor: pointer;
  border: 1px solid #ddd;
  border-radius: 4px;
  flex-shrink: 0;
}

.code-image:hover {
  border-color: #409eff;
}

.register-btn {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.3s;
  margin-bottom: 12px;
}
.register-btn:hover {
  background: #2d8cf0;
}
.register-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.error-tip {
  color: #f56c6c;
  font-size: 13px;
  margin: 0 0 15px;
  height: 18px;
}

.login-link {
  text-align: center;
  font-size: 14px;
}
.login-link a {
  color: #409eff;
  text-decoration: none;
}
.login-link a:hover {
  text-decoration: underline;
}
</style>