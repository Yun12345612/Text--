<template>
  <div class="billing-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-page-header @back="handleUserBack" content="体检开单">
      </el-page-header>
    </div>

    <!-- 主要内容区域 -->
    <el-row :gutter="20" class="main-content">
      <!-- 左侧选择区域 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="14" :xl="14">
        <!-- 搜索区域 -->
        <el-card class="search-section">
          <div slot="header" class="card-header">
            <span>搜索条件</span>
          </div>
          <el-form :model="searchForm" :inline="true" class="search-form">
            <el-form-item label="名称">
              <el-input
                v-model="searchForm.name"
                placeholder="套餐或项目名称"
                clearable
                style="width: 180px"
              />
            </el-form-item>
            <el-form-item label="类型">
              <el-select
                v-model="searchForm.type"
                placeholder="全部"
                clearable
                style="width: 120px"
              >
                <el-option label="套餐" value="package" />
                <el-option label="项目" value="item" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <el-form :model="searchForm" :inline="true" class="price-form">
            <el-form-item label="价格区间">
              <el-input
                v-model="searchForm.minPrice"
                placeholder="最小价格"
                type="number"
                style="width: 110px"
              />
              <span class="price-separator">-</span>
              <el-input
                v-model="searchForm.maxPrice"
                placeholder="最大价格"
                type="number"
                style="width: 110px"
              />
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 套餐和项目选择 -->
        <el-card class="selection-section">
          <el-tabs v-model="activeTab" class="selection-tabs">
            <!-- 套餐选项卡 -->
            <el-tab-pane label="体检套餐" name="package">
              <el-table
                :data="filteredPackages"
                border
                v-loading="packageLoading"
                class="data-table"
                :max-height="tableMaxHeight"
              >
                <el-table-column label="序号" width="60" align="center">
                  <template slot-scope="scope">{{ scope.$index + 1 }}</template>
                </el-table-column>
                <el-table-column
                  prop="packageName"
                  label="套餐名称"
                  min-width="120"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="packagePrice"
                  label="价格"
                  width="100"
                  align="center"
                >
                  <template slot-scope="scope"
                    >¥{{ scope.row.packagePrice?.toFixed(2) }}</template
                  >
                </el-table-column>
                <el-table-column
                  prop="packageDesc"
                  label="描述"
                  min-width="150"
                  show-overflow-tooltip
                />
                <el-table-column label="操作" width="150" align="center">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      size="small"
                      @click="viewPackageDetails(scope.row)"
                      >查看</el-button
                    >
                    <el-radio
                      v-model="selectedPackage"
                      :label="scope.row.packageId"
                      @change="handlePackageSelect(scope.row)"
                      >选择</el-radio
                    >
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <!-- 项目选项卡 -->
            <el-tab-pane label="体检项目" name="item">
              <el-table
                :data="filteredItems"
                border
                v-loading="itemLoading"
                class="data-table"
                :max-height="tableMaxHeight"
              >
                <el-table-column label="序号" width="60" align="center">
                  <template slot-scope="scope">{{ scope.$index + 1 }}</template>
                </el-table-column>
                <el-table-column
                  prop="itemName"
                  label="项目名称"
                  min-width="120"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="itemPrice"
                  label="价格"
                  width="100"
                  align="center"
                >
                  <template slot-scope="scope"
                    >¥{{ scope.row.itemPrice?.toFixed(2) }}</template
                  >
                </el-table-column>
                <el-table-column
                  prop="detailName"
                  label="明细"
                  min-width="120"
                  show-overflow-tooltip
                />
                <el-table-column
                  prop="departmentName"
                  label="科室"
                  width="100"
                  align="center"
                />
                <el-table-column label="操作" width="150" align="center">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      size="small"
                      @click="viewItemDetails(scope.row)"
                      >查看</el-button
                    >
                    <el-radio
                      v-model="selectedItemsMap"
                      :label="scope.row.itemId"
                      @change="handleItemSelect(scope.row)"
                      >选择</el-radio
                    >
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>

      <!-- 右侧信息区域 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="10" :xl="10">
        <!-- 已选内容 -->
        <el-card class="selected-section">
          <div slot="header" class="card-header">
            <span>已选体检内容</span>
            <span class="selected-count"
              >({{ selectedItemsList.length }}项)</span
            >
          </div>
          <el-table
            :data="selectedItemsList"
            border
            class="selected-table"
            :max-height="selectedTableMaxHeight"
          >
            <el-table-column label="序号" width="60" align="center">
              <template slot-scope="scope">{{ scope.$index + 1 }}</template>
            </el-table-column>
            <el-table-column
              prop="type"
              label="类型"
              width="80"
              align="center"
            />
            <el-table-column
              prop="name"
              label="名称"
              min-width="120"
              show-overflow-tooltip
            />
            <el-table-column
              prop="price"
              label="价格"
              width="100"
              align="center"
            >
              <template slot-scope="scope"
                >¥{{ scope.row.price?.toFixed(2) }}</template
              >
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  style="color: #f56c6c"
                  @click="removeItem(scope.row)"
                >
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 底部操作区域 -->
          <div class="action-section">
            <div class="total-section">
              <span class="total-label">总计：</span>
              <span class="total-amount">¥{{ totalAmount }}</span>
            </div>
            <div class="button-group">
              <el-button
                type="primary"
                @click="handleSubmit"
                :loading="submitting"
                size="medium"
              >
                提交开单
              </el-button>
              <el-button @click="handleClear" size="medium">清空选择</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 提交确认弹窗 -->
    <el-dialog
      title="确认开单信息"
      :visible.sync="confirmDialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <div v-if="userInfo" class="confirm-dialog">
        <div class="user-info-section">
          <h4 style="margin-bottom: 15px; color: #303133">用户信息</h4>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="用户ID">
              {{ userInfo.userId || "暂无" }}
            </el-descriptions-item>
            <el-descriptions-item label="姓名">
              {{ userInfo.userName || "暂无" }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ userInfo.userPhone || "暂无" }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{
                userInfo.userGender === "1"
                  ? "男"
                  : userInfo.userGender === "0"
                  ? "女"
                  : "暂无"
              }}
            </el-descriptions-item>
            <el-descriptions-item label="年龄">
              {{ userInfo.userAge || "暂无" }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="order-info-section" style="margin-top: 20px">
          <h4 style="margin-bottom: 15px; color: #303133">订单信息</h4>
          <div class="order-details">
            <p><strong>已选项目：</strong>{{ selectedItemsList.length }}项</p>
            <p><strong>总计金额：</strong>¥{{ totalAmount }}</p>
            <p><strong>包含内容：</strong></p>
            <ul class="selected-items-list">
              <li
                v-for="(item, index) in selectedItemsList"
                :key="index"
                class="selected-item"
              >
                {{ item.type }} - {{ item.name }} (¥{{
                  item.price?.toFixed(2)
                }})
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div v-else class="no-user-info">
        <p style="text-align: center; color: #909399">用户信息加载中...</p>
      </div>

      <div slot="footer">
        <el-button @click="handleCancelSubmit">取消</el-button>
        <el-button type="primary" @click="confirmSubmit" :loading="submitting">
          确认提交
        </el-button>
      </div>
    </el-dialog>
    <!-- 套餐详情弹窗 -->
    <el-dialog
      title="套餐详情"
      :visible.sync="packageDetailVisible"
      width="600px"
    >
      <div v-if="currentPackage" class="package-detail">
        <h3 style="text-align: center; margin-bottom: 20px">
          {{ currentPackage.packageName }}
        </h3>
        <div class="package-info">
          <p>
            <strong>价格：</strong>¥{{
              currentPackage.packagePrice?.toFixed(2)
            }}
          </p>
          <p>
            <strong>描述：</strong
            >{{ currentPackage.packageDesc || "暂无描述" }}
          </p>
          <p><strong>包含项目：</strong></p>
        </div>

        <!-- 显示项目名称字符串 -->
        <div
          v-if="currentPackage.itemName"
          style="
            margin: 15px 0;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 4px;
            border-left: 4px solid #409eff;
          "
        >
          <p style="margin: 0; font-weight: 500; color: #303133">项目列表：</p>
          <p style="margin: 8px 0 0 0; color: #606266; line-height: 1.5">
            {{ currentPackage.itemName }}
          </p>
        </div>

        <!-- 显示项目列表 -->
        <div
          v-else-if="currentPackage.items && currentPackage.items.length > 0"
        >
          <el-table
            :data="currentPackage.items"
            border
            size="small"
            style="margin-top: 10px"
          >
            <el-table-column label="序号" width="60" align="center">
              <template slot-scope="scope">{{ scope.$index + 1 }}</template>
            </el-table-column>
            <el-table-column prop="itemName" label="项目名称" align="center" />
            <el-table-column
              prop="itemPrice"
              label="价格"
              width="100"
              align="center"
            >
              <template slot-scope="scope"
                >¥{{ scope.row.itemPrice?.toFixed(2) }}</template
              >
            </el-table-column>
          </el-table>
        </div>

        <!-- 没有项目信息时的提示 -->
        <div v-else style="color: #909399; text-align: center; padding: 20px">
          <i
            class="el-icon-info"
            style="font-size: 24px; margin-bottom: 10px; display: block"
          ></i>
          <p>暂无项目详情</p>
          <p style="font-size: 12px; margin-top: 10px">套餐项目信息暂不可用</p>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="packageDetailVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 项目详情弹窗 -->
    <el-dialog title="项目详情" :visible.sync="itemDetailVisible" width="500px">
      <div v-if="currentItem" class="item-detail">
        <h3 style="text-align: center; margin-bottom: 20px">
          {{ currentItem.itemName }}
        </h3>
        <div class="item-info">
          <p><strong>价格：</strong>¥{{ currentItem.itemPrice?.toFixed(2) }}</p>
          <p>
            <strong>明细名称：</strong>{{ currentItem.detailName || "暂无" }}
          </p>
          <p>
            <strong>明细描述：</strong
            >{{ currentItem.detailDesc || "暂无描述" }}
          </p>
          <p>
            <strong>所属科室：</strong
            >{{ currentItem.departmentName || "暂无" }}
          </p>
          <p>
            <strong>项目状态：</strong>
            <el-tag
              :type="currentItem.itemStatus === 'active' ? 'success' : 'info'"
              size="small"
            >
              {{ currentItem.itemStatus === "active" ? "启用" : "停用" }}
            </el-tag>
          </p>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="itemDetailVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message } from "element-ui";
import { getUserList } from "@/api/user.js";
import { examinationPage } from "@/api/package.js";
import { getItemList } from "@/api/item.js";
import { createOrder } from "@/api/order.js";

export default {
  name: "UserBillingView",
  props: {
    userId: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      //确认用户开单弹窗
      confirmDialogVisible: false,
      userInfo: {},
      localUserId: "", // 用于存储处理后的用户ID
      packageList: [],
      itemList: [],
      packageLoading: false,
      itemLoading: false,
      activeTab: "package",
      searchForm: {
        name: "",
        type: "",
        minPrice: "",
        maxPrice: "",
      },
      selectedPackage: "",
      selectedItemsMap: [],
      selectedItemsList: [],
      packageDetailVisible: false,
      itemDetailVisible: false,
      currentPackage: null,
      currentItem: null,
      submitting: false,
      tableMaxHeight: 400,
      selectedTableMaxHeight: 300,
    };
  },
  computed: {
    filteredPackages() {
      let packages = this.packageList.filter(
        (pkg) => pkg.packageStatus === "active" && pkg.packageIsDelete !== 1
      );

      if (this.searchForm.name) {
        packages = packages.filter((pkg) =>
          pkg.packageName
            ?.toLowerCase()
            .includes(this.searchForm.name.toLowerCase())
        );
      }

      if (this.searchForm.minPrice) {
        packages = packages.filter(
          (pkg) => pkg.packagePrice >= parseFloat(this.searchForm.minPrice)
        );
      }

      if (this.searchForm.maxPrice) {
        packages = packages.filter(
          (pkg) => pkg.packagePrice <= parseFloat(this.searchForm.maxPrice)
        );
      }

      return packages;
    },

    filteredItems() {
      let items = this.itemList.filter(
        (item) => item.itemStatus === "active" && item.itemIsDelete !== 1
      );

      if (this.searchForm.name) {
        items = items.filter(
          (item) =>
            item.itemName
              ?.toLowerCase()
              .includes(this.searchForm.name.toLowerCase()) ||
            item.detailName
              ?.toLowerCase()
              .includes(this.searchForm.name.toLowerCase())
        );
      }

      if (this.searchForm.minPrice) {
        items = items.filter(
          (item) => item.itemPrice >= parseFloat(this.searchForm.minPrice)
        );
      }

      if (this.searchForm.maxPrice) {
        items = items.filter(
          (item) => item.itemPrice <= parseFloat(this.searchForm.maxPrice)
        );
      }

      return items;
    },

    totalAmount() {
      return this.selectedItemsList
        .reduce((total, item) => total + (parseFloat(item.price) || 0), 0)
        .toFixed(2);
    },
  },
  mounted() {
    this.getUserId(); //获取用户ID
    this.loadUserInfo(); //加载用户信息
    this.loadPackageList(); //加载套餐列表
    this.loadItemList(); //加载项目列表
    this.calculateTableHeight(); //加载时计算表格高度
    window.addEventListener("resize", this.calculateTableHeight); //监听窗口大小变化
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.calculateTableHeight);
  },
  methods: {
    getUserId() {
      // 多途径获取用户ID
      this.localUserId =
        this.userId ||
        this.$route.query.userId ||
        localStorage.getItem("currentBillingUserId");

      console.log("用户ID获取详情:", {
        fromProps: this.userId,
        fromQuery: this.$route.query.userId,
        fromLocalStorage: localStorage.getItem("currentBillingUserId"),
        finalUserId: this.localUserId,
      });

      if (!this.localUserId) {
        console.error("未找到用户ID");
        Message.error("用户信息缺失，请从用户列表进入");
        this.handleBack();
        this.$router.push;
        return;
      }
    },

    // 加载用户信息 - 使用现有接口
    loadUserInfo() {
      if (!this.localUserId) {
        Message.warning("用户ID为空，无法加载用户信息");
        return;
      }

      console.log("正在加载用户信息，用户ID:", this.localUserId);

      // 使用现有的 getUserList 接口，通过 userId 精确查询
      getUserList({
        userId: this.localUserId,
        pageNum: 1,
        pageSize: 1, // 只需要一条记录
      })
        .then((response) => {
          console.log("用户信息响应:", response);

          // 根据接口返回结构处理
          if (response && response.list && response.list.length > 0) {
            this.userInfo = response.list[0];
            console.log("成功加载用户信息:", this.userInfo);
          } else if (response && response.data) {
            // 如果接口返回格式是 { data: userInfo }
            this.userInfo = response.data;
          } else {
            this.userInfo = {};
            Message.warning("未找到对应的用户信息");
          }
        })
        .catch((err) => {
          console.error("加载用户信息失败:", err);
          Message.error("加载用户信息失败");
        });
    },
    // 计算表格最大高度
    calculateTableHeight() {
      const windowHeight = window.innerHeight;
      this.tableMaxHeight = windowHeight - 400;
      this.selectedTableMaxHeight = windowHeight - 1000;
    },

    // 在加载数据时统一转换
    loadPackageList() {
      this.packageLoading = true;
      examinationPage({
        pageNum: 0,
        pageSize: 1000,
        packageStatus: "active",
      })
        .then((response) => {
          console.log("套餐列表响应:", response);
          // 统一转换价格为元
          this.packageList = (response.list || []).map((pkg) => ({
            ...pkg,
            packagePrice: pkg.packagePrice / 100,
          }));
        })
        .catch((err) => {
          console.error("加载套餐列表失败:", err);
          Message.error("加载套餐列表失败");
        })
        .finally(() => {
          this.packageLoading = false;
        });
    },

    loadItemList() {
      this.itemLoading = true;
      getItemList({
        pageNum: 0,
        pageSize: 1000,
        itemStatus: "active",
      })
        .then((response) => {
          // 统一转换价格为元
          this.itemList = (response.list || []).map((item) => ({
            ...item,
            itemPrice: item.itemPrice / 100,
          }));
        })
        .catch((err) => {
          console.error("加载项目列表失败:", err);
          Message.error("加载项目列表失败");
        })
        .finally(() => {
          this.itemLoading = false;
        });
    },
    //选择套餐
    handlePackageSelect(packageItem) {
      const packageIndex = this.selectedItemsList.findIndex(
        (selected) =>
          selected.type === "套餐" && selected.id === packageItem.packageId
      );

      if (packageIndex > -1) {
        // 如果已存在该套餐，就移除（取消选择）
        this.selectedItemsList.splice(packageIndex, 1);
        this.selectedPackage = ""; // 清空单选按钮
      } else {
        // 如果不存在该套餐，就添加（选择）
        this.selectedItemsList.push({
          type: "套餐",
          name: packageItem.packageName,
          price: packageItem.packagePrice,
          id: packageItem.packageId,
          originalData: packageItem,
        });
      }
    },
    //选择项目
    handleItemSelect(item) {
      const itemIndex = this.selectedItemsList.findIndex(
        (selected) => selected.type === "项目" && selected.id === item.itemId
      );

      if (itemIndex > -1) {
        this.selectedItemsList.splice(itemIndex, 1);
        this.selectedItemsMap = this.selectedItemsMap.filter(
          (id) => id !== item.itemId
        );
      } else {
        this.selectedItemsList.push({
          type: "项目",
          name: item.itemName,
          price: item.itemPrice,
          id: item.itemId,
          originalData: item,
        });
      }
    },

    handleSearch() {
      // 搜索逻辑
    },

    handleReset() {
      this.searchForm = { name: "", type: "", minPrice: "", maxPrice: "" };
    },

    viewPackageDetails(packageItem) {
      console.log("查看套餐详情:", packageItem);

      // 直接使用套餐列表中的信息
      this.currentPackage = { ...packageItem };

      // 尝试解析套餐中的项目信息
      this.parsePackageItems(packageItem);

      this.packageDetailVisible = true;
    },

    // 解析套餐中的项目信息
    parsePackageItems(packageItem) {
      let items = [];

      // 方法1: 如果套餐有项目名称字符串，解析它
      if (packageItem.itemName) {
        console.log("套餐有项目名称字符串:", packageItem.itemName);
        items = this.parseItemNameString(packageItem.itemName);
      }
      // 方法2: 如果套餐有项目ID数组，从项目列表中查找
      else if (packageItem.itemIds && Array.isArray(packageItem.itemIds)) {
        console.log("套餐有项目ID数组:", packageItem.itemIds);
        items = this.findItemsByIds(packageItem.itemIds);
      }
      // 方法3: 如果套餐直接包含项目列表
      else if (packageItem.items && Array.isArray(packageItem.items)) {
        console.log("套餐直接包含项目列表:", packageItem.items);
        items = packageItem.items;
      }

      console.log("解析后的项目列表:", items);
      this.currentPackage.items = items;
    },

    // 解析项目名称字符串
    parseItemNameString(itemName) {
      if (!itemName) return [];

      // 假设项目名称以顿号、逗号或分号分隔
      const separators = /[、,，;；]/;
      const itemNames = itemName.split(separators);

      return itemNames
        .map((name) => name.trim())
        .filter((name) => name)
        .map((name, index) => ({
          itemId: index + 1, // 临时ID
          itemName: name,
          itemPrice: 0, // 未知价格
        }));
    },

    // 根据项目ID从项目列表中查找
    findItemsByIds(itemIds) {
      return itemIds
        .map((itemId) => {
          const item = this.itemList.find((i) => i.itemId === itemId);
          return item
            ? {
                itemId: item.itemId,
                itemName: item.itemName,
                itemPrice: item.itemPrice,
              }
            : null;
        })
        .filter(Boolean);
    },
    // 查看项目详情
    viewItemDetails(item) {
      this.currentItem = item;
      this.itemDetailVisible = true;
    },
    // 移除已选项
    removeItem(item) {
      const index = this.selectedItemsList.findIndex(
        (selected) => selected.id === item.id && selected.type === item.type
      );
      // 从已选列表中移除
      if (index > -1) {
        this.selectedItemsList.splice(index, 1);
        if (item.type === "套餐") {
          this.selectedPackage = "";
        } else if (item.type === "项目") {
          this.selectedItemsMap = this.selectedItemsMap.filter(
            (id) => id !== item.id
          );
        }
      }
    },
    // 清空所有选择
    handleClear() {
      this.selectedPackage = "";
      this.selectedItemsMap = [];
      this.selectedItemsList = [];
      this.submitting = false; // 重置提交状态
      this.confirmDialogVisible = false; // 关闭确认弹窗
      Message.info("已清空所有选择");
    },
    // 提交开单前的确认
    handleSubmit() {
      if (this.selectedItemsList.length === 0) {
        Message.warning("请至少选择一个体检项目或套餐");
        return;
      }
      // 显示确认弹窗
      this.confirmDialogVisible = true;
    },
    //确认提交当前用户信息的弹窗
    confirmSubmit() {
      this.submitting = true;
      // 模拟加载用户信息
      const orderData = {
        userId: this.localUserId,
        items: this.selectedItemsList,
        totalAmount: this.totalAmount,
        orderTime: new Date().toISOString(),
      };
      // 调用开单接口
      createOrder(orderData)
        .then(() => {
          Message.success("开单成功");
          this.confirmDialogVisible = false; // 关闭确认弹窗
          this.handleBack();
        })
        .catch((err) => {
          console.error("开单失败:", err);
          Message.error("开单失败");
          this.confirmDialogVisible = false; // 关闭确认弹窗
        })
        .finally(() => {
          this.submitting = false; // 重置提交状态
        });
    },
    // 取消提交（在确认弹窗中点击取消）
    handleCancelSubmit() {
      this.confirmDialogVisible = false;
      this.submitting = false; // 取消时也重置提交状态
    },
    // 返回用户列表
    handleUserBack() {
      this.$router.push({ name: "UserInfo" });
    },
  },
};
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin-left: 20px;
  margin-bottom: 0;
  color: #303133;
}

