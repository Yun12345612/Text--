<template>
  <div class="new-home-container">
    <!-- 顶部导航栏：深蓝色设计 -->
    <header class="new-home-header">
      <div class="header-left">
        <button class="sidebar-toggle" @click="toggleSidebar">
          <i class="el-icon-menu"></i>
        </button>
        <div class="brand-section">
          <img
            :src="systemLogo || defaultLogo"
            alt="系统logo"
            class="system-logo"
          />
          <h1 class="app-title">{{ systemName || "体检健康管理系统" }}</h1>
        </div>
      </div>

      <div class="header-right">
        <div class="user-info">
          <span class="welcome-text">晚上好，{{ userName || "管理员" }}</span>
        </div>
        <div class="user-dropdown-wrapper">
          <el-dropdown
            trigger="click"
            placement="bottom-end"
            @command="handleCommand"
            class="custom-user-dropdown"
          >
            <div class="user-profile">
              <img
                :src="userAvatar || defaultAvatar"
                alt="用户头像"
                class="user-avatar"
              />
              <i class="el-icon-arrow-down arrow-icon"></i>
            </div>
            <el-dropdown-menu slot="dropdown" class="custom-dropdown-menu">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item command="setting">
                <i class="el-icon-setting"></i> 账号设置
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体内容区 -->
    <div class="new-home-main">
      <!-- 侧边栏：统一样式 -->
      <aside
        class="new-home-sidebar"
        :class="{ collapsed: isSidebarCollapsed }"
      >
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="#fff"
          text-color="#303133"
          active-text-color="#409EFF"
          :collapse="isSidebarCollapsed"
          router
          :collapse-transition="false"
          :unique-opened="false"
        >
          <!-- 动态渲染权限菜单 -->
          <menu-item
            v-for="topMenu in menuTree"
            :key="topMenu.id"
            :menu="topMenu"
          />
        </el-menu>
      </aside>

      <section class="content-area">
        <div class="content-header">
          <el-breadcrumb class="content-breadcrumb" separator="/">
            <el-breadcrumb-item
              v-for="(crumb, index) in breadcrumbList"
              :key="index"
            >
              {{ crumb }}
            </el-breadcrumb-item>
          </el-breadcrumb>
          <div class="header-actions">
            <el-button type="text" icon="el-icon-refresh" @click="refreshPage">
              刷新
            </el-button>
          </div>
        </div>

        <div class="content-container">
          <router-view></router-view>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { getPermissionMenu } from "@/api/menu.js";
import { getSystemInfo } from "@/api/SystemInfo.js";
import MenuItem from "./MenuItemView.vue";

const DEFAULT_LOGO = require("@/assets/images/system-logo.png");
const DEFAULT_AVATAR = require("@/assets/images/default-avatar.png");

export default {
  name: "NewHomeView",
  components: { MenuItem },
  data() {
    return {
      isSidebarCollapsed: false,
      menuTree: [],
      activeMenu: "/home/dashboard",
      userName: "",
      userRole: "",
      userAvatar: "",
      systemName: "",
      systemLogo: "",
      breadcrumbList: [],
      defaultLogo: DEFAULT_LOGO,
      defaultAvatar: DEFAULT_AVATAR,
    };
  },
  mounted() {
    
    Promise.all([
      this.fetchSystemInfo(),
      this.loadUserInfo(),
      this.loadPermissionMenu(),
    ]);

    this.$router.afterEach((to) => {
      this.activeMenu = to.path;
      this.updateBreadcrumb(to);
    });
  },
  methods: {
    
    async fetchSystemInfo() {
      try {
        const systemData = await getSystemInfo();
        this.systemName = systemData.systemName;
        this.systemLogo = systemData.logoImageUrl;
      } catch (error) {
        console.error("获取系统信息失败", error);
      }
    },

    loadUserInfo() {
      const userInfo = localStorage.getItem("userInfo");
      if (userInfo) {
        const user = JSON.parse(userInfo);
        this.userName = user.adminName;
        this.userRole = user.roleName;
        this.userAvatar = user.avatarUrl;
      }
    },

    async loadPermissionMenu() {
      console.log("菜单数据:", this.menuTree);
      try {
        const userId = localStorage.getItem("id");
        if (!userId) {
          this.$router.push("/login");
          return;
        }

        const userInfoStr = localStorage.getItem("userInfo");
        if (!userInfoStr) {
          this.$router.push("/login");
          return;
        }
        const userInfo = JSON.parse(userInfoStr);

        const roleId = userInfo.roleId;
        if (!roleId) {
          this.$message.error("用户角色信息缺失，请重新登录");
          this.$router.push("/login");
          return;
        }

        const menuData = await getPermissionMenu(roleId);
        console.log("获取菜单返回数据:", menuData);

        const homeMenu = {
          id: "1",
          menuName: "首页",
          menuPath: "/dashboard",
          icon: "el-icon-s-home",
          children: [],
        };

        this.menuTree = [homeMenu, ...menuData];
        this.processMenuPaths(this.menuTree);
      } catch (error) {
        console.error("加载菜单失败:", error);
      }
    },

    processMenuPaths(menus) {
      menus.forEach((menu) => {
        if (menu.menuPath && menu.menuPath.startsWith("/")) {
          menu.menuPath = "/home" + menu.menuPath;
        }
        if (menu.children && menu.children.length) {
          this.processMenuPaths(menu.children);
        }
      });
    },

    toggleSidebar() {
      this.isSidebarCollapsed = !this.isSidebarCollapsed;
    },

    handleCommand(command) {
      switch (command) {
        case "profile":
          this.$router.push("/home/profile");
          break;
        case "setting":
          this.$router.push("/home/account-setting");
          break;
        case "logout":
          this.handleLogout();
          break;
      }
    },

    refreshPage() {
      window.location.reload();
    },

    handleLogout() {
      localStorage.removeItem("adminId");
      localStorage.removeItem("userInfo");
      this.$message.success("退出登录成功");
      this.$router.push("/login");
    },

    updateBreadcrumb(route) {
      this.breadcrumbList = route.matched
        .filter((item) => item.meta && item.meta.title)
        .map((item) => item.meta.title);
    },
  },
};
</script>

