import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    loading: false
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
      if (user) {
        localStorage.setItem('user', JSON.stringify(user))
      } else {
        localStorage.removeItem('user')
      }
    },
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    SET_LOADING(state, loading) {
      state.loading = loading
    },
    LOGOUT(state) {
      state.user = null
      state.token = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    }
  },
  actions: {
    async login({ commit }, { username, password }) {
      try {
        commit('SET_LOADING', true)
        const response = await axios.post('/user/login', {
          username,
          password
        })
        
        if (response.data.code === 200) {
          const { user, token } = response.data.data
          commit('SET_USER', user)
          commit('SET_TOKEN', token)
          return { success: true, user }
        } else {
          return { success: false, message: response.data.message }
        }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || '登录失败' 
        }
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async logout({ commit }) {
      try {
        await axios.post('/user/logout')
      } catch (error) {
        console.error('登出请求失败:', error)
      } finally {
        commit('LOGOUT')
      }
    },
    
    async register({ commit }, userData) {
      try {
        commit('SET_LOADING', true)
        const response = await axios.post('/user/register', userData)
        
        if (response.data.code === 200) {
          return { success: true, message: response.data.message }
        } else {
          return { success: false, message: response.data.message }
        }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || '注册失败' 
        }
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async updateUser({ commit, state }, userData) {
      try {
        const response = await axios.put('/user/update', userData)
        
        if (response.data.code === 200) {
          // 如果更新的是当前用户，更新store中的用户信息
          if (state.user && state.user.id === userData.id) {
            const updatedUser = { ...state.user, ...userData }
            commit('SET_USER', updatedUser)
          }
          return { success: true, message: response.data.message }
        } else {
          return { success: false, message: response.data.message }
        }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || '更新失败' 
        }
      }
    }
  },
  getters: {
    isLoggedIn: state => !!state.user,
    isAdmin: state => state.user && state.user.userType === 2,
    isReader: state => state.user && state.user.userType === 1,
    currentUser: state => state.user
  }
})