.main-content {
  margin-bottom: 20px;
}

/* 卡片通用样式 */
.search-section,
.selection-section,
.selected-section {
  margin-bottom: 50px;
}

.card-header {
  font-weight: 1000;
  color: #303133;
}

.selected-count {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

/* 搜索表单样式 */
.search-form {
  margin-bottom: 10px;
}

.price-form {
  margin-bottom: 0;
}

.price-separator {
  margin: 0 8px;
  color: #909399;
}

/* 表格样式 */
.data-table,
.selected-table {
  width: 100%;
}

/* 操作列样式 */
.data-table >>> .el-table__body-wrapper .el-radio,
.data-table >>> .el-table__body-wrapper .el-button {
  margin: 0 2px;
}

/* 底部操作区域 */
.action-section {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.total-section {
  text-align: center;
  margin-bottom: 15px;
}

.total-label {
  font-size: 16px;
  color: #606266;
}

.total-amount {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  margin-left: 8px;
}

.button-group {
  display: flex;
  justify-content: space-between;
}

/* 详情弹窗样式 */
.package-detail,
.item-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.package-info,
.item-info {
  margin-bottom: 20px;
}

.package-info p,
.item-info p {
  margin-bottom: 10px;
  line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .billing-container {
    padding: 15px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .page-header h2 {
    margin-left: 0;
    margin-top: 10px;
  }

  .button-group {
    flex-direction: column;
    gap: 10px;
  }

  .button-group .el-button {
    width: 100%;
  }
}

@media (max-width: 1200px) {
  .main-content .el-col {
    margin-bottom: 20px;
  }
}
</style>