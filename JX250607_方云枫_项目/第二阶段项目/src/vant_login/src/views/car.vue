<template>
  <div class="plate-management">
    <!-- 导航栏 - 添加了左箭头返回键 -->
    <van-nav-bar
      title="车牌管理"
      left-text="返回"
      left-arrow
      @click-left="goBack"
      right-text="添加车牌"
      @click-right="showAddDialog"
    />

    <!-- 搜索栏 -->
    <div class="search-section">
      <van-search
        v-model="searchParams.plateNumber"
        placeholder="搜索车牌号码..."
        show-action
        @search="handleSearchInput"
      >
        <template #action>
          <div v-if="searchParams.plateNumber" @click="clearSearch">取消</div>
        </template>
      </van-search>
    </div>

    <!-- 车牌列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadPlateData"
      >
        <div class="plate-list">
          <van-cell-group>
            <van-swipe-cell 
              v-for="plate in filteredPlates" 
              :key="plate.id"
            >
              <van-cell :border="true">
                <div class="plate-item">
                  <div class="plate-info">
                    <div class="plate-number">{{ plate.plateNumber }}</div>
                    <div class="plate-details">
                      <van-tag size="mini" type="primary">用户ID: {{ plate.userId }}</van-tag>
                      <van-tag 
                        size="mini" 
                        :type="plate.status === 1 ? 'success' : 'danger'"
                      >
                        {{ plate.status === 1 ? '启用' : '禁用' }}
                      </van-tag>
                    </div>
                  </div>
                </div>
              </van-cell>
              <template #right>
                <van-button 
                  square 
                  type="primary" 
                  text="编辑" 
                  @click="editPlate(plate)"
                  class="swipe-edit-btn"
                />
                <van-button 
                  square 
                  type="danger" 
                  text="删除" 
                  @click="deletePlate(plate.id)"
                  class="swipe-delete-btn"
                />
              </template>
            </van-swipe-cell>
          </van-cell-group>
        </div>

        <!-- 空状态 -->
        <div v-if="filteredPlates.length === 0 && !loading" class="empty-state">
          <van-empty
            :description="searchParams.plateNumber ? emptyTip : '暂无车牌信息'"
          >
            <van-button 
              round 
              type="primary" 
              class="bottom-button"
              @click="showAddDialog"
            >
              添加车牌
            </van-button>
          </van-empty>
        </div>
      </van-list>
    </van-pull-refresh>

    <!-- 添加/编辑对话框 -->
    <van-popup
      v-model="showDialog"
      :title="isEditing ? '编辑车牌' : '添加车牌'"
      position="bottom"
      round
      :style="{ height: '50%' }"
    >
      <div class="dialog-content">
        <van-form @submit="savePlate">
          <van-field
            v-model="currentPlate.plateNumber"
            name="plateNumber"
            label="车牌号码"
            placeholder="请输入车牌号码（如：京A12345）"
            :rules="[{ required: true, message: '请输入车牌号码' }]"
          />
          <van-field
            v-model="currentPlate.userId"
            type="digit"
            name="userId"
            label="用户ID"
            placeholder="请输入用户ID"
            :rules="[{ required: true, message: '请输入用户ID' }]"
          />
          <van-cell title="启用状态">
            <template #right-icon>
              <van-switch v-model="currentPlate.status" :active-value="1" :inactive-value="0" />
            </template>
          </van-cell>
          <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
              保存
            </van-button>
            <van-button 
              round 
              block 
              plain 
              type="default" 
              style="margin-top: 10px;" 
              @click="closeDialog"
            >
              取消
            </van-button>
          </div>
        </van-form>
      </div>
    </van-popup>
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
import {
  listCarPlate,
  addCarPlate,
  updateCarPlate,
  delCarPlate
} from "@/api/login/car/index";
import { Toast, Dialog, Notify } from 'vant';

