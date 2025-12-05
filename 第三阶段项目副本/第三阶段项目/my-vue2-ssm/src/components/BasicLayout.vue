<template>
  <div class="basic-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-container">
        <div class="logo-area" @click="$router.push('/')">
          <img :src="localLogo" alt="医疗体检中心" class="logo" />
          <h1 class="site-title">医疗体检中心</h1>
        </div>

        <nav class="main-nav">
          <el-menu
            mode="horizontal"
            :default-active="activeNav"
            router
            class="nav-menu"
          >
            <el-menu-item index="/">
              <i class="el-icon-s-home"></i>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/services">
              <i class="el-icon-first-aid-kit"></i>
              <span>体检服务</span>
            </el-menu-item>
            <el-menu-item index="/examination">
              <i class="el-icon-s-goods"></i>
              <span>体检项目</span>
            </el-menu-item>
            <el-menu-item index="/packages">
              <i class="el-icon-s-goods"></i>
              <span>体检套餐</span>
            </el-menu-item>
            <el-menu-item index="/about">
              <i class="el-icon-info"></i>
              <span>关于我们</span>
            </el-menu-item>
            <el-menu-item index="/contact">
              <i class="el-icon-phone"></i>
              <span>联系我们</span>
            </el-menu-item>
          </el-menu>
        </nav>

        <div class="user-actions">
          <el-button type="primary" @click="handleLogin" v-if="!isLoggedIn"
            >登录/注册</el-button
          >
          <el-dropdown v-else @command="handleUserCommand">
            <span class="user-info">
              <span class="welcome-text">欢迎您,</span>
              <el-avatar :size="32" :src="userAvatar">
                {{ userName.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userName }}</span>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="order">
                <i class="el-icon-s-order"></i>
                个人订单
              </el-dropdown-item>
              <el-dropdown-item command="appointment">
                <i class="el-icon-date"></i>
                我的预约
              </el-dropdown-item>
              <el-dropdown-item command="appointment">
                <i class="el-icon-shopping-cart-1"></i>
                我的购物车
              </el-dropdown-item>
              <el-dropdown-item command="reports">
                <i class="el-icon-document"></i>
                体检报告
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <i class="el-icon-switch-button"></i>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="main-content">
      <router-view></router-view>
    </main>

    <!-- 底部信息 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>联系我们</h3>
            <p>地址：厦门市思明区中山路123号</p>
            <p>电话：400-123-4567</p>
            <p>邮箱：service@medical.com</p>
          </div>
          <div class="footer-section">
            <h3>服务时间</h3>
            <p>周一至周五：8:00-18:00</p>
            <p>周六至周日：8:00-17:00</p>
          </div>
          <div class="footer-section">
            <h3>快速链接</h3>
            <p><a href="/about">关于我们</a></p>
            <p><a href="/services">体检服务</a></p>
            <p><a href="/contact">联系我们</a></p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>
            &copy; 2025 医疗体检中心 版权所有(注:本站仅用于学习,无实际商业服务)
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import localLogo from "@/assets/images/system-logo.png";

export default {
  name: "BasicLayout",
  data() {
    return {
      localLogo: localLogo,
    };
  },
  computed: {
    activeNav() {
      return this.$route.path;
    },
    isLoggedIn() {
      return this.$store.getters.isLoggedIn;
    },
    userName() {
      return this.$store.getters.userName;
    },
    userAvatar() {
      return this.$store.state.userInfo?.avatar || "";
    },
  },
  created() {
    this.$store.dispatch("initUserInfo");
  },
  methods: {
    handleLogin() {
      this.$router.push("/login");
    },
    // 处理用户下拉菜单命令
    handleUserCommand(command) {
      switch (command) {
        case "profile":
          this.$router.push("/profile");
          break;
        case "appointment":
          this.$router.push("/appointment");
          break;
        case "reports":
          this.$router.push("/reports");
          break;
        case "logout":
          this.handleLogout();
          break;
        case "shopping":
          this.handleShoppingt(); // 购物车方法
          break;
        case "order":
          this.handleOrder(); // 跳到订单页面的方法
          break;
      }
    },
    // 跳到订单页面的方法
    handleOrder() {
      if (this.$route.path === "/order") {
        window.location.reload();
      } else {
        this.$router.push("/order");
      }
    },
    // 购物车跳转
    handleShoppingt() {
      this.$router.push("/shopping-cart");
    },
    // 退出登录处理
    handleLogout() {
      this.$confirm("确定要退出登录吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          // 清除 Vuex 状态
          this.$store.dispatch("clearUserInfo");
          // 清除 localStorage
          localStorage.removeItem("userInfo");
          localStorage.removeItem("token");
          localStorage.removeItem("userId");

          this.$message.success("已退出登录");

          this.$router.push("/login");
        })
        .catch(() => {
          // 用户取消退出
        });
    },
  },
};
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 头部样式 */
.header {
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.logo-area:hover {
  opacity: 0.8;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  object-fit: contain;
  flex-shrink: 0;
}

.site-title {
  margin: 0;
  font-size: 22px;
  color: #409eff;
  font-weight: bold;
  white-space: nowrap;
}

.nav-menu {
  border: none;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-name {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}
.welcome-text {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 0;
}

/* 底部样式 */
.footer {
  background: #304156;
  color: white;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section h3 {
  margin-bottom: 16px;
  color: #409eff;
}

.footer-section p {
  margin: 8px 0;
  color: #c0c4cc;
}

.footer-section a {
  color: #c0c4cc;
  text-decoration: none;
}

.footer-section a:hover {
  color: #409eff;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #434a53;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    flex-direction: column;
    height: auto;
    padding: 15px 20px;
  }

  .logo-area {
    margin-bottom: 15px;
  }

  .logo {
    width: 35px;
    height: 35px;
  }

  .site-title {
    font-size: 20px;
  }

  .nav-menu {
    margin-bottom: 15px;
  }
}

@media (max-width: 480px) {
  .logo {
    width: 30px;
    height: 30px;
  }

  .site-title {
    font-size: 18px;
  }

  .logo-area {
    gap: 8px;
  }

  .user-info {
    padding: 6px 8px;
  }

  .user-name {
    font-size: 12px;
  }
}
</style>