<style scoped>
/* 全局容器样式 */
.new-home-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  font-family: "Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    sans-serif;
}

/* 顶部导航栏：深蓝色设计 */
.new-home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background: #1e3a8a;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid #1e40af;
  position: relative;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sidebar-toggle {
  border: none;
  background: rgba(255, 255, 255, 0.15);
  font-size: 18px;
  color: white;
  cursor: pointer;
  transition: all 0.3s;
  padding: 8px;
  border-radius: 4px;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.sidebar-toggle:hover {
  background: rgba(255, 255, 255, 0.25);
}

.system-logo {
  width: 32px;
  height: 32px;
  border-radius: 4px;
}

.app-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: white;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
}

.welcome-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
}
.user-profile:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.arrow-icon {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

/* 下拉菜单样式 */
::v-deep .custom-user-dropdown .el-dropdown-menu {
  min-width: 140px;
  margin-top: 4px;
}

::v-deep .custom-user-dropdown .el-dropdown-menu__item {
  font-size: 14px;
  padding: 8px 16px;
}

::v-deep .custom-user-dropdown .el-dropdown-menu__item i {
  margin-right: 8px;
}

/* 主体内容区布局 */
.new-home-main {
  display: flex;
  flex: 1;
  overflow: hidden;
  background: #f5f7fa;
}

/* 侧边栏：统一白色样式 */
.new-home-sidebar {
  width: 220px;
  transition: width 0.3s ease;
  background: #fff;
  border-right: 1px solid #e6e8eb;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.05);
}
.new-home-sidebar.collapsed {
  width: 64px;
}

/* 菜单容器 - 统一白色样式 */
.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
}
.sidebar-menu::-webkit-scrollbar {
  width: 6px;
}
.sidebar-menu::-webkit-scrollbar-thumb {
  background-color: #c0c4cc;
  border-radius: 3px;
}
.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background-color: #909399;
}

/* 统一菜单项样式 - 修复折叠状态问题 */
::v-deep .sidebar-menu .el-menu-item,
::v-deep .sidebar-menu .el-submenu__title {
  height: 46px !important;
  line-height: 46px !important;
}

::v-deep .sidebar-menu .el-menu-item:hover,
::v-deep .sidebar-menu .el-submenu__title:hover {
  background-color: #ecf5ff !important;
}

::v-deep .sidebar-menu .el-menu-item.is-active {
  background-color: #ecf5ff !important;
  color: #409eff !important;
}

/* 关键修复：折叠状态下完全隐藏文字和正确设置间距 */
::v-deep .sidebar-menu.el-menu--collapse {
  background-color: #fff !important;
  width: 64px !important;
}

::v-deep .sidebar-menu.el-menu--collapse .el-menu-item,
::v-deep .sidebar-menu.el-menu--collapse .el-submenu__title {
  background-color: #fff !important;
  color: #303133 !important;
  padding: 0 20px !important;
  text-align: center;
}

/* 确保折叠状态下文字完全隐藏 */
::v-deep .sidebar-menu.el-menu--collapse .el-menu-item span,
::v-deep .sidebar-menu.el-menu--collapse .el-submenu__title span {
  display: none !important;
  height: 0 !important;
  width: 0 !important;
  overflow: hidden !important;
}

/* 确保折叠状态下图标居中 */
::v-deep .sidebar-menu.el-menu--collapse .el-menu-item .el-icon,
::v-deep .sidebar-menu.el-menu--collapse .el-submenu__title .el-icon {
  margin-right: 0 !important;
  margin-left: 0 !important;
}

::v-deep .sidebar-menu.el-menu--collapse .el-menu-item:hover,
::v-deep .sidebar-menu.el-menu--collapse .el-submenu__title:hover {
  background-color: #ecf5ff !important;
}

::v-deep .sidebar-menu.el-menu--collapse .el-menu-item.is-active {
  background-color: #ecf5ff !important;
  color: #409eff !important;
}

/* 修复子菜单在折叠状态下的显示问题 */
::v-deep .sidebar-menu.el-menu--collapse .el-submenu .el-menu {
  display: none !important;
}

/* 内容区域 */
.content-area {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
}

.content-breadcrumb {
  flex: 1;
}

::v-deep .content-breadcrumb .el-breadcrumb__inner {
  color: #606266;
  font-weight: 500;
}

::v-deep
  .content-breadcrumb
  .el-breadcrumb__item:last-child
  .el-breadcrumb__inner {
  color: #303133;
  font-weight: 600;
}

.header-actions .el-button {
  color: #606266;
  font-size: 14px;
}

.header-actions .el-button:hover {
  color: #409eff;
}

.content-container {
  background-color: #fff;
  padding: 20px;
  flex: 1;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e6e8eb;
  min-height: calc(100vh - 140px);
}
</style>