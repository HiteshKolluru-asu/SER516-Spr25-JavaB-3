// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import MicroserviceA1 from '@/views/MicroserviceA1.vue'
import MicroserviceB1 from '@/views/MicroserviceB1.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/afferent-efferent', component: MicroserviceA1 },
  { path: '/defect-density', component: MicroserviceB1 },
]

export default createRouter({
  history: createWebHistory(),
  routes
})
