// 定义MapApplication类，用于管理地图应用
class MapApplication {
  // 构造函数
  constructor() {
    // 地图实例，初始为null
    this.map = null;
    // 停车数据，初始为null
    this.parkData = null;
    // 建筑物数据，初始为null
    this.building = null;
    // 当前选中的车位ID，初始为null
    this.selectedSpotId = null;
    // 点击计数器，用于导航起点/终点设置
    this.clickCount = 0;
    // 上一个点击坐标
    this.lastCoord = null;
    // 点击坐标
    this.mapCoord = null;
    // 起点坐标信息
    this.startPoint = null;
    // 终点坐标信息
    this.endPoint = null;
    // 定义地图车位ID与数据库车位ID的映射关系
    this.spotIdMapping = {
      // 格式：'地图ID': '数据库ID'
      '70090': 1, // 示例映射
      '70091': 2,
      '70092': 3,
      '70093': 4,
      '70094': 5,
      '70095': 6,
    };
    // 创建反向映射（数据库ID到地图ID）
    this.reverseMapping = {};
    // 遍历spotIdMapping生成反向映射
    for (const [mapId, dbId] of Object.entries(this.spotIdMapping)) {
      this.reverseMapping[dbId] = mapId;
    }
  }

  // 初始化地图方法
  initMap() {
    // debugger
    //地图的ID
    const mapID = 10005;
    // 检查ESMap库是否加载
    if (typeof esmap === 'undefined') {
      console.error('ESMap library not loaded');
      return;
    }
    try {
      console.log('Initializing ESMap with ID:', mapID);

      const container = document.getElementById('map-container');
      if (!container) {
        console.error('Map container not found!');
        return;
      }
      // 配置地图选项
      const mapOptions = {
        container: container, // 地图容器
        mapDataSrc: '/static/esmap/data/', // 地图数据源路径
        mapAudioSrc: '/static/esmap/lib/', // 地图数据源路径
        token: 'escope', // 认证token
        mode: esmap.MapMode.Building, // 地图模式为建筑物模式
        focusAlphaMode: true, // 启用聚焦透明度模式
        focusAnimateMode: true, // 启用聚焦动画模式
        focusAlpha: 0.4, // 聚焦透明度值
        focusFloor: 1, // 初始聚焦楼层
        showCompass: true, // 显示指南针
      };
      // 兼容不同版本的ESMap库
      if (typeof esmap.MapMode !== 'undefined' && esmap.MapMode.Building) {
        mapOptions.mode = esmap.MapMode.Building;
      } else if (typeof esmap.ESBuildingType !== 'undefined') {
        mapOptions.buildingType = esmap.ESBuildingType.BUILDING;
      }
      console.log('Map options:', mapOptions);
      // 创建地图实例
      this.map = new esmap.ESMap(mapOptions);
      // 添加地图事件监听器
      this.addMapEventListeners();
      // 打开指定ID的地图
      console.log('Opening map with ID:', mapID);
      this.map.openMapById(mapID);
    } catch (error) {
      // 捕获并记录初始化错误
      console.error('Error initializing map:', error);
      console.error('Error stack:', error.stack);
    }

  }

