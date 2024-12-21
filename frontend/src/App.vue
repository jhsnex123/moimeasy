<template>
  <div class="app-layout">
    <!-- 로딩 스피너 -->
    <LoadingOverlay />
    <!-- 사이드바 -->
    <SideBar
      class="sidebar"
      :class="{ expanded: uiStore.visible }"
      v-if="userData"
    />
    <!-- 스피드다이얼 영역-->
    <SideSpeedDial v-if="userData" />
    <!-- 메인 컨텐츠 -->
    <div class="contents-wrapper">
      <Header class="header" />
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { useUIStore } from '@/stores/uiStore';
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
const route = useRoute();
const userData = ref('');
const uiStore = useUIStore();

watch(
  () => route.matched,
  () => {
    const userDataStorage = localStorage.getItem('user');
    userData.value = userDataStorage ? JSON.parse(userDataStorage) : null;
  },
  { immediate: true } // 컴포넌트가 처음 마운트될 때도 실행
);
</script>

<style>
.app-layout {
  display: flex;
}
.sidebar {
  flex: 0; /* 사이드바영역 전체비율 설정 -> 디폴트 : 접힌 상태 */
  background-color: #333;
  color: white;
  transition: all 0.3s ease-in-out;
}

.sidebar.expanded {
  flex: 0.58; /* 펼쳐진 상태 */
}

@media (min-width: 1490px) {
  /* md 브레이크포인트: 1490px 이상 */
  .sidebar.expanded {
    flex: 0.52; /* 펼쳐진 상태 */
  }
}
@media (min-width: 1600px) {
  /* md 브레이크포인트: 1600px 이상 */
  .sidebar.expanded {
    flex: 0.42; /* 펼쳐진 상태 */
  }
}

.contents-wrapper {
  flex: 3;
  background-color: #f8f9fa;
  overflow: auto;
}

.hidden {
  display: none; /* 애니메이션 종료 후 숨기기 */
}
</style>
