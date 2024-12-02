import "./assets/main.css";

import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import Header from "./components/Header.vue";
import SideBar from "./components/SideBar.vue";

const app = createApp(App);
app.component("Header", Header);
app.component("SideBar", SideBar);
app.use(router).mount("#app");