  // 添加地图事件监听器方法
  addMapEventListeners() {
    // debugger
    // 检查地图是否初始化
    if (!this.map) return;
    // 监听地图加载完成事件
    this.map.on("loadComplete", () => {
      console.log("地图加载完成");
      // 获取建筑物数据
      this.building = this.map.getBuilding();
      // 添加楼层控制器
      this.addFloorControl();
      // 创建导航实例
      this.createNavi();
      // 创建并派发自定义事件，通知Vue组件地图加载完成
      const loadCompleteEvent = new CustomEvent('load-map-complete');
      window.dispatchEvent(loadCompleteEvent);
    });
    // 监听地图节点点击事件
    this.map.on("mapClickNode", (event) => {
      console.log("地图节点点击事件:", event);
      // 过滤不需要处理的节点类型
      if (event.nodeType === esmap.ESNodeType.NONE ||
        event.nodeType === esmap.ESNodeType.FLOOR ||
        event.name === "楼梯") {
        console.log("过滤掉的节点类型:", event.nodeType, "节点名称:", event.name);
        return;
      }
      // 如果是车位点击，派发自定义事件
      if (event.nodeType === esmap.ESNodeType.MODEL) {
        const clickEvent = new CustomEvent('parking-spot-click', {
          detail: {
            ID: event.id || event.ID, // 传递车位ID
            floorNum: event.floor || event.FloorNum
          }
        });
        window.dispatchEvent(clickEvent);
      }

      let curfnum = null;
      let h = 1;
      // 根据节点类型提取楼层和高度信息
      if (event.nodeType === esmap.ESNodeType.MODEL) { // 车位/模型
        console.log("点击的是模型节点");
        curfnum = event.floor || event.FloorNum || event.groupID;
        this.mapCoord = event.hitCoord || event.position;
      } else if (event.nodeType === 5) { // 房间
        console.log("点击的是房间节点");
        curfnum = event.floorNum;
        h = event.roomHigh;
        this.mapCoord = event.hitCoord;
      } else {
        // 其他节点类型也尝试处理
        console.log("点击的是其他节点类型:", event.nodeType);
        curfnum = event.floor || event.FloorNum || event.groupID || 1;
        this.mapCoord = event.hitCoord || event.position;
      }

      // 调用showNavi，传递楼层、坐标、高度
      if (curfnum !== null && this.mapCoord) {
        console.log("调用导航功能，楼层:", curfnum, "坐标:", this.mapCoord, "高度:", h);
        this.showNavi(curfnum, this.mapCoord, h);
      } else {
        console.log("无法获取楼层或坐标信息");
      }
    });
  }

  // 更新停车位状态方法
  updateParkingLots(parkingData) {
    //parkingData 的数据格式是这样的
    // {id: 70149, floorNum: 1, status: '1', plateNumber: '闽D7A689'}
    /*
   id: 车位id
   floorNum: 楼层
   status: 1-有车 0 没有车
   plateNumber: 车牌号 , 有车需要显示 的车牌
   */
    console.log('开始更新停车位状态...');
    console.log('地图对象:', this.map ? '已初始化' : '未初始化');
    console.log('建筑物对象:', this.building ? '已初始化' : '未初始化');
    console.log(parkingData);
    /**this.building = this.map.getBuilding();**/
    //检查地图和建筑物是否初始化
    if (!this.map || !this.building) {
      console.error('地图或建筑物未初始化');
      return { total: 0, floorData: new Map() };
    }
    console.log('更新车位数据:', parkingData);
    // 保存停车数据
    this.parkData = parkingData;
    // 定义颜色数组和状态名称
    const colors = ["#7eacca", "#ff0000", "#00ff00"]; //固定、占用、空闲
    const statusNames = ["无车", "有车", "固定车位"];
    // 创建按楼层管理车位的数据结构
    const floorData = new Map();
    let totalSpots = 0; // 总车位计数
    // 检查数据是否为空

    if (!parkingData || parkingData.length === 0) {
      console.warn('没有车位数据');
      return { total: 0, floorData: floorData };
    }
    // 处理每个停车位数据
    for (let i = 0; i < parkingData.length; i++) {
      const spot = parkingData[i];
      // 获取楼层号，默认为1
      const floorNum = spot.floorNum || 1;
      // 获取或初始化该楼层的数据
      let floorSpots = floorData.get(floorNum);
      if (!floorSpots) {
        // 初始化三种状态的车位ID数组
        floorSpots = { idlist: [[], [], []] };
        floorData.set(floorNum, floorSpots);
      }
      totalSpots++; // 增加总车位计数
      // 处理车位状态
      let statusIndex = 0; // 默认状态为固定车位
      try {
        // 转换状态为数字
        statusIndex = parseInt(spot.status);
        // 验证状态值有效性
        if (isNaN(statusIndex) || statusIndex < 0 || statusIndex > 2) {
          console.warn(`车位 ${spot.id} 的状态无效: ${spot.status}，设置为默
认值 0`);
          statusIndex = 0;
        }
      } catch (e) {
        console.error(`解析车位状态出错:`, e);
      }
      // 将数据库ID转换为地图ID
      const mapId = this.dbIdToMapId(spot.id);
      // 将车位ID添加到对应状态的数组
      floorSpots.idlist[statusIndex].push(mapId);
      console.log(`处理车位: 数据库ID=${spot.id}, 地图ID=${mapId}, 楼层
=${floorNum}, 状态=${statusNames[statusIndex]}`);
    }
    // 更新每个楼层的车位颜色
    floorData.forEach((floorSpots, floorNum) => {

      // 遍历三种状态
      for (let statusIndex = 0; statusIndex < colors.length;
        statusIndex++) {
        const ids = floorSpots.idlist[statusIndex];
        if (ids && ids.length > 0) {
          console.log(`设置楼层${floorNum}的${statusNames[statusIndex]}颜色,
数量:`, ids.length);
          try {
            // debugger
            // 批量修改车位颜色
            this.map.changeModelColor({
              id: ids, // 车位ID数组
              fnum: floorNum, // 楼层号
              color: colors[statusIndex] // 颜色值
            });
          } catch (error) {
            console.error(`更新车位颜色出错:`, error);
          }
        }
      }
    });
    // 如果存在选中的车位，重新高亮显示
    if (this.selectedSpotId) {
      const selectedSpot = parkingData.find(spot => spot.id ===
        this.selectedSpotId);
      if (selectedSpot) {
        this.highlightParkingSpot(this.selectedSpotId,
          selectedSpot.floorNum || 1);
      }
    }
    // 返回处理结果
    return {
      total: totalSpots, // 总车位数量
      floorData: floorData // 按楼层分类的车位数据
    };
  }

