import {defineStore} from 'pinia'
import {getInfo, logout} from "../api/user.ts";
import {ElMessage} from "element-plus";
import {useRouter} from 'vue-router'
import {useCartStore} from "./cart.ts";

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        avatar: '',
        userInfo: null
    }),
    actions: {
        setToken(newToken: string) {
            this.token = newToken
            localStorage.setItem('token', newToken)
        },
        async fetchUserInfo() {
            // 这里实现获取用户信息的逻辑
            getInfo().then(res => {
                if (200 == res.code) {
                    if (!res.user.schoolId) {
                        this.clearAuth()
                        ElMessage.error("此用户没有当前端权限，请注册")
                        logout();
                    }
                    this.userInfo = res.user
                    this.avatar = import.meta.env.VITE_API_BASEURL + res.user.avatar
                    // 刷新购物车
                    useCartStore().loadCart();
                } else {
                    this.clearAuth()
                    logout();
                }
            }).catch(err => {
                this.clearAuth()
                logout();
                ElMessage.error(err.message)
            })
        },
        clearAuth() {
            this.token = ''
            this.userInfo = null
            localStorage.removeItem('token')
        }
    }
})
