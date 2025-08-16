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
    'Kkulddip_og_1200x630.png',
    'browserconfig.xml',
    'firebase-messaging-sw.js',
  ],
  manifest: {
    id: '/',
    name: '꿀띱',
    short_name: '꿀띱',
    description:
      '꿀띱에서 남은 맛있는 음식을 저렴하게 픽업하세요. 환경 보호와 절약을 동시에!',
    theme_color: '#FF6B35',
    background_color: '#ffffff',
    display: 'standalone',
    orientation: 'portrait',
    scope: '/',
    start_url: '/',
    categories: ['food', 'lifestyle', 'shopping'],
    lang: 'ko',
    icons: [
      {
        src: '/favicon.ico',
        sizes: '16x16 24x24 32x32 48x48 64x64',
        type: 'image/x-icon',
      },
      {
        src: '/favicon.ico',
        sizes: '192x192',
        type: 'image/x-icon',
        purpose: 'any',
      },
      {
        src: '/favicon.ico',
        sizes: '512x512',
        type: 'image/x-icon',
        purpose: 'maskable',
      },
    ],
    shortcuts: [
      {
        name: '홈',
        short_name: '홈',
        description: '맛있는 음식을 찾아보세요',
        url: '/',
        icons: [{ src: '/favicon.ico', sizes: '32x32' }],
      },
      {
        name: '주문내역',
        short_name: '주문',
        description: '주문 내역을 확인하세요',
        url: '/order',
        icons: [{ src: '/favicon.ico', sizes: '32x32' }],
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