  // 高亮显示特定车位方法
  highlightParkingSpot(spotId, floorNum, color = "#ffff00") {
    // 检查地图是否初始化
    if (!this.map) {
      console.error('地图未初始化，无法高亮显示车位');
      return;
    }
    // 将数据库ID转换为地图ID（如果需要）

    const mapId = this.reverseMapping[spotId] ?
      this.reverseMapping[spotId] : spotId;
    console.log(`高亮显示车位: 传入ID=${spotId}, 使用地图ID=${mapId}, 楼层
=${floorNum}, 颜色=${color}`);
    try {
      // 调用地图API修改车位颜色
      this.map.changeModelColor({
        id: mapId, // 车位ID
        fnum: floorNum || 1, // 楼层号，默认为1
        color: color // 高亮颜色
      });
    } catch (error) {
      console.error(`高亮显示车位出错: ${error.message}`);
    }
  }

  // 重置车位颜色方法
  resetSpotColor(spotId) {
    // 检查地图和停车数据是否初始化
    if (!this.map || !this.parkData) {
      console.error('地图或停车数据未初始化，无法重置车位颜色');
      return;
    }
    // 将地图ID转换为数据库ID（如果需要）
    const dbId = this.mapIdToDbId(spotId);
    // 查找对应的车位数据
    const spot = this.parkData.find(s => s.id === dbId);
    if (!spot) {
      console.warn(`未找到车位数据: 传入ID=${spotId}, 查找数据库ID=${dbId}`);
      return;
    }
    // 定义颜色数组
    const colors = ["#7eacca", "#ff0000", "#00ff00"]; // 固定、占用、空闲
    let statusIndex = 0; // 默认状态为固定车位
    try {
      // 转换状态为数字
      statusIndex = parseInt(spot.status);
      // 验证状态值有效性
      if (isNaN(statusIndex) || statusIndex < 0 || statusIndex > 2) {
        console.warn(`车位 ${spotId} 的状态无效: ${spot.status}，使用默认值
0`);
        statusIndex = 0;

      }
    } catch (e) {
      console.error(`解析车位状态出错:`, e);
    }
    // 将数据库ID转换为地图ID
    const mapId = this.dbIdToMapId(spot.id);
    console.log(`重置车位颜色: 数据库ID=${spot.id}, 地图ID=${mapId}, 楼层
=${spot.floorNum}, 状态=${statusIndex}`);
    try {
      // 调用地图API修改车位颜色
      this.map.changeModelColor({
        id: mapId, // 车位ID
        fnum: spot.floorNum || 1, // 楼层号，默认为1
        color: colors[statusIndex] // 根据状态设置颜色
      });
    } catch (error) {
      console.error(`重置车位颜色出错: ${error.message}`);
    }
  }

