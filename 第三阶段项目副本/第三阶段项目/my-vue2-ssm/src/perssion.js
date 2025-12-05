import router from "./router"
import http from "./utils/request";
import store from "./store";
// 全局路由守卫
router.beforeEach((to, from, next) => {





    // 从 localStorage 获取用户信息
    /*const adminInfoStr = localStorage.getItem("adminInfo")*/

    //记录白名单信息
    const whiteList = ["/login", "/register"]
    const token = localStorage.getItem("token")

    // 如果访问的是白名单页面，直接放行
    if (whiteList.includes(to.path)) {
        next();
        return; // 必须return，防止后续逻辑干扰
    }
    if (token == null) {
        //未登录
        alert('请先登录')
        next("/login")
    } else {
        http.get("/system/info").then(res => {
            console.log("-----get system info-----")
            console.log("系统信息", res);
            store.commit("setSystemInfo", res);
            store.commit("Title", );
            next()
            
        })
    }

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
})