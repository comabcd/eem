import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
  {
    path: '/login',
    component: () => import('../views/login/index.vue'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('../views/register/index.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard'
  },
  // 系统管理模块
  {
    path: '/system',
    component: Layout,
    meta: { roles: ['admin'] },
    children: [
      {
        path: 'dashboard',
        component: () => import('../views/system/index.vue'),
        name: 'SystemDashboard',
        meta: { title: '系统首页', icon: 'House' }
      },
      {
        path: 'user',
        component: () => import('../views/system/user/index.vue'),
        name: 'User',
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'post',
        component: () => import('../views/system/post/index.vue'),
        name: 'Post',
        meta: { title: '岗位管理', icon: 'Briefcase' }
      },
      {
        path: 'dept',
        component: () => import('../views/system/dept/index.vue'),
        name: 'Department',
        meta: { title: '部门管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'dict',
        component: () => import('../views/system/dict/index.vue'),
        name: 'Dict',
        meta: { title: '基础数据管理', icon: 'Files' }
      }
    ]
  },
  // 部门管理模块
  {
    path: '/dept',
    component: Layout,
    meta: { roles: ['dept_admin'] },
    children: [
      {
        path: 'dashboard',
        component: () => import('../views/dept/index.vue'),
        name: 'DeptDashboard',
        meta: { title: '部门首页', icon: 'House' }
      },
      {
        path: 'employee',
        component: () => import('../views/dept/employee/index.vue'),
        name: 'DeptEmployee',
        meta: { title: '员工管理', icon: 'UserFilled'}
      },
      {
        path: 'leave',
        component: () => import('../views/dept/leave/index.vue'),
        name: 'Leave',
        meta: { title: '请假管理', icon: 'Timer'}
      },
      {
        path: 'attendance',
        component: () => import('../views/dept/attendance/index.vue'),
        name: 'Attendance',
        meta: { title: '考勤管理', icon: 'Calendar'}
      }
    ]
  },
  // HR模块
  {
    path: '/hr',
    component: Layout,
    meta: { roles: ['hr_admin'] },
    children: [
      {
        path: 'dashboard',
        component: () => import('../views/hr/index.vue'),
        name: 'HrDashboard',
        meta: { title: 'HR首页', icon: 'House' }
      },
      {
        path: 'onboard',
        component: () => import('../views/hr/onboard/index.vue'),
        name: 'Onboard',
        meta: { title: '入职办理', icon: 'DocumentAdd' }
      },
      {
        path: 'resign',
        component: () => import('../views/hr/resign/index.vue'),
        name: 'Resign',
        meta: { title: '离职办理', icon: 'DocumentAdd' }
      },
      {
        path: 'employee',
        name: 'HrEmployee',
        component: () => import('../views/hr/employee/index.vue'),
        meta: { title: '员工管理', icon: 'UserFilled'}
      }
    ]
  },
  // 员工模块
  {
    path: '/employee',
    component: Layout,
    meta: { roles: ['employee'] },
    children: [
      {
        path: 'dashboard',
        component: () => import('../views/employee/index.vue'),
        name: 'EmployeeDashboard',
        meta: { title: '员工首页', icon: 'House' }
      },
      {
        path: 'leave-apply',
        component: () => import('../views/employee/leave/index.vue'),
        name: 'LeaveApply',
        meta: { title: '请假申请', icon: 'DocumentAdd' }
      },
      {
        path: 'attendance-record',
        component: () => import('../views/employee/attendance/index.vue'),
        name: 'AttendanceRecord',
        meta: { title: '考勤打卡', icon: 'Stamp' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  
  // 处理登录和注册页面
  if (to.path === '/login' || to.path === '/register') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }

  // 处理其他页面
  if (!token) {
    next('/login')
    return
  }

  // 处理首页重定向
  if (to.path === '/dashboard') {
    switch (userRole) {
      case 'admin':
        next('/system/dashboard')
        break
      case 'employee':
        next('/employee/dashboard')
        break
      case 'dept_admin':
        next('/dept/dashboard')
        break
      case 'hr_admin':
        next('/hr/dashboard')
        break
      default:
        next('/login')
    }
    return
  }

  // 检查路由权限
  const requiresAuth = to.matched.some(record => record.meta.roles)
  if (requiresAuth) {
    const hasPermission = to.matched.some(record => 
      !record.meta.roles || record.meta.roles.includes(userRole)
    )
    if (hasPermission) {
      next()
    } else {
      next('/dashboard')
    }
  } else {
    next()
  }
})

export default router 