import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev: `npm run dev` serves on :5173 and proxies /api to the Spring Boot app on :8076.
// Build: output lands in src/main/resources/static so the jar serves it.
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  },
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8076',
    },
  },
});
