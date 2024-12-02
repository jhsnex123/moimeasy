<template>
  <div class="login-container">
    <h2 class="title">로그인</h2>
    <form @submit.prevent="submitLogin">
      <div class="form-group">
        <label>Email</label>
        <input
          type="email"
          v-model="formData.email"
          placeholder="Enter your email"
          required
        />
      </div>
      <div class="form-group">
        <label>Password</label>
        <input
          type="password"
          v-model="formData.password"
          placeholder="Enter your password"
          required
        />
      </div>
      <div class="form-options">
        <div class="remember-me">
          <input type="checkbox" id="remember" v-model="formData.rememberMe" />
          <label for="remember">이메일 저장</label>
        </div>
        <a href="#" class="forgot-password" @click.prevent="forgotPassword"
          >비밀번호 찾기</a
        >
      </div>
      <button type="submit" class="btn-primary">로그인</button>
    </form>
    <button class="btn-secondary" @click="goToSignup">회원가입</button>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      formData: {
        email: "",
        password: "",
        rememberMe: false,
      },
    };
  },
  methods: {
    submitLogin() {
      console.log("로그인 성공:", this.formData.email);
      // 로그인 후 처리 로직 추가 (예: 홈으로 이동)
      this.$router.push("/");
    },
    goToSignup() {
      // 회원가입 페이지로 이동
      this.$router.push("/signup");
    },
    forgotPassword() {
      // 비밀번호 찾기 로직 추가 (현재는 콘솔 출력)
      console.log("비밀번호 찾기");
      alert("비밀번호 찾기 기능은 아직 준비 중입니다.");
    },
  },
};
</script>

<style scoped>
/* 로그인 스타일 */
.login-container {
  width: 400px;
  min-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  color: #414651;
  font-weight: bold;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  color: #414651;
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forgot-password {
  color: #7f56d9;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password:hover {
  text-decoration: underline;
}

.btn-primary {
  display: block;
  width: 100%;
  background-color: #7f56d9;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 10px;
}

.btn-primary:hover {
  background-color: #6a48b0;
}

.btn-secondary {
  display: block;
  width: 100%;
  background-color: white;
  color: #414651;

  padding: 10px;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 10px;
}

.btn-secondary:hover {
  background-color: #f7f5fc;
}
</style>
