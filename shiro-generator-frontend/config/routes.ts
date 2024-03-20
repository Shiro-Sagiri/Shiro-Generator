export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {path: '/user/login', component: './User/Login'},
      {path: '/user/register', component: './User/Register'},
    ],
  },
  {path: '/', icon: 'home', component: './', name: '主页'},
  {
    path: '/admin',
    icon: 'crown',
    name: '管理页',
    access: 'canAdmin',
    routes: [
      {path: '/admin', redirect: '/admin/user'},
      {icon: 'table', path: '/admin/user', component: './Admin/User', name: '用户管理'},
      {icon: 'tool', path: 'admin/generator', component: './Admin/Generator', name: '生成器管理'},
    ],
  },
  {
    path: '/generator/add',
    icon: 'plus',
    component: './Generator/Add',
    name: '创建生成器',
  },
  {
    path: '/generator/update',
    component: './Generator/Add',
    hideInMenu: true
  },
  {
    path: '/generator/detail/:id',
    component: './Generator/Detail',
    hideInMenu: true
  },
  {path: '*', layout: false, component: './404'},
];
