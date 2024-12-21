import { fileURLToPath, URL } from 'node:url';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';
import svgLoader from 'vite-svg-loader';

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools(), svgLoader()],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },

  // 빌드 출력 디렉토리 설정
  build: {
    outDir: '../src/main/resources/static', // Spring Boot의 정적 리소스 폴더로 설정
    emptyOutDir: true, // 기존 빌드 결과 삭제
  },

  // 개발 서버 설정
  server: {
    host: '0.0.0.0', // 모든 IP에서 접근 가능
    port: 3000, // 개발 서버 포트 설정
    proxy: {
      '/api/v1': {
        target: 'http://localhost:8088', // Spring Boot 서버 주소
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1/, '/api/v1'), // 경로 재작성
      },
      '/ws-connect': {
        // WebSocket 프록시 경로 설정
        target: 'http://localhost:8088', // Spring Boot WebSocket 서버 주소와 포트
        ws: true, // WebSocket 지원 설정
        changeOrigin: true, // 동일한 오리진 정책 회피
        secure: false,
      },
    },
  },

  // 글로벌 객체 정의
  define: {
    global: {},
  },
});
