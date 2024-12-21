<template>
  <div class="header-wrapper">
    <div class="header-box">
      <div class="header-box1">
        <div class="card breadcrumb-wrapper">
          <Breadcrumb :home="home" :model="items">
            <template #item="{ item, props }">
              <router-link
                v-if="item.route"
                v-slot="{ href, navigate }"
                :to="item.route"
                custom
              >
                <a :href="href" v-bind="props.action" @click="navigate">
                  <span :class="[item.icon, 'text-color']" />
                  <span class="text-primary font-semibold">{{
                    item.label
                  }}</span>
                </a>
              </router-link>
              <a
                v-else
                :href="item.url"
                :target="item.target"
                v-bind="props.action"
              >
                <span class="text-dark">{{ item.label }}</span>
              </a>
            </template>
          </Breadcrumb>
        </div>
      </div>
      <div class="header-box2">
        <!-- 검색창 -->
        <div class="search-bar-box">
          <input class="search-bar" />
          <img
            src="@/assets/icons/searchIcon.svg?url"
            alt="search Icon"
            height="15px"
            class="search-icon"
          />
        </div>

        <!-- 알림메시지  -->
        <Toast position="bottom-right" />
        <div class="notifications-box" @click="showSecondary">
          <OverlayBadge :value="notificationInfos.length" severity="danger">
            <i class="pi pi-bell" style="font-size: 24px" />
          </OverlayBadge>
        </div>
        <!-- 유저 프로필 영역 -->
        <div class="profile-box">
          <img
            :src="profileImage ? profileImage : defaultAvatar"
            alt="Avatar Icon"
            height="32px"
            @click="toggle"
          />
          <!-- <Avatar
            :image="profileImage ? profileImage : defaultAvatar"
            class="mr-2"
            size="large"
            shape="circle"
            @click="toggle"
          /> -->
          <p>{{ nickName }}</p>
          <!-- 프로필이미지 클릭시 드롭다운 메뉴 -->
          <div class="tiered-menu-box">
            <TieredMenu ref="menu" id="overlay_tmenu" :model="menus" popup />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useRouter } from 'vue-router';
import OverlayBadge from 'primevue/overlaybadge';
import Breadcrumb from 'primevue/breadcrumb';
import Toast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
import TieredMenu from 'primevue/tieredmenu';
import { useAuthStore } from '@/stores/auth'; // Pinia auth 스토어
import { fetchImageUrl } from '@/utils/image-load-utils';
import defaultAvatar from '@/assets/icons/Avatar.svg?url'; // 이미지 가져오기
import axios from 'axios';
const authStore = useAuthStore();
const nickName = ref('');
const profileImage = ref('');
const userData = ref('');
const toast = useToast();
const route = useRoute();
const router = useRouter();
const menu = ref();
const menus = ref([]);
const notificationInfos = ref([]);
// 브레드크럼 홈경로 설정
const home = ref({
  icon: 'pi pi-home',
  route: '/',
});

// 브레드크럼 페이지별 경로 설정
const items = ref([]);

// 라우트 변경 감지 및 업데이트
const updatePathInfo = () => {
  items.value = route.matched.map((item) => ({
    label: item.name,
    route: item.path,
  }));
};

// 초기 경로 설정
updatePathInfo();

// 모임 알림 가져오기
const fetchNotification = async () => {
  console.log('유저데이터', userData.value);
  try {
    const { data } = await axios.get(
      `/api/v1/notifications?userId=${userData.value?.userId}`
    );
    console.log('추가된 알림정보:', data);
    // 알림 내역 저장
    notificationInfos.value = data.map((a, i) => {
      return {
        severity: 'secondary',
        summary: a.header,
        detail: a.body,
        life: 3000,
      };
    });
  } catch (error) {}
};

// 마지막으로 알림을 본 시간 업데이트
const fetchUpdateLastViewedAt = async () => {
  try {
    const response = await axios.patch(
      `/api/v1/notifications/update-viewed-time?userId=${userData.value?.userId}`
    );
    console.log('응답 :', response.data);
  } catch (error) {
    console.error;
  }
};

// 라우트 변경 감지용
watch(
  () => route.matched,
  async () => {
    updatePathInfo();
    if (
      userData.value != '' &&
      userData.value != null &&
      userData.value != undefined
    ) {
      fetchNotification();
    } //알림 정보 가져오기
    console.log('라우트 변경됨:', items.value);
    // 유저 정보가져오기
    const userDataStorage = localStorage.getItem('user');
    userData.value = userDataStorage ? JSON.parse(userDataStorage) : null;

    // 닉네임 업데이트
    nickName.value = userData.value?.nickname || '게스트';

    // 비동기 함수로 파이어베이스의 이미지 경로 불러오기
    if (userData.value?.profileImage) {
      profileImage.value = await fetchImageUrl(userData.value.profileImage);
    }

    menus.value = [
      {
        label: 'Profile',
        icon: 'pi pi-user',
        command: () => {
          router.push(`/user-profile/${userData.value?.userId}`);
        },
      },
      {
        label: 'Edit',
        icon: 'pi pi-user-edit',
        command: () => {
          router.push(`/edit-profile/${userData.value?.userId}`);
        },
      },
      nickName.value != '게스트'
        ? {
            label: 'Logout',
            icon: 'pi pi-sign-out',
            command: () => {
              console.log('로그아웃 실행'); // 디버깅 로그
              authStore.logout(); // Pinia auth 스토어의 logout 호출
            },
          }
        : {
            label: 'Login',
            icon: 'pi pi-sign-in',
            command: () => {
              router.push('/login');
            },
          },
    ];
  },
  { immediate: true } // 컴포넌트가 처음 마운트될 때도 실행
);
//Toast 전달 메시지 설정
const showSecondary = () => {
  notificationInfos.value.map((a) => {
    toast.add(a); // 알림데이터 토스트에 추가
  });
  fetchUpdateLastViewedAt(); // 알림 확인 시각 업데이트
  notificationInfos.value = []; // 알림데이터 초기화
};

const toggle = (event) => {
  menu.value.toggle(event);
};
</script>

<style>
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
  justify-content: space-evenly;
  gap: 12px;
  cursor: pointer;
  border-radius: 20px;
}

.breadcrumb-wrapper {
  display: flex;
  align-items: center;
}
.font-semibold {
  font-size: 14px;
  margin-top: 2px;
}
.text-dark {
  color: red;
}

.p-breadcrumb {
  min-width: 232px;
}
</style>
