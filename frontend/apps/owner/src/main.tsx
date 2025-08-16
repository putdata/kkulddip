import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { queryClient } from 'common';
import { Toaster } from '@/components/ui/sonner';
import './index.css';
import App from './App';
import GlobalErrorBoundary from '@/components/boundary/GlobalErrorBoundary';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <GlobalErrorBoundary>
        <App />
        <Toaster position="bottom-right" />
        <ReactQueryDevtools initialIsOpen={false} />
      </GlobalErrorBoundary>
    </QueryClientProvider>
  </StrictMode>,
);
