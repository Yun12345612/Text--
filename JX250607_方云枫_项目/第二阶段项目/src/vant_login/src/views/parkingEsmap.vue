<template>
  <div class="parking-container">
    <van-nav-bar
      title="反向寻车"
      left-text="返回"
      left-arrow
      @click-left="goBack"
    >
      <!-- 在右侧添加2D/3D文字切换按钮 -->
      <template #right>
        <div class="view-text-switcher">
          <span 
            :class="{ active: currentView === '2d' }"
            @click="switchTo2D"
          >2D</span>
          <span class="divider">/</span>
          <span 
            :class="{ active: currentView === '3d' }"
            @click="switchTo3D"
          >3D</span>
        </div>
      </template>
    </van-nav-bar>
    
    <!-- 模型的地图容器 -->
    <div id="map-container" class="map-container"></div>
    
    <!-- 移除原来的2D/3D切换按钮容器 -->
    
    <!-- 停车位状态显示 -->
    <div class="parking fix">
      <span :style="{ color: 'rgb(255, 255, 0)' }">{{ selectedSpot.spaceNumber }}</span>
      车位情况：<span>{{ selectedSpot.status }} {{ selectedSpot.plateNumber }}</span>
    </div>
    
    <!-- 反向导航按钮 -->
    <div class="reverse-nav fix" @click="handleReverseNavi">
      <span>反向导航</span>
    </div>
    
    <!-- 图例 -->
    <div class="codition fix">
      <ul>
        <li><span class="codition-first"></span>占用车位</li>
        <li><span class="codition-second"></span>空闲车位</li>
        <li><span class="codition-third"></span>固定车位</li>
      </ul>
    </div>
    
    <!-- 统计信息横幅 -->
    <div class="i-test-tip fix">
      <div class="test-tip" ref="marquee">
        停车场车位总数：<span>{{ totalSpots }}</span>个，
        当前剩余车位数 <span>{{ availableSpots }}</span>。
        <template v-if="floorStats.length">
          各层剩余：
          <span v-for="(stat, index) in floorStats" :key="index">
            {{ stat.floor }}层:{{ stat.available }}个
            <template v-if="index < floorStats.length - 1">, </template>
          </span>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import MapApplication from "@/esmap/MapApplication";
import { listParking } from "@/api/login/car/car";
import { Toast } from 'vant';

export default {
  name: "ParkingLot",
  data() {
    return {
      totalSpots: 0,
      availableSpots: 0,
      floorStats: [],
      selectedSpot: {
        id: "",
        plateName: "",
        plateNumber: "",
        status: "",
      },
      parkingData: [],
      statusNames: ["固定车位", "有车", "无车"],
      pollingInterval: null,
      currentView: "3d", // 跟踪当前视角，默认3D
    };
  },
  mounted() {
    this.initializeMap();
    this.startDataPolling();
    this.setupEventListeners();
    this.initMarquee();
  },
  beforeDestroy() {
    this.stopDataPolling();
    MapApplication.destroyMap();
    window.removeEventListener("parking-spot-click", this.handleSpotClick);
  },
  methods: {
    // 返回首页
    goBack() {
      this.$router.push("/home");
    },
    
    // 切换到2D视角
    switchTo2D() {
      if (MapApplication.map && esmap.ESViewMode) {
        MapApplication.map.viewMode = esmap.ESViewMode.MODE_2D;
        esmap.ESTileMap.viewMode = esmap.ESViewMode.MODE_2D;
        this.currentView = "2d";
        Toast('已切换到2D视角');
      }
    },
    
    // 切换到3D视角
    switchTo3D() {
      if (MapApplication.map && esmap.ESViewMode) {
        MapApplication.map.viewMode = esmap.ESViewMode.MODE_3D;
        esmap.ESTileMap.viewMode = esmap.ESViewMode.MODE_3D;
        this.currentView = "3d";
        Toast('已切换到3D视角');
      }
    },

    // 初始化地图
    initializeMap() {
      MapApplication.initMap();
    },

    // 加载车位数据
    async loadParkingData() {
      try {
        const response = await listParking({});
        const realParkingList = response.rows;
        this.parkingData = realParkingList.map((item) => ({
          id: item.id,
          plateName: item.spaceNumber,
          floorNum: 1,
          status: String(item.parkingStatus),
          plateNumber: item.carNumber || "",
        }));

        const result = MapApplication.updateParkingLots(this.parkingData);
        this.totalSpots = result.total;
        this.updateStatistics(result.floorData);
      } catch (error) {
        console.error("加载停车数据失败:", error);
      }
    },

    // 定时刷新
    startDataPolling() {
      this.loadParkingData();
      this.pollingInterval = setInterval(() => this.loadParkingData(), 5000);
    },
    stopDataPolling() {
      if (this.pollingInterval) clearInterval(this.pollingInterval);
    },

    // 事件监听
    setupEventListeners() {
      window.addEventListener("parking-spot-click", this.handleSpotClick);
    },

    // 车位点击事件
    handleSpotClick(event) {
      const mapId = event.detail.ID;
      const dbId = MapApplication.mapIdToDbId(mapId);
      const spot = this.parkingData.find((p) => p.id === dbId);

      if (spot) {
        this.selectedSpot = {
          id: spot.id,
          plateName: spot.plateName,
          plateNumber: spot.plateNumber,
          status: this.statusNames[parseInt(spot.status)],
        };
      } else {
        this.selectedSpot = {
          id: "",
          plateName: "",
          plateNumber: "",
          status: "",
        };
      }
    },

    // 反向导航
    handleReverseNavi() {
      console.log("执行反向导航");
      MapApplication.reverseNavi();
    },

    // 统计更新
    updateStatistics(floorData) {
      let totalAvailable = 0;
      const stats = [];
      floorData.forEach((data, floorNum) => {
        const available = data.idlist[2].length;
        totalAvailable += available;
        stats.push({ floor: floorNum, available });
      });
      this.availableSpots = totalAvailable;
      this.floorStats = stats.sort((a, b) => a.floor - b.floor);
    },

    // 滚动字幕
    initMarquee() {
      const marquee = this.$refs.marquee;
      if (!marquee) return;
      const animate = () => {
        const parentWidth = marquee.parentElement.offsetWidth;
        const textWidth = marquee.offsetWidth;
        let position = parentWidth;
        const step = () => {
          position--;
          if (position < -textWidth) position = parentWidth;
          marquee.style.left = position + "px";
          requestAnimationFrame(step);
        };
        requestAnimationFrame(step);
      };
      animate();
    },
  },
};
</script>

