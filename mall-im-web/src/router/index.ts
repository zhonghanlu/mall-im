import {createRouter, createWebHistory} from 'vue-router'
import {useAuthStore} from '@/stores/auth'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/login/LoginView.vue'),
            meta: {
                requiresAuth: false,
                hideNavbar: true
            }
        },
        {
            path: '/',
            name: 'home',
            component: () => import('@/views/home/HomeView.vue'),
            meta: {requiresAuth: true}
        },
        {
            path: '/user-center',
            name: 'userCenter',
            component: () => import('@/views/userCenter/UserCenter.vue'),
            meta: {requiresAuth: true},
            children: [
                {path: 'order', component: () => import('@/views/userCenter/UserOrder.vue')},
            ]
        },
        {
            path: '/detail/:id',
            name: 'detail',
            component: () => import('@/views/home/ProductDetail.vue'),
            meta: {requiresAuth: true},
            props: true
        },
        {
            path: '/community',
            name: 'community',
            component: () => import('@/views/home/Community.vue'),
            meta: {requiresAuth: true},
        }
        ,
        {
            path: '/school',
            name: 'school',
            component: () => import('@/views/home/School.vue'),
            meta: {requiresAuth: true},
        },
        {
            path: '/search',
            name: 'search',
            component: () => import('@/views/search/SearchView.vue'),
            meta: {requiresAuth: true},
        },
        {
            path: '/cart',
            name: 'cart',
            component: () => import('@/views/userCenter/UserCart.vue'),
            meta: {requiresAuth: true},
        },
        {
            path: '/order',
            name: 'order',
            component: () => import('@/views/order/OrderView.vue'),
            meta: {requiresAuth: true},
        }
    ]
})

router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore();

    console.log('[路由守卫] 当前路由:', to.path)
    console.log('[认证状态] token存在:', !!authStore.token)
    console.log('[路由元信息] 需要认证:', to.meta.requiresAuth)

    if (to.meta.requiresAuth) {
        if (authStore.token) {
            try {
                await authStore.fetchUserInfo();
                next();
            } catch (error) {
                console.error('[认证失败]', error);
                next('/login?redirect=' + encodeURIComponent(to.fullPath));
            }
        } else {
            console.log('[访问拒绝] 重定向到登录页');
            next('/login?redirect=' + encodeURIComponent(to.fullPath));
        }
    } else {
        if (to.path === '/login' && authStore.token) {
            console.log('[已登录] 重定向到首页');
            next(from.path === '/' ? '/' : (from.query.redirect || '/'));
        } else {
            next();
        }
    }
});

export default router