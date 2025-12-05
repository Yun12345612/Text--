<template>
  <div class="project-management-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">体检项目</h1>
        <p class="page-subtitle">专业全面的体检项目，满足不同健康需求</p>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="container">
        <el-row :gutter="20" align="middle">
          <el-col :xs="24" :sm="8" :md="6">
            <div class="filter-item">
              <label>项目类型：</label>
              <el-select
                v-model="filterParams.type"
                placeholder="全部类型"
                clearable
              >
                <el-option label="基础项目" value="basic"></el-option>
                <el-option label="专项检查" value="special"></el-option>
                <el-option label="深度检查" value="deep"></el-option>
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
                <el-option label="100元以下" value="0-100"></el-option>
                <el-option label="100-500元" value="100-500"></el-option>
                <el-option label="500-1000元" value="500-1000"></el-option>
                <el-option label="1000元以上" value="1000-99999"></el-option>
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
                <el-option label="通用项目" value="general"></el-option>
                <el-option label="女性专属" value="female"></el-option>
                <el-option label="男性专属" value="male"></el-option>
                <el-option label="中老年" value="elderly"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="6">
            <div class="search-item">
              <el-input
                v-model="filterParams.keyword"
                placeholder="搜索项目名称..."
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

    <!-- 项目列表 -->
    <div v-else class="projects-section">
      <div class="container">
        <el-row :gutter="30">
          <el-col
            :xs="24"
            :sm="12"
            :lg="8"
            v-for="project in paginatedProjects"
            :key="project.itemId"
            class="project-col"
          >
            <el-card class="project-card" shadow="hover">
              <!-- 项目标签 -->
              <div class="project-tags">
                <el-tag
                  v-if="project.hot"
                  type="danger"
                  size="small"
                  class="hot-tag"
                >
                  热门
                </el-tag>
                <el-tag
                  v-if="project.recommend"
                  type="success"
                  size="small"
                  class="recommend-tag"
                >
                  推荐
                </el-tag>
                <el-tag
                  :type="project.itemStatus === 'active' ? 'success' : 'info'"
                  size="small"
                  class="status-tag"
                >
                  {{ project.itemStatus === "active" ? "启用" : "停用" }}
                </el-tag>
              </div>

              <!-- 项目头部 -->
              <div slot="header" class="project-header">
                <h3 class="project-name">{{ project.itemName }}</h3>
                <div class="project-price">
                  <span class="current-price"
                    >¥{{ project.itemPrice.toFixed(2) }}</span
                  >
                </div>
              </div>

              <!-- 项目描述 -->
              <div class="project-description">
                <p>
                  {{ project.detailDesc || "专业的体检项目，保障您的健康" }}
                </p>
              </div>

              <!-- 科室信息 -->
              <div class="project-department">
                <el-tag type="info" size="mini" class="department-tag">
                  {{ project.departmentName || "通用科室" }}
                </el-tag>
              </div>

              <!-- 包含明细 -->
              <div class="project-features">
                <h4>项目明细：</h4>
                <ul>
                  <li>
                    <i class="el-icon-check"></i>
                    {{ project.detailName || "基础检查" }}
                  </li>
                  <li v-if="project.detailDesc" class="more-info">
                    <i class="el-icon-info"></i>
                    {{
                      project.detailDesc.length > 30
                        ? project.detailDesc.substring(0, 30) + "..."
                        : project.detailDesc
                    }}
                  </li>
                </ul>
              </div>

              <!-- 操作按钮 -->
              <div class="project-actions">
                <el-button
                  type="primary"
                  size="medium"
                  @click="handleBook(project)"
                  class="book-btn"
                  :disabled="project.itemStatus !== 'active'"
                >
                  <i class="el-icon-shopping-cart-2"></i>
                  {{
                    project.itemStatus === "active" ? "立即预约" : "暂不可用"
                  }}
                </el-button>
                <el-button
                  size="medium"
                  @click="handleDetail(project)"
                  class="detail-btn"
                >
                  <i class="el-icon-view"></i>
                  查看详情
                </el-button>
                <el-button
                  size="medium"
                  @click="handleBuy(project)"
                  class="detail-btn"
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
          v-if="!loading && paginatedProjects.length === 0"
          description="暂无相关项目"
          :image-size="200"
        >
          <el-button type="primary" @click="resetFilters"
            >重置筛选条件</el-button
          >
        </el-empty>

        <!-- 分页 -->
        <div
          v-if="!loading && paginatedProjects.length > 0"
          class="pagination-section"
        >
          <el-pagination
            background
            layout="prev, pager, next, total"
            :total="filteredProjects.length"
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
import { getItemList, getDepartmentList } from "@/api/item.js";
import { Message } from "element-ui";