export default {
  name: "PlateManagement",
  data() {
    return {
      // 车牌列表
      plateList: [],
      
      // 列表加载状态
      loading: false,
      finished: false,
      refreshing: false,
      
      // 搜索参数
      searchParams: {
        plateNumber: '',
      },
      
      // 对话框相关
      showDialog: false,
      isEditing: false,
      currentPlate: {
        id: null,
        plateNumber: "",
        userId: null,
        status: 1
      },
      
      // 空状态提示语
      emptyTip: '未找到匹配的车牌号码'
    };
  },
  computed: {
    // 过滤后的车牌列表（前端搜索）
    filteredPlates() {
      if (!this.searchParams.plateNumber) {
        return this.plateList;
      }
      
      const searchTerm = this.searchParams.plateNumber.toLowerCase();
      return this.plateList.filter(plate => 
        plate.plateNumber.toLowerCase().includes(searchTerm)
      );
    }
  },
  mounted() {
    this.loadPlateData();
  },
  methods: {
    // 返回首页
    goBack() {
      // 根据您的路由配置，这里可以返回上一页或跳转到首页
      this.$router.push('/home'); // 跳转到首页，您可以根据实际路由调整
      // 或者使用 this.$router.go(-1); 返回上一页
    },
    
    // 加载车牌数据
    async loadPlateData() {
      this.loading = true;
      try {
        const response = await listCarPlate({
          pageNum: 1,
          pageSize: 1000,
        });
        
        // 兼容不同API响应格式
        if (response.code === 200) {
          this.plateList = response.data || [];
        } else if (response.rows) {
          this.plateList = response.rows;
        } else {
          this.plateList = response || [];
        }
        
        this.finished = true;
      } catch (error) {
        console.error('加载车牌数据失败:', error);
        this.plateList = [];
        Notify({ type: 'danger', message: '加载车牌数据失败' });
      } finally {
        this.loading = false;
        this.refreshing = false;
      }
    },

    // 下拉刷新
    onRefresh() {
      this.finished = false;
      this.loading = true;
      this.loadPlateData();
    },

    // 显示添加对话框
    showAddDialog() {
      this.isEditing = false;
      this.currentPlate = {
        id: null,
        plateNumber: "",
        userId: null,
        status: 1
      };
      this.showDialog = true;
    },

    // 编辑车牌
    editPlate(plate) {
      this.isEditing = true;
      this.currentPlate = { 
        ...plate 
      };
      this.showDialog = true;
    },

    // 关闭对话框
    closeDialog() {
      this.showDialog = false;
    },

    // 保存车牌
    async savePlate() {
      try {
        if (this.isEditing) {
          // 编辑车牌
          await updateCarPlate(this.currentPlate);
          Toast.success('车牌更新成功');
          
          // 更新本地数据
          const index = this.plateList.findIndex(p => p.id === this.currentPlate.id);
          if (index !== -1) {
            this.plateList.splice(index, 1, {...this.currentPlate});
          }
        } else {
          // 添加车牌
          const response = await addCarPlate(this.currentPlate);
          Toast.success('车牌添加成功');
          
          // 添加到本地列表（前端立即显示）
          const newPlate = {
            ...this.currentPlate,
            id: response.data?.id || Date.now() // 使用API返回的ID或临时ID
          };
          this.plateList.unshift(newPlate);
        }

        // 关闭对话框
        this.closeDialog();
        
      } catch (error) {
        console.error('保存车牌失败:', error);
        Toast.fail('操作失败，请重试');
      }
    },

    // 删除车牌
    async deletePlate(id) {
      try {
        Dialog.confirm({
          title: '确认删除',
          message: '确定要删除这个车牌吗？',
        }).then(async () => {
          await delCarPlate(id);
          Toast.success('车牌删除成功');
          
          // 从本地列表中移除
          this.plateList = this.plateList.filter(plate => plate.id !== id);
        }).catch(() => {
          // 取消删除
        });
      } catch (error) {
        console.error('删除车牌失败:', error);
        Toast.fail('删除失败，请重试');
      }
    },

    // 搜索输入处理
    handleSearchInput() {
      // 前端搜索，无需额外处理
    },
    
    // 清空搜索
    clearSearch() {
      this.searchParams.plateNumber = '';
    }
  }
};
</script>

<style scoped>
.plate-management {
  background-color: #f7f8fa;
  min-height: 100vh;
}

.search-section {
  padding: 10px 16px;
  background: white;
}

.plate-item {
  padding: 5px 0;
}

.plate-info {
  flex: 1;
}

.plate-number {
  font-size: 16px;
  font-weight: bold;
  color: #323233;
  margin-bottom: 8px;
}

.plate-details {
  display: flex;
  gap: 8px;
}

.swipe-edit-btn, .swipe-delete-btn {
  height: 100%;
}

.empty-state {
  padding: 50px 0;
}

.dialog-content {
  padding: 10px 0;
}

.bottom-button {
  width: 160px;
  height: 40px;
}

/* 导航栏返回按钮样式优化 */
:deep(.van-nav-bar__left) {
  padding: 0 10px;
}

:deep(.van-nav-bar__text) {
  color: #1989fa;
}
/* 底部导航栏样式 */
.tabbar {
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}
</style>