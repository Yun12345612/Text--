<template>
  <div>
    <!-- 有子菜单的菜单项：递归渲染 -->
    <el-submenu
      v-if="menu.children && menu.children.length > 0"
      :index="menu.menuId.toString()"
    >
      <template slot="title">
        <i :class="menu.menuIcon || 'el-icon-circle'" v-if="menu.menuIcon"></i>
        <span>{{ menu.menuName }}</span>
      </template>
      <!-- 递归调用自身渲染子菜单 -->
      <menu-item
        v-for="child in menu.children"
        :key="child.menuId"
        :menu="child"
      />
    </el-submenu>

    <el-menu-item v-else :index="getCorrectPath()">
      <i :class="menu.menuIcon || 'el-icon-circle'" v-if="menu.menuIcon"></i>
      <span slot="title">{{ menu.menuName }}</span>
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
      validator: (val) =>
        val.menuId && val.menuName && val.menuPath !== undefined,
    },
  },
  methods: {
    // 生成正确的路由路径：确保人员管理对应/home/person
    getCorrectPath() {
      // 菜单正常拼接
      const path = this.menu.menuPath.replace(/^\//, "");
      return `/home/${path}`;
    },
  },
};
</script>

<style scoped>
.el-menu-item i,
.el-submenu__title i {
  margin-right: 8px !important;
}
</style>
