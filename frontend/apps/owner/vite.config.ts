import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import tsconfigPaths from 'vite-tsconfig-paths';
import { VitePWA, type VitePWAOptions } from 'vite-plugin-pwa';
import path from 'path';
import fs from 'fs';

const pwaConfig: Partial<VitePWAOptions> = {
  registerType: 'autoUpdate',
  workbox: {
    globPatterns: ['**/*.{js,css,html,ico,png,svg,woff2,xml}'],
    runtimeCaching: [
      {
        urlPattern: /\.(?:png|jpg|jpeg|svg|gif|webp|ico)$/,
        handler: 'CacheFirst',
        options: {
          cacheName: 'images-cache',
          expiration: {
            maxEntries: 150,
            maxAgeSeconds: 60 * 60 * 24 * 90, // 90 days for icons
          },
        },
      },
      {
        urlPattern: /^https:\/\/api\.kkulddip\.store\/.*/i,
        handler: 'NetworkFirst',
        options: {
          cacheName: 'api-cache',
          expiration: {
            maxEntries: 100,
            maxAgeSeconds: 60 * 10, // 10 minutes
          },
        },
      },
    ],
  },
  includeAssets: [
    'favicon.ico',
    'favicon-16x16.png',
    'favicon-32x32.png',
    'favicon-96x96.png',
    'favicon-128.png',
    'favicon-196x196.png',
    'apple-touch-icon-*.png',
    'mstile-*.png',
    'maskable-icon-512x512.png',
    'Kkulddip_og_1200x630.png',
    'apple-splash-*.png',
    'browserconfig.xml',
    'firebase-messaging-sw.js',
  ],
  manifest: {
    id: '/',
    name: '꿀띱 사장님',
    short_name: '꿀띱',
    description: '꿀띱 사장님 전용 관리 시스템 - 매장 운영을 쉽고 효율적으로',
    theme_color: '#2563eb',
    background_color: '#ffffff',
    display: 'standalone',
    orientation: 'portrait',
    scope: '/',
    start_url: '/',
    categories: ['business', 'food', 'productivity'],
    lang: 'ko',
    icons: [
      {
        src: '/favicon-96x96.png',
        sizes: '96x96',
        type: 'image/png',
      },
      {
        src: '/favicon-128.png',
        sizes: '128x128',
        type: 'image/png',
      },
      {
        src: '/apple-touch-icon-144x144.png',
        sizes: '144x144',
        type: 'image/png',
      },
      {
        src: '/favicon-196x196.png',
        sizes: '196x196',
        type: 'image/png',
        purpose: 'any',
      },
      {
        src: '/maskable-icon-512x512.png',
        sizes: '512x512',
        type: 'image/png',
        purpose: 'maskable',
      },
      {
        src: '/mstile-310x310.png',
        sizes: '310x310',
        type: 'image/png',
      },
    ],
    shortcuts: [
      {
        name: '대시보드',
        short_name: '홈',
        description: '매장 운영 현황을 한눈에 확인하세요',
        url: '/',
        icons: [{ src: '/favicon-96x96.png', sizes: '96x96' }],
      },
      {
        name: '알림',
        short_name: '알림',
        description: '새로운 알림을 확인하세요',
        url: '/notifications',
        icons: [{ src: '/favicon-96x96.png', sizes: '96x96' }],
      },
      {
        name: '설정',
        short_name: '설정',
        description: '앱 설정 및 알림 관리',
        url: '/settings',
        icons: [{ src: '/favicon-96x96.png', sizes: '96x96' }],
      },
    ],
    display_override: ['window-controls-overlay', 'standalone'],
    edge_side_panel: {
      preferred_width: 400,
    },
  },
};

export default defineConfig(() => {
  const generateServiceWorker = () => {
    const templatePath = path.resolve(
      __dirname,
      'src/firebase/firebase-messaging-sw.template.js',
    );
    const outputPath = path.resolve(
      __dirname,
      'public/firebase-messaging-sw.js',
    );

    try {
      const template = fs.readFileSync(templatePath, 'utf-8');

      const swContent = template;

      fs.writeFileSync(outputPath, swContent);
      console.log('✅ Firebase service worker generated');
    } catch {
      console.warn('⚠️ Could not generate Firebase service worker:');
    }
  };

  generateServiceWorker();

  return {
    plugins: [react(), tailwindcss(), tsconfigPaths(), VitePWA(pwaConfig)],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
      },
    },
    envDir: '../../',
  };
});
