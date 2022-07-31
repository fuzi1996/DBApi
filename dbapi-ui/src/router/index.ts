import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import { IMenubarList } from '/@/type/store/layout'
import { components } from '/@/router/asyncRouter'

const Components:IObject<() => Promise<typeof import('*.vue')>> = Object.assign({}, components, {
  Layout: (() => import('/@/layout/index.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  Redirect: (() => import('/@/layout/redirect.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  LayoutBlank: (() => import('/@/layout/blank.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  DataSource: (() => import('/@/views/DataSource/index.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  EditDataSource: (() => import('../views/DataSource/editDataSource.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  Api: (() => import('/@/views/ApiComponent/index.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  ApiEdit: (() => import('/@/views/ApiComponent/apiEdit.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  ApiRequest: (() => import('/@/views/ApiComponent/apiRequest.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  Token: (() => import('/@/views/Token/index.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  PasswordChange: (() => import('/@/views/SystemConfig/PasswordChange.vue')) as unknown as () => Promise<typeof import('*.vue')>,
  Firewall: (() => import('/@/views/SystemConfig/Firewall.vue')) as unknown as () => Promise<typeof import('*.vue')>
})

// 静态路由页面
export const allowRouter:Array<IMenubarList> = [
  {
    name: 'Dashboard',
    path: '/',
    component: Components['Layout'],
    redirect: '/dashboard/workplace',
    meta: { title: '仪表盘', icon: 'el-icon-eleme' },
    children: [
      {
        name: 'Workplace',
        path: '/dashboard/workplace',
        component: Components['Workplace'],
        meta: { title: '工作台', icon: 'el-icon-tools' }
      }
    ]
  },
  {
    name: 'RedirectPage',
    path: '/redirect',
    component: Components['Layout'],
    meta: { title: '重定向页面', icon: 'el-icon-eleme', hidden: true },
    children: [
      {
        name: 'Redirect',
        path: '/redirect/:pathMatch(.*)*',
        meta: {
          title: '重定向页面',
          icon: ''
        },
        component: Components.Redirect
      }
    ]
  },
  {
    name: 'Login',
    path: '/login',
    component: Components.Login,
    meta: { title: '登录', icon: 'el-icon-eleme', hidden: true }
  },
  {
    name: 'DataSource',
    path: '/datasource',
    component: Components['Layout'],
    meta: { title: '数据源', icon: 'el-icon-eleme', alwaysShow: true },
    redirect: '/datasource/list',
    children: [
      {
        name: 'List',
        path: '/datasource/list',
        component: Components['DataSource'],
        meta: { title: '数据源管理', icon: 'el-icon-tools' }
      },
      {
        name: 'edit',
        path: '/datasource/edit',
        component: Components['EditDataSource'],
        meta: { title: '数据源编辑', icon: 'el-icon-tools', hidden: true }
      }
    ]
  },
  {
    name: 'Api',
    path: '/api',
    component: Components['Layout'],
    meta: { title: 'API', icon: 'el-icon-eleme', alwaysShow: true },
    redirect: '/api/list',
    children: [
      {
        name: 'ApiList',
        path: '/api/list',
        component: Components['Api'],
        meta: { title: 'API管理', icon: 'el-icon-tools' }
      },
      {
        name: 'ApiEdit',
        path: '/api/edit',
        component: Components['ApiEdit'],
        meta: { title: 'API编辑', icon: 'el-icon-tools', hidden: true }
      },
      {
        name: 'ApiReqeust',
        path: '/api/request',
        component: Components['ApiRequest'],
        meta: { title: 'API请求测试', icon: 'el-icon-tools', hidden: true }
      }
    ]
  },
  {
    name: 'Token',
    path: '/token',
    component: Components['Layout'],
    meta: { title: '权限', icon: 'el-icon-eleme', alwaysShow: true },
    redirect: '/token/list',
    children: [
      {
        name: 'TokenList',
        path: '/token/list',
        component: Components['Token'],
        meta: { title: '权限管理', icon: 'el-icon-tools' }
      }
    ]
  },
  {
    name: 'Setting',
    path: '/setting',
    component: Components['Layout'],
    meta: { title: '系统设置', icon: 'el-icon-eleme', alwaysShow: true },
    children: [
      {
        name: 'ChangePassword',
        path: '/setting/password',
        component: Components['PasswordChange'],
        meta: { title: '修改密码', icon: 'el-icon-tools' }
      },
      {
        name: 'Firewall',
        path: '/setting/firewall',
        component: Components['Firewall'],
        meta: { title: '防火墙', icon: 'el-icon-tools' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(), // createWebHistory
  routes: allowRouter as RouteRecordRaw[]
})

export default router