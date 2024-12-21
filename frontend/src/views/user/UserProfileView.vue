<template>
  <!-- 사용자 프로필 컨테이너 -->
  <div class="profile-container">
    <Toast />
    <ConfirmDialog></ConfirmDialog>
    <form v-if="user">
      <!-- 프로필 이미지 업로드 -->
      <div class="form-group">
        <label for="userProfile">프로필이미지</label>
        <div class="profile-box">
          <!-- 이미지 아바타 컴포넌트 -->
          <Avatar
            :icon="src ? undefined : 'pi pi-user'"
            :image="src || undefined"
            class="mr-2"
            size="xlarge"
            shape="circle"
          />
          <!-- 파일 업로드 버튼 -->
          <FileUpload
            v-if="user.userName == loginUserName"
            mode="basic"
            @select="onFileSelect"
            customUpload
            auto
            severity="secondary"
            class="p-button-outlined"
          />
        </div>
      </div>

      <!-- 사용자 정보 입력 필드 -->
      <div class="form-group">
        <label for="username">유저명</label>
        <input id="username" v-model="user.userName" type="text" readonly />
      </div>

      <div class="form-group">
        <label for="nickname">닉네임</label>
        <input id="nickname" v-model="user.nickname" type="text" readonly />
      </div>

      <div class="form-group">
        <label for="email">이메일</label>
        <input id="email" v-model="user.email" type="email" readonly />
      </div>

      <div class="form-group">
        <label for="phone">전화번호</label>
        <input id="phone" v-model="user.phone" type="text" readonly />
      </div>

      <div class="form-group">
        <label for="createAt">가입 날짜</label>
        <input id="createAt" v-model="user.createAt" type="text" readonly />
      </div>
    </form>

    <!-- 사용자 정보가 없는 경우 표시 -->
    <div v-else>사용자 정보를 불러오는 중입니다...</div>
  </div>
</template>
<script>
import axios from 'axios';
import { useConfirm } from 'primevue/useconfirm';
import { useToast } from 'primevue/usetoast'; // Toast 사용
import { ref, uploadBytes } from 'firebase/storage';
import { firebaseStorage } from '@/firebase/firebaseConfig';
import { fetchImageUrl } from '@/utils/image-load-utils';

export default {
  setup() {
    const confirm = useConfirm(); // useConfirm 선언
    const toast = useToast(); // Toast 인스턴스
    return { confirm, toast };
  },
  data() {
    return {
      user: null, // 사용자 데이터
      loginUser: null, // 현재 로그인 유저 데이터
      loginUserName: null, // 현재 로그인 유저 네임
      src: null, // 이미지 소스
      profileImage: {},
    };
  },
  mounted() {
    const userId = this.$route.params.userId; // URL에서 userId 가져오기
    this.loadUserData(userId); // 사용자 데이터 불러오기
  },
  methods: {
    // 사용자 데이터를 API로 불러오는 메서드
    async loadUserData(userId) {
      this.loginUser = JSON.parse(localStorage.getItem('user'));
      this.loginUserName = JSON.parse(localStorage.getItem('user')).name;
      try {
        const response = await axios.get(`/api/v1/users/${userId}`);
        this.user = response.data; // 응답 데이터를 user에 저장
        this.src = await fetchImageUrl(response.data.profileImage); // 프로필 이미지 저장
      } catch (error) {
        console.error('사용자 정보를 불러오는데 실패했습니다:', error);
      }
    },

    // 파일 업로드 시 실행되는 메서드
    onFileSelect(event) {
      const file = event.files[0];
      const reader = new FileReader();

      // 파일을 읽고 이미지 소스로 설정
      reader.onload = async (e) => {
        this.src = e.target.result;
      };
      reader.readAsDataURL(file);
      this.profileSaveConfirm(file); // 확인창 메서드 호출
    },
    // 확인창 메서드 (PrimeVue의 useConfirm 사용)
    profileSaveConfirm(file) {
      this.confirm.require({
        message: '새 프로필 이미지를 등록하시겠어요?',
        header: '프로필 등록',
        icon: 'pi pi-id-card',
        rejectProps: {
          label: 'Cancel',
          severity: 'secondary',
          outlined: true,
        },
        acceptProps: {
          label: 'Save',
        },
        accept: () => {
          // 프로필 저장 api추가
          this.handleFileUpload(file); // 1. 파이어베이스 스토리지 파일업로드 진행
        },
        reject: () => {},
      });
    },
    // 파이어베이스 파일업로드
    async handleFileUpload(file) {
      if (!file) return;
      this.currentFile = file.name;
      // Firebase 스토리지 경로 설정
      const storageRef = ref(
        firebaseStorage,
        `/moeimeasy/profile/${file.name}`
      );
      // 파일 업로드
      try {
        let uploadedFile = await uploadBytes(storageRef, file);
        let uploadedFileUrl = uploadedFile.metadata.fullPath;
        this.fetchUpdateProfileImage(uploadedFileUrl); //2.DB에 파이어베이스에 저장된 파일경로를 저장
      } catch (error) {
        console.error('파일 업로드 실패', error);
      }
    },
    // DB에 profileImage 저장
    async fetchUpdateProfileImage(uploadedFileUrl) {
      this.profileImage = {
        profileImage: uploadedFileUrl,
      };
      try {
        const response = await axios.put(
          `/api/v1/users/update/${this.user.userId}`,
          this.profileImage
        );
        this.src = await fetchImageUrl(uploadedFileUrl); // 3. 파이어베이스 스토리지에서 이미지 로드 가능한 변환된 경로 가져오기
        const userInStorage = JSON.parse(localStorage.getItem('user'));
        // console.log(
        //   '로컬스토리지 profileImage: ' + userInStorage?.profileImage
        // ); 확인용 로그
        // 저장 완료 시 토스트메시지 띄우기
        userInStorage.profileImage = this.profileImage.profileImage;
        localStorage.setItem('user', JSON.stringify(userInStorage)); // 4. 스토리지 업데이트
        this.toast.add({
          severity: 'success',
          summary: '등록 완료',
          detail: '프로필 이미지가 등록되었습니다.',
          life: 3000,
        });
      } catch (error) {
        console.error('저장실패', error);
        this.toast.add({
          severity: 'danger',
          summary: '등록 실패',
          detail: '프로필 이미지 등록에 실패했습니다.',
          life: 3000,
        });
      }
    },
  },
};
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 10px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #414651; /* 폰트 색상 */
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #414651; /* 폰트 색상 */
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  color: #414651; /* 폰트 색상 */
}

input[readonly] {
  background-color: #f9f9f9;
}

.p-avatar {
  width: 100px;
  height: 100px;
}
.profile-box {
  display: flex;
  flex-direction: column;
}
</style>