  // 添加楼层控制方法
  addFloorControl() {
    try {
      // 检查地图是否初始化
      if (!this.map) {
        console.error('Map instance not initialized');
        return;
      }
      // 配置楼层控制控件选项
      const ctlOpt = new esmap.ESControlOptions({
        position: esmap.ESControlPositon.RIGHT_TOP, // 控件位置在右上角
      });
      // 监听地图加载完成事件
      this.map.on('loadComplete', () => {
        // 创建楼层控件
        const floorControl = new esmap.ESScrollFloorsControl(this.map,
          ctlOpt);
        console.log('Floor control added successfully');
      });
    } catch (error) {
      console.error('Error adding floor control:', error);
      console.error('Error stack:', error.stack);
    }
  }

  // 创建导航对象
  createNavi() {
    console.log("导航对象初始化成功")
    // 确保建筑物数据已加载
    if (!this.building) {
      console.warn("建筑物数据未加载！");
      return;
    }

    // 初始化导航对象（不再检查naviData，确保总是创建导航对象）
    if (!this.navi) {
      // 初始化导航对象
      this.navi = new esmap.ESNavigation({
        map: this.map,
        // 图标路径
        locationMarkerUrl: '/static/esmap/Case/park/image/pointer.png',
        // 图标大小
        locationMarkerSize: 156,
        // 导航速度
        speed: 2,
        // 导航是否会自动旋转场景
        followAngle: true,
        // 导航是否会自动跟随视角
        followPosition: true,
        // 导航标记与路线之间的距离
        followGap: 3,
        // 导航开始时视角倾斜度
        tiltAngle: 30,
        // 是否播放语音
        audioPlay: true,
        // 导航模式1人行 2车行
        mode: 1,
        offsetHeight: 2,
        // 距离终点多少米后结束导航
        maxEnd: 1,
        linestyle: {
          color: '#24bdf5ff',
          lineType: esmap.ESLineType.ESARROW,
          linewidth: 6,
          fixedWidth: true,
          offsetHeight: 0.3,
          smooth: true,
          seeThrough: false,
          noAnimate: true
        },
        scaleAnimate: true,
        showMultifloor: false
      });

      console.log("导航对象创建成功");
    }

    // 添加事件监听
    this.navi.on('success', () => {
      this.navi.followAngle = true;
      this.navi.followPosition = true;
      this.navi.scaleAnimate = true;
      // 语音播报
      this.map.ESAudioTool.playSoundByName('startNaving');
      this.navi.simulate();
    });

    this.navi.on('naviTip', (message) => {
      const descriptionElement = document.getElementById('description');
      if (descriptionElement) {
        descriptionElement.innerHTML = message;
      }
    });

    this.navi.on('complete', () => {
      console.log("停止");
      const descriptionElement = document.getElementById('description');
      if (descriptionElement) {
        descriptionElement.innerText = "到达终点";
      }
    });
  }

