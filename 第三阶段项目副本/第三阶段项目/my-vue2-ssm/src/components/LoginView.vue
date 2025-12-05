<template>
  <div class="login-container">
    <!-- 1、系统logo和名称 -->
    <img :src="systemLogo" alt="系统logo" class="logo" />
    <h1>医疗体检系统</h1>
    <h2>用户登录</h2>

    <!-- 登录表单 -->
    <form @submit.prevent="handleLogin">
      <div class="form-item">
        <label>账号：</label>
        <input
          type="text"
          v-model="loginForm.loginInput"
          placeholder="请输入账号/手机号/身份证"
          required
        />
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input
          type="password"
          v-model="loginForm.password"
          placeholder="请输入密码"
          required
        />
      </div>
      <div class="form-item">
        <label>验证码：</label>
        <div class="code-input-group">
          <input
            type="text"
            v-model="loginForm.inputCode"
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

      <button type="submit" class="login-btn" :disabled="isLoading">
        {{ isLoading ? "登录中..." : "登录" }}
      </button>

      <!-- 错误提示（登录失败时显示） -->
      <div v-if="loginError" class="error-tip">
        {{ loginError }}
      </div>

      <!-- 注册链接 -->
      <div class="register-link">
        <router-link to="/register">没有账号？去注册</router-link>
      </div>
    </form>
  </div>
</template>

<script>
// 引入登录接口
import { userLogin } from "@/api/user.js";
// 本地静态 logo
import localLogo from "@/assets/images/system-logo.png";

export default {
  name: "LoginView",
  data() {
    return {
      loginForm: {
        loginInput: "",
        password: "",
        inputCode: "",
      },
      systemLogo: localLogo,
      imgSrc: "/api/get/code",
      isLoading: false,
      loginError: "",
    };
  },

  mounted() {
    this.getCode();
  },

  methods: {
    getCode() {
      this.imgSrc = `/api/get/code?t=${Date.now()}`;
    },
    //登录方法
    async handleLogin() {
      this.isLoading = true;
      this.loginError = "";

      if (
        !this.loginForm.loginInput ||
        !this.loginForm.password ||
        !this.loginForm.inputCode
      ) {
        this.loginError = "请填写完整信息";
        this.isLoading = false;
        return;
      }

      try {
        const res = await userLogin({
          userAccount: this.loginForm.loginInput,
          userPassword: this.loginForm.password,
          code: this.loginForm.inputCode,
        });

        const userData = res.userInfo;
        const token = res.token;

        if (userData && token) {
          // 存储用户信息和token
          localStorage.setItem("userId", userData.userId);
          localStorage.setItem("userName", userData.userName);
          localStorage.setItem("userPhone",userData.userPhone|| userData.phone);
          localStorage.setItem("userInfo", JSON.stringify(userData));
          localStorage.setItem("token", token);
         //登录成功调用 Vuex中的setUserInfo方法把用户信息和token存储到Vuex中
          this.$store.dispatch('setUserInfo', { userInfo: userData, token });
          alert(
            `登录成功！欢迎 ${
              userData.userName || userData.loginInput || "用户"
            }`
          );
          this.$router.push({ name: "Home" }); // 直接跳转首页
        } else {
          this.loginError = "登录数据异常，请重试";
          this.getCode();
        }
      } catch (err) {
        console.error("登录失败：", err);
        this.loginError =
          err.response?.data?.msg ||
          err.message ||
          "登录失败，请检查账号密码或验证码";
        this.getCode();
        this.loginForm.inputCode = "";
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>

<style scoped>
/* 样式保持不变 */
.login-container {
  width: 350px;
  margin: 80px auto;
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

.login-container h1 {
  font-size: 22px;
  color: #333;
  margin: 0 0 10px;
}
.login-container h2 {
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
.form-item input:focus {
  border-color: #409eff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.code-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 280px;
}

.code-input {
  flex: 1;
  width: 70px !important;
  padding: 10px;
  height: 19px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.code-input:focus {
  border-color: #409eff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
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

.login-btn {
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
.login-btn:hover {
  background: #2d8cf0;
}
.login-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.error-tip {
  color: #f56c6c;
  font-size: 13px;
  margin: 0 0 15px;
  height: 18px;
}

.register-link {
  text-align: center;
  font-size: 14px;
}
.register-link a {
  color: #409eff;
  text-decoration: none;
}
.register-link a:hover {
  text-decoration: underline;
}
</style>