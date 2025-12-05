javascript
import router from "./router"
import http from "./utils/request";
import store from "./store";

// 全局路由守卫
router.beforeEach((to, from, next) => {
    // 记录白名单信息
    const whiteList = ["/login", "/register"]
    const token = localStorage.getItem("token")

    // 如果访问的是白名单页面，直接放行
    if (whiteList.includes(to.path)) {
        next();
        return;
    }

    // 检查token是否存在
    if (!token) {
        alert('请先登录');
        next("/login");
        return;
    }

    // 已登录，请求系统信息
    http.get("/system/info").then(systemData => {
        console.log("系统信息", systemData);
        
        //直接使用业务数据（拦截器已处理）
        if (systemData && systemData.adminInfo) {
            store.commit("setSystemInfo", systemData.adminInfo);
            store.commit("Title", systemData.adminInfo.title);
        } else {
            console.warn("系统信息数据格式异常", systemData);
        }
        
        next();
    }).catch(err => {
        console.error("获取系统信息失败:", err);
        alert('登录已过期，请重新登录');
        localStorage.removeItem("token");
        next("/login");
    });
});

    /**方案一:判断是登录注册直接访问,其他页面需要登录*/
    /*if (to.path === '/login' || to.path === '/register') {
        // 访问登录/注册页，直接放行
        next()
    } else {
        // 访问其他页面，检查登录状态
        if (!adminInfoStr) {
            // 未登录，跳转到登录页
            alert('请先登录')
            next('/login')
        } else {
            // 已登录，正常放行
            next()
        }
    }*/
