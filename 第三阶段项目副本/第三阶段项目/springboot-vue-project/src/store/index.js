import vue from 'vue';
import Vuex from 'vuex';
vue.use(Vuex);
//创建仓库
const store = new Vuex.Store({
    state: {
        //定义共享数据
        title: '体检健康管理系统',
        adminInfo: null,
        
    },
    mutations: {
        //定义修改共享数据的方法
        setTitle(state, newTitle) {
            state.title = newTitle;
        }
    },
    actions: {
        //定义异步修改共享数据的方法
        setTitleAsync({ commit }, newTitle) {
            setTimeout(() => {
                commit('setTitle', newTitle);
            }, 5000);
        }
    },
    getters: {
        //定义获取共享数据的方法
        getTitle(state) {
            return state.title;
        }
    }
});
export default store;

