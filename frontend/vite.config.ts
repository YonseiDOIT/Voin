import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { VitePWA } from 'vite-plugin-pwa'
import mkcert from 'vite-plugin-mkcert'
import svgr from 'vite-plugin-svgr';

// https://vite.dev/config/
export default defineConfig({
  server: {
    host: true,
    port: 5173,
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/signup/profile-image': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  },
  appType: 'spa', 
  plugins: [
    react(),
    tailwindcss(),
    mkcert(),
    svgr(),
    VitePWA({
      registerType: 'autoUpdate', // 서비스 워커 업데이트 방식 설정
      devOptions: {
        enabled: true // 개발 환경에서도 PWA 활성화
      },
      manifest: {
        name: 'Voin',
        short_name: 'Voin',
        display: "standalone",
        theme_color: '#ffffff',
        lang: 'ko-KR',
        icons: [
          {
            src: 'icons/192.png', // public 폴더 기준 경로
            sizes: '192x192',
            type: 'image/png'
          },
          {
            src: 'icons/512.png',
            sizes: '512x512',
            type: 'image/png'
          },
          {
            src: 'icons/512.png',
            sizes: '512x512',
            type: 'image/png',
            purpose: 'any maskable' // 아이콘 용도 (maskable 아이콘)
          }
        ]
      }
    }),
  ],
})
