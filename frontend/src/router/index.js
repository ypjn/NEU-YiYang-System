import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: { role: 'admin' },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'users', name: 'UserManage', component: () => import('../views/admin/UserManage.vue') },
      { path: 'elderly', name: 'ElderlyManage', component: () => import('../views/admin/ElderlyManage.vue') },
      { path: 'nursing-level', name: 'NursingLevel', component: () => import('../views/admin/NursingLevel.vue') },
      { path: 'nursing-project', name: 'NursingProject', component: () => import('../views/admin/NursingProject.vue') },
      { path: 'nursing-record', name: 'NursingRecord', component: () => import('../views/admin/NursingRecord.vue') },
      { path: 'beds', name: 'BedManage', component: () => import('../views/admin/BedManage.vue') },
      { path: 'out-manage', name: 'OutManage', component: () => import('../views/admin/OutManage.vue') },
      { path: 'checkout-manage', name: 'CheckoutManage', component: () => import('../views/admin/CheckoutManage.vue') },
      { path: 'employees', name: 'EmployeeManage', component: () => import('../views/admin/EmployeeManage.vue') },
      { path: 'services', name: 'ServiceManage', component: () => import('../views/admin/ServiceManage.vue') },
      { path: 'foods', name: 'FoodManage', component: () => import('../views/admin/FoodManage.vue') },
      { path: 'diet-calendar', name: 'DietCalendar', component: () => import('../views/admin/DietCalendar.vue') },
      { path: 'diet-preference', name: 'DietPreference', component: () => import('../views/admin/DietPreference.vue') },
      { path: 'logs', name: 'OperationLog', component: () => import('../views/admin/OperationLog.vue') },
    ]
  },
  {
    path: '/nurse',
    component: () => import('../views/nurse/Layout.vue'),
    meta: { role: 'nurse' },
    children: [
      { path: '', redirect: '/nurse/elderly' },
      { path: 'elderly', name: 'NurseElderly', component: () => import('../views/nurse/ElderlyInfo.vue') },
      { path: 'records', name: 'NurseRecords', component: () => import('../views/nurse/CareRecords.vue') },
      { path: 'all-records', name: 'NurseAllRecords', component: () => import('../views/admin/NursingRecord.vue') },
      { path: 'services', name: 'NurseServices', component: () => import('../views/admin/ServiceManage.vue') },
      { path: 'diet', name: 'NurseDiet', component: () => import('../views/admin/DietPreference.vue') },
      { path: 'messages', name: 'NurseMessages', component: () => import('../views/nurse/Messages.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const user = sessionStorage.getItem('user')
  if (to.path !== '/login' && to.path !== '/register' && !user) {
    return '/login'
  }
  if (user && to.meta.role) {
    const u = JSON.parse(user)
    if (to.meta.role !== u.role) {
      return u.role === 'admin' ? '/admin' : '/nurse'
    }
  }
})

export default router
