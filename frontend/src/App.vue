<template>
  <div class="app-layout">
    <SideBar class="sidebar" />
    <Header class="header" />
    <router-view />
  </div>
</template>

<script>
import SideBar from "./components/SideBar.vue";

export default {
  name: "App",
};
</script>
