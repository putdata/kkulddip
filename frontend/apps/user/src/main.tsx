import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { queryClient } from 'common';
import GlobalErrorBoundary from '@/components/boundary/GlobalErrorBoundary';
import './index.css';
import App from './App';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <GlobalErrorBoundary>
        <App />
        <ReactQueryDevtools initialIsOpen={false} />
      </GlobalErrorBoundary>
    </QueryClientProvider>
  </StrictMode>,
);
