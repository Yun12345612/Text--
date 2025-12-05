<template>
  <div class="login-container">
    <!-- 1、系统logo和名称 -->
    <img :src="systemLogo" alt="系统logo" class="logo" />
    <h1>{{ $store.state.title }}</h1>
    <h2>管理员登录</h2>

    <!-- 登录表单 -->
    <form @submit.prevent="handleLogin">
      <div class="form-item">
        <label>账号：</label>
        <input
          type="text"
          v-model="loginForm.account"
          placeholder="请输入账号"
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
// 引入登录接口（对应修改后的admin.js接口名）
import { adminLogin } from "@/api/admin";
// 引入获取系统信息的接口（若需要，可根据实际后端调整）
import { getSystemInfo } from "@/api/SystemInfo.js";
// 本地静态 logo（将图片放到 src/assets/logo.png）
import localLogo from "@/assets/images/system-logo.png";

export default {
  data() {
    return {
      loginForm: {
        account: "",
        password: "",
        inputCode: "", // 验证码输入字段
      },
      // 系统信息相关（若后端有系统信息接口可启用）
      systemName: "",
      systemLogo: localLogo,
      // 验证码图片地址（加 /api 前缀以匹配后端 context-path）
      imgSrc: "/api/get/code",
      isLoading: false, // 登录加载状态
      loginError: "", // 登录错误提示
    };
  },

  // 组件创建时执行
  created() {
    // 打印Vuex中的标题（若使用Vuex）
    console.log("store中的title:", this.$store.getters.getTitle);
  },

  // 组件挂载后执行
  mounted() {
    // 加载时获取验证码（防止缓存）
    this.getCode();
    // 若后端有系统信息接口，可启用以下代码获取logo和名称
    this.getSystemInfoData();
  },

  methods: {
    // 刷新验证码（加时间戳防止缓存）
    getCode() {
      this.imgSrc = `/api/get/code?t=${Date.now()}`;
    },

    // （可选）获取系统信息（需后端配合提供接口）
    async getSystemInfoData() {
      try {
        const response = await getSystemInfo();
      
        this.systemName = response.data.systemName;

        console.log("系统logo路径：", this.systemLogo);
      } catch (error) {
        console.error("获取系统信息失败", error);
      }
    },

    // 登录核心方法（async/await异步处理）
    async handleLogin() {
      this.isLoading = true;
      this.loginError = ""; // 清空之前的错误提示

      // 前端基础校验
      if (!this.loginForm.inputCode) {
        this.loginError = "请输入验证码";
        this.isLoading = false;
        return;
      }

      try {
        // 调用登录接口，传递参数（与后端接收字段匹配）
        const res = await adminLogin({
          account: this.loginForm.account,
          password: this.loginForm.password,
          code: this.loginForm.inputCode,
        });

        // 登录成功处理
        if (res ) {
          const responseData = res;
          const userInfo = responseData.adminInfo || res.data.data;

          // 存储用户信息到localStorage
          localStorage.setItem("id", userInfo.id);
          localStorage.setItem("userInfo", JSON.stringify(userInfo));
          // 若后端返回token，可添加token存储
          localStorage.setItem("token", responseData.token);
          await this.getSystemInfoData(); // 可选：登录后获取系统信息

          // 登录成功提示（若使用Element Plus可替换为this.$message）
          alert(
            `登录成功！欢迎 ${userInfo.adminName || userInfo.adminAccount}`
          );
          // 跳转到管理员列表页（对应你的目标页面）
          this.$router.push("/home");
        }
        // 登录失败处理
        else {
          this.loginError = res.data.msg || "登录失败，请重试";
          this.getCode(); // 刷新验证码
          this.loginForm.inputCode = ""; // 清空验证码输入框
        }
      } catch (err) {
        // 网络错误或接口异常处理
        this.loginError = err.response?.data?.msg || "网络异常，请检查后端服务";
        console.error("登录失败：", err);
        this.getCode(); // 刷新验证码
        this.loginForm.inputCode = ""; // 清空验证码输入框
      } finally {
        // 无论成功失败，最终关闭加载状态
        this.isLoading = false;
      }
    },
  },
};
</script>

<style scoped>
/* 容器样式：居中、边框、阴影 */
.login-container {
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

/* 表单项样式：标签与输入框对齐 */
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

/* 验证码输入组样式 */
.code-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 280px;
}

.code-input {
  flex: 1;
  width: 70px !important; /* 覆盖原来的宽度 */
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

/* 登录按钮样式 */
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

/* 错误提示样式 */
.error-tip {
  color: #f56c6c;
  font-size: 13px;
  margin: 0 0 15px;
  height: 18px;
}

/* 注册链接样式 */
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