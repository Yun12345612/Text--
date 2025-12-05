<template>
  <div class="register-container">
    <!-- 系统logo和名称 -->
    <img :src="systemLogo" alt="系统logo" class="logo" />
    <h1>{{ $store.state.title }}</h1>
    <h2>管理员注册</h2>

    <!-- 注册表单 -->
    <form @submit.prevent="handleRegister">
      <div class="form-item">
        <label>账号：</label>
        <input
          type="text"
          v-model="registerForm.account"
          placeholder="请输入账号（6-20位字符）"
          required
          :class="{ 'error-input': accountError }"
        />
        <div v-if="accountError" class="error-text">{{ accountError }}</div>
      </div>

      <div class="form-item">
        <label>密码：</label>
        <input
          type="password"
          v-model="registerForm.password"
          placeholder="请输入密码（至少6位）"
          required
          :class="{ 'error-input': passwordError }"
        />
        <div v-if="passwordError" class="error-text">{{ passwordError }}</div>
      </div>

      <div class="form-item">
        <label>确认密码：</label>
        <input
          type="password"
          v-model="registerForm.confirmPassword"
          placeholder="请再次输入密码"
          required
          :class="{ 'error-input': confirmPasswordError }"
        />
        <div v-if="confirmPasswordError" class="error-text">
          {{ confirmPasswordError }}
        </div>
      </div>

      <div class="form-item">
        <label>姓名：</label>
        <input
          type="text"
          v-model="registerForm.name"
          placeholder="请输入姓名"
          required
          :class="{ 'error-input': nameError }"
        />
        <div v-if="nameError" class="error-text">{{ nameError }}</div>
      </div>

      <div class="form-item">
        <label>验证码：</label>
        <input
          type="text"
          v-model="registerForm.inputCode"
          placeholder="请输入验证码"
          required
          style="width: 120px; margin-right: 10px"
          :class="{ 'error-input': codeError }"
        />
        <img
          :src="imgSrc"
          @click="getCode"
          alt="验证码"
          style="
            height: 40px;
            cursor: pointer;
            border: 1px solid #ddd;
            border-radius: 4px;
          "
          title="点击刷新验证码"
        />
        <div v-if="codeError" class="error-text">{{ codeError }}</div>
      </div>

      <button type="submit" class="register-btn" :disabled="isLoading">
        {{ isLoading ? "注册中..." : "注册" }}
      </button>

      <!-- 错误提示 -->
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
import { adminRegister } from "@/api/admin";

export default {
  data() {
    return {
      registerForm: {
        account: "",
        password: "",
        confirmPassword: "",
        name: "",
        inputCode: "",
      },
      systemLogo: "",
      imgSrc: "get/code",
      isLoading: false,
      registerError: "",
      accountError: "",
      passwordError: "",
      confirmPasswordError: "",
      nameError: "",
      codeError: "",
    };
  },

  mounted() {
    this.getCode();
  },

  methods: {
    // 刷新验证码
    getCode() {
      this.imgSrc = `/api/get/code?t=${Date.now()}`;
    },

    // 表单校验
    validateForm() {
      let isValid = true;

      // 账号校验
      if (!this.registerForm.account) {
        this.accountError = "请输入账号";
        isValid = false;
      } else if (
        this.registerForm.account.length < 6 ||
        this.registerForm.account.length > 20
      ) {
        this.accountError = "账号长度需在6-20位之间";
        isValid = false;
      } else {
        this.accountError = "";
      }

      // 密码校验
      if (!this.registerForm.password) {
        this.passwordError = "请输入密码";
        isValid = false;
      } else if (this.registerForm.password.length < 6) {
        this.passwordError = "密码长度至少6位";
        isValid = false;
      } else {
        this.passwordError = "";
      }

      // 确认密码校验
      if (!this.registerForm.confirmPassword) {
        this.confirmPasswordError = "请确认密码";
        isValid = false;
      } else if (
        this.registerForm.confirmPassword !== this.registerForm.password
      ) {
        this.confirmPasswordError = "两次密码输入不一致";
        isValid = false;
      } else {
        this.confirmPasswordError = "";
      }

      // 姓名校验
      if (!this.registerForm.name) {
        this.nameError = "请输入姓名";
        isValid = false;
      } else {
        this.nameError = "";
      }

      // 验证码校验
      if (!this.registerForm.inputCode) {
        this.codeError = "请输入验证码";
        isValid = false;
      } else {
        this.codeError = "";
      }

      return isValid;
    },

    // 注册核心方法
    async handleRegister() {
      this.isLoading = true;
      this.registerError = "";

      // 前端表单校验
      if (!this.validateForm()) {
        this.isLoading = false;
        return;
      }

      try {
        // 调用注册接口
        await adminRegister({
          account: this.registerForm.account,
          password: this.registerForm.password,
          name: this.registerForm.name,
          code: this.registerForm.inputCode,
        });
        alert("注册成功，请前往登录");
        this.$router.push("/login");
        //使用拦截器处理响应结果
      } catch (err) {
        this.registerError = err.message || "注册失败，请重试";
        console.error("注册失败：", err);
        this.getCode();
        this.registerForm.inputCode = "";
      } finally {
        this.isLoading = false;
      }

      /*if (res.data.code === 200) {
          alert("注册成功，请前往登录");
          this.$router.push("/login");
        } else {
          this.registerError = res.data.msg || "注册失败，请重试";
          this.getCode();
          this.registerForm.inputCode = "";
        }
      } catch (err) {
        this.registerError = err.response?.data?.msg || "网络异常，请稍后再试";
        console.error("注册失败：", err);
        this.getCode();
        this.registerForm.inputCode = "";
      } finally {
        this.isLoading = false;
      }*/
    },
  },
};
</script>

<style scoped>
/* 容器样式 */
.register-container {
  width: 350px;
  margin: 80px auto;
  padding: 25px;
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.08);
  text-align: center;
}

/* 系统logo样式 */
.logo {
  width: 100px;
  height: 100px;
  margin-bottom: 15px;
  border-radius: 50%;
  object-fit: cover;
}

/* 标题样式 */
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

/* 表单项样式 */
.form-item {
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.form-item label {
  display: inline-block;
  width: 60px;
  text-align: right;
  margin-right: 12px;
  color: #666;
  font-size: 14px;
}
.form-item input {
  width: 200px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}
.form-item input.error-input {
  border-color: #f56c6c;
}
.form-item input:focus {
  border-color: #409eff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 错误提示文本 */
.error-text {
  position: absolute;
  left: 72px;
  bottom: -20px;
  font-size: 12px;
  color: #f56c6c;
  white-space: nowrap;
}

/* 注册按钮样式 */
.register-btn {
  width: 100%;
  padding: 12px;
  background: #67c23a;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.3s;
  margin-bottom: 12px;
}
.register-btn:hover {
  background: #5daf34;
}
.register-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 错误提示样式 */
.error-tip {
  color: #f56c6c;
  font-size: 13px;
  margin: 0 0 15px;
  height: 18px;
}

/* 登录链接样式 */
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