  // 导航逻辑
  showNavi(fnum, coord, h) {
    // 确保导航对象存在
    if (!this.navi) {
      console.warn("导航对象未初始化");
      return;
    }

    if (!coord) {
      console.warn("坐标信息为空");
      return;
    }

    console.log("导航调用次数:", this.clickCount);

    // 第三次点击清空路线并设置新起点
    if (this.clickCount === 2) {
      console.log("清空导航路线并设置新起点");
      this.navi.clearAll();
      this.clickCount = 0;
      this.lastCoord = null;
      this.startPoint = null;
      this.endPoint = null;

      // 清空后继续执行设置起点的逻辑
      console.log("设置新起点:", coord.x, coord.y, fnum, h);
      this.lastCoord = coord;
      this.startPoint = { x: coord.x, y: coord.y, fnum: fnum, height: h || 1 };
      // 设置导航起点
      this.navi.setStartPoint({
        x: coord.x,
        y: coord.y,
        fnum: fnum,
        height: h || 1,
        url: '/static/esmap/Case/park/image/start.png',
        // 起点图标大小
        size: 64,
        showLevel: 23
      });

      this.clickCount++;
      return;
    }

    // 第一次点击设置起点
    if (this.clickCount === 0) {
      console.log("设置起点:", coord.x, coord.y, fnum, h);
      this.lastCoord = coord;
      this.startPoint = { x: coord.x, y: coord.y, fnum: fnum, height: h || 1 };
      // 设置导航起点
      this.navi.setStartPoint({
        x: coord.x,
        y: coord.y,
        fnum: fnum,
        height: h || 1,
        url: '/static/esmap/Case/park/image/start.png',
        // 起点图标大小
        size: 64,
        showLevel: 23
      });
    }
    // 第二次点击设置终点
    else if (this.clickCount === 1) {
      console.log("设置终点:", coord.x, coord.y, fnum, h);
      if (this.lastCoord && this.lastCoord.x === coord.x && this.lastCoord.y === coord.y) {
        alert("起点和终点不能相同！");
        return;
      }

      this.endPoint = { x: coord.x, y: coord.y, fnum: fnum, height: h || 1 };

      // 设置导航终点
      this.navi.setEndPoint({
        x: coord.x,
        y: coord.y,
        fnum: fnum,
        height: h || 1,
        url: '/static/esmap/Case/park/image/end.png',
        size: 64,
        showLevel: 23
      });

      // 获取路径规划结果
      this.navi.getRouteResult({
        // 是否绘制路径
        drawRoute: true,
        // 是否显示导航标记
        showMarker: true,
        // 是否播放语音提示
        audioPlay: true
      });
    }

    this.clickCount++;
  }

  // 反向导航功能
  reverseNavi() {
    // 确保导航对象存在且已设置起点和终点
    if (!this.navi) {
      console.warn("导航对象未初始化");
      return;
    }

    if (!this.startPoint || !this.endPoint) {
      console.warn("未设置起点或终点，无法进行反向导航");
      return;
    }

    console.log("执行反向导航");

    // 清除当前导航路线
    this.navi.clearAll();

    // 交换起点和终点
    const tempStart = this.startPoint;
    this.startPoint = this.endPoint;
    this.endPoint = tempStart;

    // 设置新的起点（原终点）
    this.navi.setStartPoint({
      x: this.startPoint.x,
      y: this.startPoint.y,
      fnum: this.startPoint.fnum,
      height: this.startPoint.height || 1,
      url: '/static/esmap/Case/park/image/start.png',
      size: 64,
      showLevel: 23
    });

    // 设置新的终点（原起点）
    this.navi.setEndPoint({
      x: this.endPoint.x,
      y: this.endPoint.y,
      fnum: this.endPoint.fnum,
      height: this.endPoint.height || 1,
      url: '/static/esmap/Case/park/image/end.png',
      size: 64,
      showLevel: 23
    });

    // 获取路径规划结果
    this.navi.getRouteResult({
      // 是否绘制路径
      drawRoute: true,
      // 是否显示导航标记
      showMarker: true,
      // 是否播放语音提示
      audioPlay: true
    });
  }
  

  // 销毁地图方法
  destroyMap() {
    try {
      if (this.map) {
        // 根据API版本调用不同的销毁方法
        if (typeof this.map.destroy === 'function') {
          this.map.destroy();
        } else if (typeof this.map.dumpScene === 'function') {
          // 兼容旧版本API
          this.map.dumpScene();
        }
        this.map = null; // 清空地图引用
        console.log('Map destroyed');
      }
    } catch (error) {
      console.error('Error destroying map:', error);
    }
  }

  // 将地图ID转换为数据库ID方法
  mapIdToDbId(mapId) {
    // 从映射表中查找，找不到则返回原值
    return this.spotIdMapping[mapId] || mapId;
  }

  // 将数据库ID转换为地图ID方法
  dbIdToMapId(dbId) {
    // 从反向映射表中查找，找不到则返回原值
    return this.reverseMapping[dbId] || dbId;
  }
}

// 导出MapApplication的单例实例

export default new MapApplication();
