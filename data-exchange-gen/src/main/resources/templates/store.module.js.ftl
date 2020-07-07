import ${table.name}_api from '../../api/${table.name}_api'

const state = {
    pageData: {}, // 分页数据
    loading: false, // 加载提示
    loadingAdd: false, //弹框加载提示
}

const mutations = {
    setPageData(state, data) {
        state.pageData = data
    },
    setLoading(state, loading) {
        state.loading = loading
    },
    setLoadingAdd(state, loading) {
        state.loadingAdd = loading
    }
}

const actions = {
    findPage(context, payload) {
        return new Promise((resolve, reject) => {
            context.commit('setLoading', true)
            ${table.name}_api.findPage(payload).then(data => {
                context.commit('setPageData', data)
                context.commit('setLoading', false)
                resolve(data)
            }).catch(err => {
                context.commit('setLoading', false)
                reject(err)
            })
        })
    },
    add(context, payload) {
        return new Promise((resolve, reject) => {
            context.commit('setLoadingAdd', true)
            ${table.name}_api.add(payload).then(data => {
                context.commit('setLoadingAdd', false)
                resolve(data)
            }).catch(err => {
                context.commit('setLoadingAdd', false)
                reject(err)
            })
        })
    },
    get(context, payload) {
        return new Promise((resolve, reject) => {
            context.commit('setLoadingAdd', true)
            ${table.name}_api.get(payload).then(data => {
                context.commit('setLoadingAdd', false)
                resolve(data)
            }).catch(err => {
                context.commit('setLoadingAdd', false)
                reject(err)
            })
        })
    },
    update(context, payload) {
        return new Promise((resolve, reject) => {
            context.commit('setLoadingAdd', true)
            ${table.name}_api.update(payload).then(data => {
                context.commit('setLoadingAdd', false)
                resolve(data)
            }).catch(err => {
                context.commit('setLoadingAdd', false)
                reject(err)
            })
        })
    },
    delete(context, payload) {
        return new Promise((resolve, reject) => {
            ${table.name}_api.delete(payload).then(data => {
                resolve(data)
            }).catch(err => {
                reject(err)
            })
        })
    },
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}