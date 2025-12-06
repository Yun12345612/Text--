<template>
  <div class="login-page">
    <!-- 通知栏 -->
    <van-notice-bar
      left-icon="volume-o"
      text="欢迎使用智慧停车管理平台，为每一位用户提供优质服务,是我们亘古不变的宗旨🥰"
      background="#fef0f0"
      color="#ff4d4f"
    />

    <!-- 搜索栏 -->
    <van-search
      v-model="searchValue"
      show-action
      label="地址"
      placeholder="请输入停车场地址或关键词"
      @search="onSearch"
      background="#fff"
      class="search-bar"
    >
      <template #action>
        <div class="search-btn" @click="onSearch">搜索</div>
      </template>
    </van-search>

    <!-- 轮播图 -->
    <van-swipe class="carousel" :autoplay="3000" indicator-color="rgba(255,255,255,0.8)">
      <van-swipe-item v-for="(img, index) in carouselImgs" :key="index" class="swipe-item">
        <div class="img-container">
          <van-image
            :src="img"
            class="carousel-img"
            :fit="imageFit"
          />
        </div>
      </van-swipe-item>
    </van-swipe>

    <!-- 分割线 -->
    <van-divider
      :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px', margin: '16px 0' }"
    >您的智能停车管理助手</van-divider>

    <!-- 功能网格 -->
    <van-grid column-num="4" border="false" class="function-grid">
      <van-grid-item
        icon="newspaper-o"
        text="停车场"
        icon-color="#1890ff"
        class="grid-item"
      />
      <van-grid-item
        icon="guide-o"  to="/parkingEsmap"
        text="反向寻车"
        icon-color="#52c41a"
        class="grid-item"
       
      />
      <van-grid-item
        icon="balance-list-o"
        text="支付记录"
        icon-color="#faad14"
        class="grid-item"
      />
      <van-grid-item
        icon="fire-o"
        text="热门活动"
        icon-color="#ff4d4f"
        class="grid-item"
      />
      <van-grid-item
        icon="shop-o"
        text="商店"
        icon-color="#722ed1"
        class="grid-item"
      />
      <van-grid-item
        icon="medal-o"
        text="休闲"
        icon-color="#13c2c2"
        class="grid-item"
      />
      <van-grid-item
        icon="setting-o"
        text="设置"
        icon-color="#8c8c8c"
        class="grid-item"
      />
      <van-grid-item
        icon="service-o"
        text="AI小智"
        icon-color="#2f54eb"
        class="grid-item"
      />
    </van-grid>

    <!-- 底部导航栏 -->
    <van-tabbar v-model="active" class="tabbar">
      <van-tabbar-item icon="wap-home-o" text="首页" />
      <van-tabbar-item icon="logistics" to="/car" text="座驾" />
      <van-tabbar-item icon="records-o" to="/order" text="订单" />
      <van-tabbar-item icon="friends-o" to="/PersonalCenter" text="个人中心" />
    </van-tabbar>
  </div>
</template>

<script>

export default {
  data() {
    return {
      searchValue: "",
      active: 0, // 默认选中首页
      carouselImgs: [
        require('@/assets/img/tc1.jpg'),
        require('@/assets/img/tc2.jpg'),
        require('@/assets/img/tc3.jpg'),
        require('@/assets/img/tc4.jpg'),
        require('@/assets/img/tc5.jpg')
      ],
      imageFit: 'cover' // 控制图片填充方式，cover会完全填充容器
    };
  },
  methods: {
    onSearch() {
      // 搜索逻辑，可根据searchValue进行处理
      console.log('搜索关键词：', this.searchValue);
      // 这里可以添加调用接口等搜索相关操作
    }
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

/* 搜索栏样式 */
.search-bar {
  margin: 10px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-btn {
  padding: 0 12px;
  color: #1890ff;
  font-size: 14px;
}

/* 轮播图样式 - 关键修改 */
.carousel {
  margin: 0 16px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  height: 200px; /* 固定轮播容器高度 */
}

.swipe-item {
  height: 100%; /* 确保轮播项占满容器高度 */
  position: relative; /* 相对定位，用于图片绝对定位 */
  overflow: hidden; /* 隐藏超出容器的部分 */
}

.img-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.carousel-img {
  width: 100%;
  height: 100%;
  /* 移除之前的固定高度，改用百分比 */
  object-fit: cover; /* 核心属性：保持比例并填充容器，裁剪超出部分 */
  transition: transform 0.3s ease; /* 平滑过渡效果 */
}

/* 鼠标悬停时轻微放大图片，增强交互感 */
.carousel-img:hover {
  transform: scale(1.02);
}

/* 功能网格样式 */
.function-grid {
  margin: 16px;
  background: #fff;
  border-radius: 12px;
  padding: 16px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.grid-item {
  transition: all 0.3s ease;
}

.grid-item:active {
  transform: scale(0.95);
  opacity: 0.8;
}

/* 底部导航栏样式 */
.tabbar {
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}
</style>