<style scoped>
/* 容器基础样式 */
.parking-container {
  width: 100%;
  height: 100%;

  position: relative;
  overflow: hidden;
}

/* 地图容器 */
.map-container {
  width: 100%;
  height: 100%;
  position: relative;
}
#map-container {
  width: 100%;
  height: 100%;
}

/* 2D/3D文字切换按钮样式 */
.view-text-switcher {
  display: flex;
  align-items: center;
  color: rgb(8, 145, 243);
  font-size: 18px;
}

.view-text-switcher span {
  padding: 0 4px;
  cursor: pointer;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.view-text-switcher span.active {
  opacity: 1;
  font-weight: bold;
  /* color: rgb(40, 55, 75); */
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.8);
}

.view-text-switcher .divider {
  opacity: 0.5;
  cursor: default;
}

.view-text-switcher span:hover:not(.active) {
  opacity: 0.9;
}

/* 导航栏样式 */
.van-nav-bar {
  background: #f6f8f9;
}

.van-nav-bar__title,
.van-nav-bar__text,
.van-nav-bar .van-icon {
  color: #fff !important;
}

/* 停车位状态显示 */
.parking {
  width: 160px;
  height: 46px;
  top:46px;
  line-height: 46px;
  /* left: 220px; */
  bottom: 20px;
  border: 1px solid #083344;
  border-radius: 4px;
  color: white;
  background-color: rgba(71, 92, 105, 0.8);
  font-size: 14px;
  padding-left: 10px;
  z-index: 30;
  position: fixed;
}

/* 反向导航按钮 */
.reverse-nav {
  width: 80px;
  height: 46px;
  line-height: 46px;
  right: 10px;
  bottom: 80px;
  border: 1px solid #083344;
  border-radius: 4px;
  color: white;
  background-color: rgba(71, 92, 105, 0.8);
  font-size: 14px;
  text-align: center;
  position: fixed;
  cursor: pointer;
  transition: background-color 0.3s ease;
  z-index: 10;
}
.reverse-nav:hover {
  background-color: rgba(100, 130, 150, 0.9);
}

/* 图例 */
.codition {
  width: 120px;
  left: 20px;
  height: 60px;
  bottom: 140px;
  position: fixed;
  z-index: 10;
}
.codition ul {
  width: 90%;
  padding: 6px;
  list-style-type: none;
  margin: 0;
  background-color: rgba(71, 92, 105, 0.8);
  border-radius: 4px;
  border: 1px solid #083344;
}
.codition ul li {
  display: list-item;
  height: 30px;
  background-color: white;
  line-height: 36px;
  text-align: center;
  margin-bottom: 4px;
  border-radius: 2px;
}
.codition ul li:last-child {
  margin-bottom: 0;
}
.codition ul li span {
  display: inline-block;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  margin-right: 10px;
  vertical-align: middle;
}
.codition-first {
  background-color: #ff0000;
}
.codition-second {
  background-color: #00ff00;
}
.codition-third {
  background-color: #7eacca;
}

/* 统计信息横幅 */
.i-test-tip {
  width: 750px;
  height: 46px;
/*   left: 100%; */
  transform: translateX(-50%);
  bottom: 20px;
  border-radius: 4px;
  overflow: hidden;
  background-color: rgba(71, 92, 105, 0.8);
  text-align: center;
  position: fixed;
  z-index: 10;
}
.test-tip {
  position: absolute;
  top: 0;
  left: 100%;
  color: white;
  font-size: 15px;
  line-height: 46px;
  white-space: nowrap;
}
.test-tip span {
  color: #0f0;
}

/* 通用fixed类 */
.fix {
  position: fixed;
}
</style>