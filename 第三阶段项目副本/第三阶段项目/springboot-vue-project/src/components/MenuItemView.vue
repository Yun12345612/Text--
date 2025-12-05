<template>
  <div>
    <!-- 一级菜单（带折叠） -->
    <el-submenu
      v-if="menu.children && menu.children.length"
      :index="menu.id.toString()"
    >
      <template #title>
        <!-- 硬编码父菜单图标：根据menuName匹配 -->
        <i :class="getIcon(menu.menuName)" class="menu-icon"></i>
        <span>{{ menu.menuName }}</span>
      </template>
      <!-- 递归渲染子菜单 -->
      <menu-item v-for="child in menu.children" :key="child.id" :menu="child" />
    </el-submenu>

    <!-- 二级菜单（直接跳转） -->
    <el-menu-item v-else :index="menu.menuPath">
      <!-- 硬编码子菜单图标：根据menuName匹配 -->
      <i :class="getIcon(menu.menuName)" class="menu-icon"></i>
      <span>{{ menu.menuName }}</span>
    </el-menu-item>
  </div>
</template>

<script>
export default {
  name: "MenuItem",
  props: {
    menu: {
      type: Object,
      required: true,
      validator: (val) => val.id && val.menuName && val.menuPath !== undefined,
    },
  },
  methods: {
    // 根据菜单名称返回对应的图标类名
    getIcon(menuName) {
      switch (menuName) {
        case "首页":
          return "el-icon-s-home"; // 首页专属图标
        case "系统管理":
          return "el-icon-setting"; // 系统设置图标
        case "用户管理":
          return "el-icon-user"; // 用户图标
        case "管理员管理":
          return "el-icon-user-solid"; // 管理员实心用户图标
        case "角色管理":
          return "el-icon-rank"; // 角色等级图标
        case "菜单管理":
          return "el-icon-menu"; // 菜单图标
        case "科室管理":
          return "el-icon-house";  // 科室建筑图标
        case "体检业务":
          return "el-icon-notebook-2"; // 业务笔记本图标
        case "体检项目管理":
          return "el-icon-s-data"; // 数据类项目管理图标
        case "体检套餐管理":
          return "el-icon-present"; // 套餐礼盒图标
        case "细项结果录入":
          return "el-icon-edit"; // 编辑录入图标
        case "总结报告管理":
          return "el-icon-document"; // 报告文档图标
        case "报告审核":
          return "el-icon-check"; // 审核勾选图标
        case "订单管理":
          return "el-icon-s-order"; // 订单图标
        default:
          return "el-icon-menu"; // 默认菜单图标
      }
    },
  },
};
</script>

<style scoped>
/* 图标统一样式（保持原有样式） */
.menu-icon {
  margin-right: 12px;
  font-size: 16px;
  width: 20px;
  text-align: center;
  color: #d1d5db !important;
}
/* 其他原有样式... */
</style>