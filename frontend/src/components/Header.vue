<template>
  <div class="header-wrapper">
    <div class="header-box">
      <div class="header-box1">
        <img src="@/assets/arrowIcon.svg?url" alt="Arrow Icon" height="12px" />
        <p>DashBoard</p>
      </div>
      <div class="header-box2">
        <div class="search-bar-box">
          <input class="search-bar" />
          <img
            src="@/assets/searchIcon.svg?url"
            alt="search Icon"
            height="15px"
            class="search-icon"
          />
        </div>
        <div class="notifications-box">
          <img
            src="@/assets/notifications.svg?url"
            alt="notifications Icon"
            height="28px"
          />
        </div>
        <div class="profile-box">
          <img src="@/assets/Avatar.svg?url" alt="Avatar Icon" height="32px" />
          <p>유저407번</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
.header-wrapper {
  width: 100%;
  height: 78px;
  background-color: #ffffff;
  border-bottom: 1px solid #e9e9e9;
}

.header-box {
  height: 100%;
  display: flex;
  padding: 0 13px;
  align-items: center;
  justify-content: space-between;
  gap: 79px;
}

.header-box1 {
  width: 309px;
  padding: 8px;
  display: flex;
  gap: 10px;

  p {
    width: 111px;
    font-size: 18px;
  }
}

.header-box2 {
  width: 763px;
  height: 40px;
  margin-right: 40px;
  display: flex;
  gap: 24px;
  align-items: center;
  justify-content: space-between;
}

.search-bar-box {
  width: 530px;
  border-radius: 10%;
  position: relative;
}

.search-bar {
  background: #f7f9fb;
  border-radius: 8px;
  flex-shrink: 0;
  height: 40px;
  width: 100%;
  border: none;
  padding: 10px 40px;
  box-sizing: border-box;
}
.search-icon {
  position: absolute;
  left: 15px;
  top: 12px;
  cursor: pointer;
}

.notifications-box {
  cursor: pointer;
}

.profile-box {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
