  <template>
    <div class="packages-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="container">
          <h1 class="page-title">体检套餐</h1>
          <p class="page-subtitle">专业全面的健康体检方案，满足不同人群需求</p>
        </div>
      </div>

      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="container">
          <el-row :gutter="20" align="middle">
            <el-col :xs="24" :sm="8" :md="6">
              <div class="filter-item">
                <label>套餐类型：</label>
                <el-select
                  v-model="filterParams.type"
                  placeholder="全部类型"
                  clearable
                >
                  <el-option label="基础套餐" value="basic"></el-option>
                  <el-option label="全面套餐" value="comprehensive"></el-option>
                  <el-option label="尊享套餐" value="premium"></el-option>
                  <el-option label="专项套餐" value="special"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :xs="24" :sm="8" :md="6">
              <div class="filter-item">
                <label>价格范围：</label>
                <el-select
                  v-model="filterParams.priceRange"
                  placeholder="全部价格"
                  clearable
                >
                  <el-option label="500元以下" value="0-500"></el-option>
                  <el-option label="500-1000元" value="500-1000"></el-option>
                  <el-option label="1000-2000元" value="1000-2000"></el-option>
                  <el-option label="2000元以上" value="2000-99999"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :xs="24" :sm="8" :md="6">
              <div class="filter-item">
                <label>适用人群：</label>
                <el-select
                  v-model="filterParams.target"
                  placeholder="全部人群"
                  clearable
                >
                  <el-option label="青年人群" value="youth"></el-option>
                  <el-option label="中年人群" value="middle"></el-option>
                  <el-option label="老年人群" value="elderly"></el-option>
                  <el-option label="女性专属" value="female"></el-option>
                  <el-option label="男性专属" value="male"></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :xs="24" :sm="24" :md="6">
              <div class="search-item">
                <el-input
                  v-model="filterParams.keyword"
                  placeholder="搜索套餐名称..."
                  clearable
                  @clear="handleSearch"
                  @keyup.enter="handleSearch"
                >
                  <el-button
                    slot="append"
                    icon="el-icon-search"
                    @click="handleSearch"
                  ></el-button>
                </el-input>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-section">
        <div class="container">
          <el-skeleton :rows="6" animated />
        </div>
      </div>

      <!-- 套餐列表 -->
      <div v-else class="packages-section">
        <div class="container">
          <el-row :gutter="30">
            <el-col
              :xs="24"
              :sm="12"
              :lg="8"
              v-for="pkg in paginatedPackages"
              :key="pkg.id"
              class="package-col"
            >
              <el-card class="package-card" shadow="hover">
                <!-- 套餐标签 -->
                <div class="package-tags">
                  <el-tag
                    v-if="pkg.hot"
                    type="danger"
                    size="small"
                    class="hot-tag"
                  >
                    热门
                  </el-tag>
                  <el-tag
                    v-if="pkg.recommend"
                    type="success"
                    size="small"
                    class="recommend-tag"
                  >
                    推荐
                  </el-tag>
                  <el-tag
                    v-if="pkg.discount"
                    type="warning"
                    size="small"
                    class="discount-tag"
                  >
                    特惠
                  </el-tag>
                </div>

                <!-- 套餐头部 -->
                <div slot="header" class="package-header">
                  <h3 class="package-name">{{ pkg.name }}</h3>
                  <div class="package-price">
                    <span class="current-price">¥{{ pkg.price }}</span>
                    <span v-if="pkg.originalPrice" class="original-price">
                      ¥{{ pkg.originalPrice }}
                    </span>
                  </div>
                </div>

                <!-- 套餐描述 -->
                <div class="package-description">
                  <p>{{ pkg.description }}</p>
                </div>

                <!-- 适用人群 -->
                <div class="package-target">
                  <el-tag
                    :type="getTargetType(pkg.target)"
                    size="mini"
                    class="target-tag"
                  >
                    {{ getTargetText(pkg.target) }}
                  </el-tag>
                </div>

                <!-- 包含项目 -->
                <div class="package-features">
                  <h4>包含项目：</h4>
                  <ul>
                    <li
                      v-for="(item, index) in (pkg.features || []).slice(0, 4)"
                      :key="index"
                    >
                      <i class="el-icon-check"></i>
                      {{ item }}
                    </li>
                    <li v-if="(pkg.features || []).length > 4" class="more-items">
                      <i class="el-icon-more"></i>
                      还有{{ (pkg.features || []).length - 4 }}个项目
                    </li>
                  </ul>
                </div>

                <!-- 操作按钮 -->
                <div class="package-actions">
                  <el-button
                    type="primary"
                    size="medium"
                    @click="handleBook(pkg)"
                    class="book-btn"
                  >
                    <i class="el-icon-shopping-cart-2"></i>
                    立即预约
                  </el-button>
                  <el-button
                    size="medium"
                    @click="handleDetail(pkg)"
                    class="detail-btn"
                  >
                    <i class="el-icon-view"></i>
                    查看详情
                  </el-button>
                  <el-button
                    type="success"
                    size="medium"
                    @click="handleBuy(pkg)"
                    class="buy-btn"
                  >
                    <i class="el-icon-wallet"></i>
                    立即购买
                  </el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 空状态 -->
          <el-empty
            v-if="!loading && paginatedPackages.length === 0"
            description="暂无相关套餐"
            :image-size="200"
          >
            <el-button type="primary" @click="resetFilters"
              >重置筛选条件</el-button
            >
          </el-empty>

          <!-- 分页 -->
          <div
            v-if="!loading && paginatedPackages.length > 0"
            class="pagination-section"
          >
            <el-pagination
              background
              layout="prev, pager, next, total"
              :total="filteredPackages.length"
              :page-size="pageSize"
              :current-page="currentPage"
              @current-change="handlePageChange"
            >
            </el-pagination>
          </div>
        </div>
      </div>
    </div>
  </template>

  <script>
  import {
    examinationGetAll,
    examinationPage,
  } from "@/api/examinationPackage.js";

  export default {
    name: "PackagesView",
    data() {
      return {
        filterParams: {
          type: "",
          priceRange: "",
          target: "",
          keyword: "",
        },
        currentPage: 1,
        pageSize: 9,
        packages: [],
        loading: false,
      };
    },
    created() {
      this.fetchPackages();
    },
    computed: {
      filteredPackages() {
        let filtered = this.packages;

        // 类型筛选
        if (this.filterParams.type) {
          filtered = filtered.filter(
            (pkg) => pkg.type === this.filterParams.type
          );
        }

        // 价格范围筛选
        if (this.filterParams.priceRange) {
          const [min, max] = this.filterParams.priceRange.split("-").map(Number);
          filtered = filtered.filter(
            (pkg) => pkg.price >= min && pkg.price <= max
          );
        }

        // 适用人群筛选
        if (this.filterParams.target) {
          filtered = filtered.filter(
            (pkg) => pkg.target === this.filterParams.target
          );
        }

        // 关键词搜索
        if (this.filterParams.keyword) {
          const keyword = this.filterParams.keyword.toLowerCase();
          filtered = filtered.filter(
            (pkg) =>
              pkg.name.toLowerCase().includes(keyword) ||
              pkg.description.toLowerCase().includes(keyword)
          );
        }

        return filtered;
      },
      paginatedPackages() {
        const start = (this.currentPage - 1) * this.pageSize;
        const end = start + this.pageSize;
        return this.filteredPackages.slice(start, end);
      },
    },
    methods: {
      // 购买套餐
      handleBuy(pkg) {
        console.log(JSON.parse(JSON.stringify(pkg)));
        this.$router.push({
          path: "/buy",
          query: {
            type: "package", // 标识是套餐类型
            id: pkg.id,
          },
        });
      },
      // 正确定义getTargetType和getTargetText方法
      getTargetType(target) {
        const typeMap = {
          youth: "success",
          middle: "warning",
          elderly: "danger",
          female: "info",
          male: "primary",
        };
        return typeMap[target] || "info";
      },
      getTargetText(target) {
        const textMap = {
          youth: "青年人群",
          middle: "中年人群",
          elderly: "老年人群",
          female: "女性专属",
          male: "男性专属",
        };
        return textMap[target] || "通用";
      },
      handleSearch() {
        this.currentPage = 1;
      },
      // 获取套餐数据
      async fetchPackages() {
        this.loading = true;
        try {
          const response = await examinationGetAll();
          // 在 fetchPackages 方法中，对价格进行单位转换
          this.packages = response.map((item) => ({
            id: item.packageId,
            name: item.packageName,
            price: item.packagePrice,
            description: item.packageDesc,
            target: "general",
            features: [item.itemName || "基础体检项目"],
            status: item.packageStatus,
          }));
          console.log("映射后的数据:", this.packages);
        } catch (error) {
          console.error("获取套餐失败:", error);
        } finally {
          this.loading = false;
        }
      },
      resetFilters() {
        this.filterParams = {
          type: "",
          priceRange: "",
          target: "",
          keyword: "",
        };
        this.currentPage = 1;
      },
      handlePageChange(page) {
        this.currentPage = page;
      },
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
  .packages-page {
    min-height: 100vh;
    background: #f8f9fa;
  }

  .page-header {
    background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
    color: white;
    padding: 60px 0;
    text-align: center;
  }

  .page-title {
    font-size: 36px;
    margin: 0 0 16px 0;
    font-weight: bold;
  }

  .page-subtitle {
    font-size: 18px;
    margin: 0;
    opacity: 0.9;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .filter-section {
    background: white;
    padding: 30px 0;
    border-bottom: 1px solid #e6e6e6;
  }

  .filter-item {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .filter-item label {
    white-space: nowrap;
    color: #606266;
    font-size: 14px;
  }

  .search-item {
    display: flex;
    justify-content: flex-end;
  }

  /* 加载样式 */
  .loading-section {
    padding: 40px 0;
  }

  .loading-section .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .packages-section {
    padding: 40px 0;
  }

  .package-col {
    margin-bottom: 30px;
  }

  .package-card {
    height: 100%;
    transition: all 0.3s ease;
    border-radius: 12px;
    position: relative;
    overflow: hidden;
  }

  .package-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  }

  .package-tags {
    position: absolute;
    top: 15px;
    right: 15px;
    display: flex;
    flex-direction: column;
    gap: 5px;
    z-index: 1;
  }

  .package-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 15px;
  }

  .package-name {
    margin: 0;
    font-size: 18px;
    color: #303133;
    font-weight: bold;
    flex: 1;
    margin-right: 10px;
  }

  .package-price {
    text-align: right;
    flex-shrink: 0;
  }

  .current-price {
    font-size: 24px;
    color: #e6a23c;
    font-weight: bold;
  }

  .original-price {
    display: block;
    font-size: 14px;
    color: #909399;
    text-decoration: line-through;
    margin-top: 4px;
  }

  .package-description {
    margin-bottom: 15px;
  }

  .package-description p {
    margin: 0;
    color: #606266;
    line-height: 1.5;
    font-size: 14px;
  }

  .package-target {
    margin-bottom: 15px;
  }

  .target-tag {
    margin-right: 5px;
  }

  .package-features {
    margin-bottom: 20px;
  }

  .package-features h4 {
    margin: 0 0 10px 0;
    font-size: 14px;
    color: #303133;
    font-weight: bold;
  }

  .package-features ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .package-features li {
    padding: 6px 0;
    color: #606266;
    font-size: 13px;
    display: flex;
    align-items: center;
  }

  .package-features i {
    margin-right: 8px;
    color: #67c23a;
  }

  .more-items {
    color: #409eff !important;
    font-style: italic;
  }
  .package-actions {
    display: flex;
    gap: 8px; /* 调整为8px间距，与项目页面一致 */
    justify-content: center;
  }

  .book-btn {
    flex: 2;
    font-size: 14px;
    height: 36px;
    padding: 8px 15px;
  }

  .detail-btn {
    flex: 1;
    font-size: 14px;
    height: 36px;
    padding: 8px 15px;
  }

  .buy-btn {
    flex: 1;
    font-size: 14px;
    height: 36px;
    padding: 8px 15px;
  }

  /* 响应式设计 - 与项目页面保持一致 */
  @media (max-width: 768px) {
    .package-actions {
      flex-direction: column;
    }

    .book-btn,
    .detail-btn,
    .buy-btn {
      flex: none;
      width: 100%;
    }
  }
  </style>