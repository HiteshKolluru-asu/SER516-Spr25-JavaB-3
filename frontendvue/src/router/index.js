// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import MicroserviceA1 from '@/views/MicroserviceA1.vue'
import MicroserviceB1 from '@/views/MicroserviceB1.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/microservice-a1', component: MicroserviceA1 },
  { path: '/microservice-b1', component: MicroserviceB1 },
]

export default createRouter({
  history: createWebHistory(),
  routes
})
