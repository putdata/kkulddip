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
    globPatterns: ['**/*.{js,css,html,ico,png,svg,woff2}'],
    runtimeCaching: [
      {
        urlPattern: /\.(?:png|jpg|jpeg|svg|gif|webp)$/,
        handler: 'CacheFirst',
        options: {
          cacheName: 'images-cache',
          expiration: {
            maxEntries: 100,
            maxAgeSeconds: 60 * 60 * 24 * 30, // 30 days
          },
        },
      },
      {
        urlPattern: /^https:\/\/api\.kkulddip\.store\/.*/i,
        handler: 'NetworkFirst',
        options: {
          cacheName: 'api-cache',
          expiration: {
            maxEntries: 50,
            maxAgeSeconds: 60 * 5, // 5 minutes
          },
        },
      },
    ],
  },
  includeAssets: ['vite.svg', 'firebase-messaging-sw.js'],
  manifest: {
    name: '꿀띱 사장님',
    short_name: '꿀띱',
    description: '꿀띱 사장님 전용 관리 시스템',
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
        src: '/vite.svg',
        sizes: 'any',
        type: 'image/svg+xml',
        purpose: 'any',
      },
    ],
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
