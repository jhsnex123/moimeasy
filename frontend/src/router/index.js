import { createRouter, createWebHistory } from "vue-router";
import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";
import Signup from "@/views/Signup.vue";
import MoeimSelect from "@/views/MoeimSelect.vue";
import CreateMoeim from "@/views/CreateMoeim.vue";
import EnterMoeim from "@/views/EnterMoeim.vue";
import Schedule from "@/views/schedule/Schedule.vue";

const routes = [
  { path: "/", name: "Home", component: Home },
  { path: "/login", name: "Login", component: Login },
  { path: "/signup", name: "Signup", component: Signup },
  { path: "/moeim-select", name: "MoeimSelect", component: MoeimSelect },
  { path: "/create-moeim", name: "CreateMoeim", component: CreateMoeim },
  { path: "/enter-moeim", name: "EnterMoeim", component: EnterMoeim },
  { path: "/schedule", name: "Schedule", component: Schedule },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