export default {
  name: "ProjectManagementPage",
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
      projects: [],
      loading: false,
      departmentList: [],
    };
  },
  created() {
    this.fetchProjects();
    this.loadDepartmentList();
  },
  computed: {
    filteredProjects() {
      let filtered = this.projects;

      // 关键词搜索
      if (this.filterParams.keyword) {
        const keyword = this.filterParams.keyword.toLowerCase();
        filtered = filtered.filter(
          (project) =>
            project.itemName.toLowerCase().includes(keyword) ||
            (project.detailDesc &&
              project.detailDesc.toLowerCase().includes(keyword)) ||
            (project.departmentName &&
              project.departmentName.toLowerCase().includes(keyword))
        );
      }

      // 价格范围筛选
      if (this.filterParams.priceRange) {
        const [min, max] = this.filterParams.priceRange.split("-").map(Number);
        filtered = filtered.filter(
          (project) => project.itemPrice >= min && project.itemPrice <= max
        );
      }

      // 只显示启用状态的项目
      filtered = filtered.filter((project) => project.itemStatus === "active");

      return filtered;
    },
    paginatedProjects() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredProjects.slice(start, end);
    },
  },
  methods: {
    // 加载科室列表
    loadDepartmentList() {
      getDepartmentList()
        .then((deptData) => {
          this.departmentList = deptData.list || [];
        })
        .catch((err) => {
          console.error("加载科室列表出错:", err);
        });
    },

    // 获取项目列表
    fetchProjects() {
      this.loading = true;
      const params = {
        pageNum: 0,
        pageSize: 100, // 获取更多数据用于前端筛选
        itemStatus: "active", // 只获取启用状态的项目
      };

      getItemList(params)
        .then((pageData) => {
          console.log("API响应:", pageData);
          this.projects = pageData.list || [];
          this.total = pageData.total || 0;

          if (this.projects.length === 0) {
            Message.info("暂无项目数据");
          }
        })
        .catch((err) => {
          console.error("查询异常:", err);
        })
        .finally(() => {
          this.loading = false;
        });
    },
    //
    handleSearch() {
      this.currentPage = 1;
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
    //跳转到预约
    handleBook(project) {
      this.$router.push(`/booking?project=${project.itemId}`);
    },
    // 购买项目
    handleBuy(project) {
      this.$router.push({
        path: "/buy",
        query: {
          type: "project", // 明确标识是项目类型
          id: project.itemId, // 传递项目ID
        },
      });
    },
    //跳转到查看详情
    handleDetail(project) {
      this.$router.push(`/BuyProject/${project.itemId}`);
    },
  },
};
</script>

<style scoped>
.project-management-page {
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
  color: #606266;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  margin-bottom: 0;
}

.search-item {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

/* 加载样式 */
.loading-section {
  padding: 40px 0;
}

.projects-section {
  padding: 40px 0;
}

.project-col {
  margin-bottom: 30px;
}

.project-card {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.project-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.project-tags {
  position: absolute;
  top: 45px;
  right: 15px;
  display: flex;
  flex-direction: column;
  gap: 5px;
  z-index: 1;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.project-name {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: bold;
  flex: 1;
  margin-right: 10px;
}

.project-price {
  text-align: right;
  flex-shrink: 0;
}

.current-price {
  font-size: 24px;
  color: #e6a23c;
  font-weight: bold;
}

.project-description {
  margin-bottom: 15px;
}

.project-description p {
  margin: 0;
  color: #606266;
  line-height: 1.5;
  font-size: 14px;
}

.project-department {
  margin-bottom: 15px;
}

.department-tag {
  margin-right: 5px;
}

.project-features {
  margin-bottom: 20px;
}

.project-features h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #303133;
  font-weight: bold;
}

.project-features ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.project-features li {
  padding: 6px 0;
  color: #606266;
  font-size: 13px;
  display: flex;
  align-items: center;
}

.project-features i {
  margin-right: 8px;
  color: #67c23a;
}

.more-info {
  color: #909399 !important;
  font-style: italic;
}

/* 操作按钮样式 - 与套餐页面完全一致 */
.project-actions {
  display: flex;
  gap: 8px;
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

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

/* 响应式设计 - 与套餐页面保持一致 */
@media (max-width: 768px) {
  .page-title {
    font-size: 28px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .filter-item {
    flex-direction: column;
    align-items: flex-start;
    margin-bottom: 15px;
  }

  .project-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .project-price {
    margin-top: 10px;
    text-align: left;
  }

  .project-actions {
    flex-direction: column;
  }

  .book-btn,
  .detail-btn {
    flex: none;
    width: 100%;
  }
}

@media (max-width: 480px) {
  .page-header {
    padding: 40px 0;
  }

  .projects-section {
    padding: 20px 0;
  }

  .project-col {
    margin-bottom: 20px;
  }
}
</style>