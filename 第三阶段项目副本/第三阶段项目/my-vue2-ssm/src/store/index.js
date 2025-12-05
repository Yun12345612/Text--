import vue from 'vue';
import Vuex from 'vuex';
vue.use(Vuex);
//创建仓库
const store = new Vuex.Store({
    //定义应用中的所有共享数据状态,作用：存储所有数据的地方 特点：不能直接修改，必须(mutations)来变更。
    state: {
        userInfo: null,  // 存储用户信息
        token: '',      // 存储认证token
        //定义共享数据
        title: '体检健康管理系统',
        adminInfo: null,

    },
    //修改数据的方法,作用：唯一可以修改state中数据的地方，特点：必须是同步函数
    mutations: {
        //修改系统标题
        setTitle(state, newTitle) {
            state.title = newTitle;
        },
        // 添加用户信息相关的 mutations信息
        SET_USER_INFO(state, userInfo) {
            state.userInfo = userInfo;
        },
        // 设置token
        SET_TOKEN(state, token) {
            state.token = token;
        },
        // 清除用户信息和token
        CLEAR_USER_INFO(state) {
            state.userInfo = null;
            state.token = '';
        }
    },
    //处理业务逻辑的方法,作用：处理复杂的业务逻辑，特点：可以包含异步操作，可以调用mutations来修改state中的数据
    actions: {
        //定义异步修改共享数据的方法
        setTitleAsync({ commit }, newTitle) {
            setTimeout(() => {
                commit('setTitle', newTitle);
            }, 5000);
        },
        // 添加用户信息相关和token
        setUserInfo({ commit }, { userInfo, token }) {
            commit('SET_USER_INFO', userInfo);
            if (token) {
                commit('SET_TOKEN', token);
            }
        },
        // 清除用户信息和token,退出登录
        clearUserInfo({ commit }) {
            commit('CLEAR_USER_INFO');
        },
        //从loacalStorage恢复用户登录状态
        initUserInfo({ commit }) {
            const storedUserInfo = localStorage.getItem('userInfo');
            const storedToken = localStorage.getItem('token');

            if (storedUserInfo) {
                try {
                    const userInfo = JSON.parse(storedUserInfo);
                    commit('SET_USER_INFO', userInfo);//恢复用户信息
                } catch (error) {
                    console.error('解析用户信息失败:', error);
                }
            }

            if (storedToken) {
                commit('SET_TOKEN', storedToken);//恢复token
            }
        }
    },
    //告诉你我如何获取state中的数据,作用：以特定的方式获取和计算state中的数据，特点：可以看作是store的计算属性
    getters: {
        //获取系统标题
        getTitle(state) {
            return state.title;
        },
        // 获取用户名称
        userName: (state) => {
            return state.userInfo?.userName || state.userInfo?.userAccount || '用户';
        },
        // 判断用户是否已登录
        isLoggedIn: (state) => {
            return !!state.userInfo && !!state.token;
        },
        //获取完整的用户信息
        userInfo: (state) => {
            return state.userInfo;
        },  // 新增需要的 getters
        getUserPhone: (state) => {
            return state.userInfo?.userPhone || '';
        },
        getUserName: (state) => {
            return state.userInfo?.userName || '';
        },
        getUserId: (state) => {
            return state.userInfo?.userId || null;
        },
        getUserAccount: (state) => {
            return state.userInfo?.userAccount || '';
        },
        getUserCard: (state) => {
            return state.userInfo?.userCard || '';
        },
        getUserAge: (state) => {
            return state.userInfo?.userAge || 0;
        }
    }
});

export default store;

