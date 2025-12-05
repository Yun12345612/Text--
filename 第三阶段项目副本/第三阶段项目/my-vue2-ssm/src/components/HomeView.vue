<template>
  <div class="home-view">
    <!-- 轮播图 -->
    <el-carousel height="400px" class="banner">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <img :src="item.image" :alt="item.title" class="banner-image" />
        <div class="banner-content">
          <h2>{{ item.title }}</h2>
          <p>{{ item.description }}</p>
          <el-button type="primary" size="large">立即预约</el-button>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 服务特色 -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">我们的服务特色</h2>
        <el-row :gutter="30">
          <el-col
            :xs="24"
            :sm="12"
            :md="8"
            v-for="feature in features"
            :key="feature.id"
          >
            <div class="feature-card">
              <i :class="feature.icon" :style="{ color: feature.color }"></i>
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.description }}</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 热门套餐 -->
    <section class="packages-section">
      <div class="container">
        <h2 class="section-title">热门体检套餐</h2>
        <el-row :gutter="20">
          <el-col
            :xs="24"
            :sm="12"
            :lg="8"
            v-for="pkg in packages"
            :key="pkg.id"
          >
            <el-card class="package-card" shadow="hover">
              <div slot="header">
                <h3>{{ pkg.name }}</h3>
                <div class="package-price">¥{{ pkg.price }}</div>
              </div>
              <ul class="package-features">
                <li v-for="item in pkg.features" :key="item">{{ item }}</li>
              </ul>
              <div class="package-actions">
                <el-button type="primary" @click="handleBook(pkg)"
                  >立即预约</el-button
                >
                <el-button @click="handleDetail(pkg)">查看详情</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: "HomeView",
  data() {
    return {
      // 轮播图数据
      banners: [
        {
          id: 1,
          image: require("@/assets/images/two.png"),
          title: "专业体检服务",
          description: "为您提供全面、专业的健康体检服务",
        },
        {
          id: 2,
          image: require("@/assets/images/one.png"),
          title: "先进医疗设备",
          description: "引进国际先进医疗设备，确保检查准确性",
        },
        {
          id: 3,
           image: require("@/assets/images/three.png"),
          title: "专业医疗团队",
          description: "资深医疗专家团队，为您健康保驾护航",
        },
      ],
      features: [
        {
          id: 1,
          icon: "el-icon-success",
          title: "专业可靠",
          description: "专业的医疗团队，可靠的检测结果",
          color: "#67C23A",
        },
        {
          id: 2,
          icon: "el-icon-star-on",
          title: "优质服务",
          description: "贴心周到的服务，舒适的就诊体验",
          color: "#E6A23C",
        },
        {
          id: 3,
          icon: "el-icon-time",
          title: "高效便捷",
          description: "快速出结果，方便快捷的预约流程",
          color: "#409EFF",
        },
      ],
      packages: [
        {
          id: 1,
          name: "基础体检套餐",
          price: 299,
          features: ["常规体格检查", "血常规", "尿常规", "肝功能检查"],
        },
        {
          id: 2,
          name: "全面体检套餐",
          price: 699,
          features: [
            "全面体格检查",
            "生化全项",
            "心电图",
            "B超检查",
            "肿瘤标志物",
          ],
        },
        {
          id: 3,
          name: "尊享体检套餐",
          price: 1299,
          features: [
            "VIP专属服务",
            "全面深度检查",
            "专家解读报告",
            "健康管理方案",
          ],
        },
      ],
    };
  },
  methods: {
    handleBook(pkg) {
      this.$router.push(`/booking?package=${pkg.id}`);
    },
    handleDetail(pkg) {
      this.$router.push(`/package/${pkg.id}`);
    },
  },
};
</script>

<style scoped>
.home-view {
  width: 100%;
}

/* 轮播图 */
.banner {
  margin-bottom: 60px;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
/* 轮播图内容 */
.banner-content {
  position: absolute;
  top: 50%;
  left: 10%;
  transform: translateY(-50%);
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.banner-content h2 {
  font-size: 36px;
  margin-bottom: 16px;
}

.banner-content p {
  font-size: 18px;
  margin-bottom: 24px;
}

/* 内容区块 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  font-size: 32px;
  margin-bottom: 40px;
  color: #303133;
}

/* 特色服务 */
.features-section {
  padding: 60px 0;
  background: #f8f9fa;
}

.feature-card {
  text-align: center;
  padding: 30px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card i {
  font-size: 48px;
  margin-bottom: 20px;
}

.feature-card h3 {
  margin: 20px 0 12px;
  color: #303133;
}

.feature-card p {
  color: #606266;
  line-height: 1.6;
}

/* 套餐卡片 */
.packages-section {
  padding: 60px 0;
}

.package-card {
  height: 100%;
  transition: all 0.3s;
}

.package-card:hover {
  transform: translateY(-5px);
}

.package-price {
  font-size: 24px;
  color: #e6a23c;
  font-weight: bold;
  float: right;
}

.package-features {
  list-style: none;
  padding: 0;
  margin: 0 0 20px 0;
}

.package-features li {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  color: #606266;
}

.package-features li:last-child {
  border-bottom: none;
}

.package-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-content h2 {
    font-size: 24px;
  }

  .banner-content p {
    font-size: 14px;
  }

  .section-title {
    font-size: 24px;
  }
}
</style>