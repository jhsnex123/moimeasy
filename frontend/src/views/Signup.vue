<template>
  <div class="signup-container">
    <h2 class="title">회원가입</h2>
    <form @submit.prevent="submitSignup">
      <div class="form-group">
        <label>Username</label>
        <input
          type="text"
          v-model="formData.username"
          placeholder="Your username"
          required
        />
      </div>
      <div class="form-group">
        <label>Email</label>
        <input
          type="email"
          v-model="formData.email"
          placeholder="Your email"
          required
        />
      </div>
      <div class="form-group">
        <label>Password</label>
        <input
          type="password"
          v-model="formData.password"
          placeholder="Password"
          required
        />
      </div>
      <div class="form-group">
        <label>Confirm Password</label>
        <input
          type="password"
          v-model="formData.confirmPassword"
          placeholder="Confirm password"
          required
        />
      </div>
      <div class="form-group-checkbox">
        <input
          type="checkbox"
          id="terms"
          v-model="formData.acceptTerms"
          required
        />
        <label for="terms">I accept the terms and privacy policy</label>
      </div>
      <button type="submit" class="btn-primary">회원가입</button>
    </form>
    <div class="alternative">
      <p>Or Register with</p>
      <div class="social-buttons">
        <button class="social-btn">Facebook</button>
        <button class="social-btn">Google</button>
        <button class="social-btn">Apple</button>
      </div>
    </div>
    <p class="footer-text">
      Already have an account? <a href="#" @click.prevent="goToLogin">Log in</a>
    </p>
  </div>
</template>

<script>
export default {
  name: "Signup",
  data() {
    return {
      formData: {
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
        acceptTerms: false,
      },
    };
  },
  methods: {
    submitSignup() {
      if (this.formData.password !== this.formData.confirmPassword) {
        alert("Passwords do not match!");
        return;
      }
      if (!this.formData.acceptTerms) {
        alert("You must accept the terms and privacy policy!");
        return;
      }
      console.log("회원가입 성공:", this.formData.username);
      // 회원가입 완료 후 로그인 페이지로 이동
      this.$router.push("/login");
    },
    goToLogin() {
      // 로그인 페이지로 이동
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
/* 스타일 추가 */
.signup-container {
  min-width: 400px;
  width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.title {
  font-weight: bold;
  text-align: center;
  color: #414651;
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

input[type="text"],
input[type="email"],
input[type="password"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
}
.form-group-checkbox {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.form-group-checkbox input {
  margin-right: 10px;
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
.alternative {
  color: #414651;
  text-align: center;
  margin-top: 20px;
}

.social-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.social-btn {
  background-color: #f1f1f1;
  border: none;
  padding: 8px 12px;
  border-radius: 5px;
  font-size: 14px;
  cursor: pointer;
}

.footer-text {
  color: #414651;
  text-align: center;
  margin-top: 20px;
}

.footer-text a {
  color: #7f56d9;
  text-decoration: none;
  font-weight: bold;
}

.footer-text a:hover {
  text-decoration: underline;
}
</style>
