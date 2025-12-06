<template>
  <div class="login-page">
    <h1 class="login-title">注册</h1>
    <img :src="avatar" alt="用户头像" class="avatar" />
    <div class="login-container">
      <van-cell-group>
        <van-field
          v-model="userName"
          type="userName"
          clearable
          label="用户名"
          left-icon="music-o"
          placeholder="请输入用户名"
        />
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
        <van-field
          v-model="confirmPassword"
          type="password"
          clearable
          label="确认密码"
          left-icon="music-o"
          placeholder="请确认密码"
        />
        <!-- 输入手机号，调起手机号键盘 -->
        <van-field v-model="tel" type="tel" label="手机号" />
      </van-cell-group>
      <br />
      <van-button block type="primary" class="login-btn" @click="handleRegister"
        >立即注册</van-button
      >
    </div>
    <van-field
      v-model="sms"
      center
      clearable
      label="短信验证码"
      placeholder="请输入短信验证码"
    >
      <template #button>
        <van-button size="small" type="primary">发送验证码</van-button>
      </template>
    </van-field>
  </div>
</template>

<script>
import { Toast } from "vant";
import avatar from "@/assets/img/头像1.jpg";
import { register, login } from '@/api/login/index'

export default {
  name: "Register",
  data() {
    return {
      avatar: avatar,
      account: "",
      password: "",
      confirmPassword: "", // 新增确认密码变量
      userName: "",
      tel: "",
      sms: ""
    };
  },
  methods: {
    async handleRegister() {
      if (!this.account) {
        Toast("请输入账号");
        return;
      }
      if (!this.password) {
        Toast("请输入密码");
        return;
      }
      if (!this.confirmPassword) {
        Toast("请输入确认密码");
        return;
      }
      // 校验两次密码是否一致
      if (this.password !== this.confirmPassword) {
        Toast("两次输入的密码不一致，请重新输入");
        return;
      }
      if (!this.userName) {
        Toast("请输入用户名");
        return;
      }
      var params = {
        account: this.account,
        password: this.password,
        userName: this.userName,
        tel: this.tel
      };
      register(params)
        .then((res) => {
          if (res.code === 200) {
            Toast.success("注册成功");
            // 跳转到登录主页
            this.$router.push("/login");
          } else {
            Toast.fail(res.msg || "注册失败");
          }
        })
        .catch((error) => {
          Toast.fail("注册失败,请重试!");
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