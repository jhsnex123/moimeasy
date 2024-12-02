<template>
  <div class="nav-bar">
    <div class="nav-logo">
      <div class="logo-container">
        <div class="logo-background"></div>
        <!-- vite-svg-loader 사용 -->
        <BlurIcon class="logo-icon" fill="#7f56d9" />
      </div>
      <div class="logo-text-container">
        <div class="logo-text">MoeimEasy</div>
      </div>
    </div>
    <div class="main-menu-container">
      <div class="main-menu-header">
        <div class="main-menu-title">MAIN MENU</div>
      </div>
      <div class="menu-tabs">
        <div class="menu-item-active">
          <div class="menu-item-text-container">
            <img class="icon-dashboard" src="@/assets/outlineIcon.svg?url" />
            <div class="menu-item-text">Dashboard</div>
          </div>
        </div>
        <div class="menu-item">
          <div class="menu-item-text-container">
            <img class="icon-cart" src="@/assets/usersIcon.svg?url" />
            <div class="menu-item-text">회원관리</div>
          </div>
        </div>
        <div class="menu-item">
          <div class="menu-item-text-container">
            <img class="icon-edit" src="@/assets/editIcon.svg?url" />
            <div class="menu-item-text">일정관리</div>
          </div>
        </div>
        <div class="dropdown-menu">
          <div class="dropdown-item">
            <div class="menu-item-text-container">
              <img class="icon-notebook" src="@/assets/notebook.svg?url" />
              <div class="menu-item-text">회비관리</div>
            </div>
          </div>
          <div class="dropdown-item">
            <div class="menu-item-text-container">
              <img class="icon-graph" src="@/assets/graphIcon.svg?url" />
              <div class="menu-item-text">통계</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="bottom-menu-container">
      <div class="more-settings">
        <div class="settings-group">
          <div class="settings-item">
            <img class="icon-users" src="@/assets/usersIcon.svg?url" />
            <div class="settings-text">Users</div>
          </div>
          <div class="settings-item">
            <img class="icon-settings" src="@/assets/settingsIcon.svg?url" />
            <div class="settings-text">Settings</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import BlurIcon from "@/assets/blurIcon.svg";
</script>

<style scoped>
.nav-bar,
.nav-bar * {
  box-sizing: border-box;
}

.nav-bar {
  background: #ffffff;
  width: 264px;
  height: 100vh;
  position: relative;
  box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.nav-logo {
  display: flex;
  flex-direction: row;
  gap: 16px;
  align-items: center;
  justify-content: flex-start;
  position: absolute;
  left: 24px;
  top: 24px;
}

.logo-container {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  position: static;
}

.logo-background {
  background: #ffffff;
  border-radius: 6px;
  border: 0.75px solid #e8e8e8;
  width: 42px;
  height: 42px;
  position: absolute;
  left: 0px;
  top: 0px;
}

.logo-icon {
  width: 24px;
  height: 24px;
  position: absolute;
  left: 9px;
  top: 9px;
  overflow: visible;
  cursor: pointer;
}

.logo-text-container {
  flex-shrink: 0;
  width: 116px;
  height: 24px;
  position: static;
}

.logo-text {
  color: #7f56d9;
  text-align: left;
  font-family: "Poppins-Medium", sans-serif;
  font-size: 20px;
  line-height: 24px;
  font-weight: 500;
  position: absolute;
  left: 58px;
  top: 9px;
}

.main-menu-container {
  display: flex;
  flex-direction: column;
  gap: 14px;
  align-items: flex-start;
  justify-content: flex-start;
  position: absolute;
  left: 24px;
  top: 122px;
}

.main-menu-header {
  flex-shrink: 0;
  width: 240px;
  height: 24px;
  position: relative;
  overflow: hidden;
}

.main-menu-title {
  color: #969696;
  text-align: left;
  font-family: "Poppins-Regular", sans-serif;
  font-size: 12px;
  line-height: 16px;
  letter-spacing: 0.04em;
  font-weight: 400;
  text-transform: uppercase;
  position: absolute;
  left: 0px;
  top: 4px;
}

.menu-tabs {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  position: relative;
}

.menu-item,
.menu-item-active {
  border-radius: 8px;
  padding: 8px 8px 8px 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: center;
  flex-shrink: 0;
  width: 212px;
  height: 53px;
  position: relative;
  cursor: pointer;
}

.menu-item-active {
  background: #ebeef0;
}

.menu-item-text-container {
  display: flex;
  flex-direction: row;
  gap: 11px;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
}

.icon-dashboard,
.icon-cart,
.icon-edit,
.icon-notebook,
.icon-graph,
.icon-users,
.icon-settings {
  display: flex;
  flex-direction: row;
  gap: 8px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  height: auto;
  position: relative;
  overflow: visible;
}

.menu-item-text {
  color: #181616;
  text-align: left;
  font-family: "Poppins-Medium", sans-serif;
  font-size: 16px;
  line-height: 16px;
  font-weight: 500;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.dropdown-menu {
  display: flex;
  flex-direction: column;
  gap: 0px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  position: relative;
}

.dropdown-item {
  border-radius: 8px;
  padding: 19px 8px 19px 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: center;
  flex-shrink: 0;
  width: 212px;
  position: relative;
  cursor: pointer;
}

.bottom-menu-container {
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: flex-start;
  width: 264px;
  height: 189px;
  position: absolute;
  left: 0px;
  bottom: 2px;
}

.more-settings {
  border-top: 1px solid #9d9d9d;
  display: flex;
  flex-direction: column;
  gap: 14px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  position: relative;
}

.settings-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  position: relative;
}

.settings-item {
  border-radius: 8px;
  padding: 8px 8px 8px 24px;
  display: flex;
  gap: 8px;
  align-items: flex-start;
  align-items: center;
  flex-shrink: 0;
  width: 212px;
  height: 53px;
  position: relative;
}

.settings-text {
  color: #181616;
  text-align: left;
  font-family: "Poppins-Medium", sans-serif;
  font-size: 16px;
  line-height: 16px;
  font-weight: 500;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
</style>
