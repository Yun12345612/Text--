<template>
  <div class="login-page">
    <h1 class="login-title">登录</h1>
    <img :src="avatar" alt="用户头像" class="avatar" />
    <div class="login-container">
      <van-cell-group>
        <van-field
          v-model="account"
          label="账号"
          left-icon="smile-o"
          right-icon="user-o"
          placeholder="请输入账号"
        />
        <van-field
          v-model="password"
          type="password"
          clearable
          label="密码"
          left-icon="music-o"
          placeholder="请输入密码"
        />
      </van-cell-group>
      <br />
      <van-button block type="primary" class="login-btn" @click="handleLogin"
        >登录</van-button
      >
    <router-link to="/register" class="register-link">立即注册?</router-link>
    </div>
  </div>
</template>

<script>
import { Toast } from "vant";
import avatar from "@/assets/img/头像1.jpg";
import { login } from "@/api/login/index";

export default {
  name: "Login",
  data() {
    return {
      avatar: avatar,
      account: "",
      password: "",
    };
  },
  methods: {
    async handleLogin() {
      if (!this.account) {
        Toast("请输入账号");
        return;
      }
      if (!this.password) {
        Toast("请输入密码");
        return;
      }
      var params = {
        account: this.account,
        password: this.password,
      };
      login(params)
        .then((res) => {
          if (res.code === 200) {
            localStorage.setItem("token",res.token)
            Toast.success("登录成功");
            //跳转到home主页
            this.$router.push("/home");
          } else {
            Toast.fail(res.msg || "登录失败");
          }
        })
        .catch((error) => {
          Toast.fail("登录失败,请重试!");
          console.error(error);
        });
    },
  },
};
</script>

<style lang="scss" scoped>
.login-page {
  padding: 20px;
  min-height: 100vh;
  background: #f7f8fa;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-title {
  text-align: center;
  margin: 30px 0;
  color: #333;
  font-size: 24px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin: 20px 0;
  border: 3px solid #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.login-container {
  width: 100%;
  max-width: 300px;
  margin-top: 50px;
}

.login-btn {
  height: 44px;
  border-radius: 6px;
  font-size: 16px;
}
</style>