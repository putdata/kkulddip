import reactLogo from '../assets/react.svg';
import viteLogo from '/vite.svg';
import { useErrorBoundary } from 'react-error-boundary';
import { useCounter, ApiError } from 'common';
import { useRegisterMutation } from '@/services/userService';

export default function Main() {
  const { count, increment, decrement } = useCounter();
  const { showBoundary } = useErrorBoundary();
  const registerMutation = useRegisterMutation();

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-100 p-6">
      <div className="mb-6 flex space-x-6">
        <a href="https://vite.dev" target="_blank" rel="noreferrer">
          <img src={viteLogo} className="h-16 w-16" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank" rel="noreferrer">
          <img src={reactLogo} className="h-16 w-16" alt="React logo" />
        </a>
      </div>

      <h1 className="mb-8 text-4xl font-bold">User App</h1>

      <div className="flex flex-col items-center space-y-4 rounded-lg bg-white p-6 shadow-md">
        <p className="text-2xl font-semibold">count is {count}</p>
        <button
          onClick={increment}
          className="rounded-md bg-blue-600 px-6 py-3 text-white transition hover:bg-blue-700"
        >
          Increment
        </button>
        <button
          onClick={() => {
            showBoundary(
              new Error(
                'Event handler error - this will trigger error boundary!',
              ),
            );
          }}
          className="rounded-md bg-red-500 px-6 py-3 text-white transition hover:bg-red-600"
        >
          Test Error Boundary (Event)
        </button>
        <button
          onClick={() => {
            registerMutation.mutate(
              { phoneNumber: '010-1234-5678' },
              {
                onError: error => {
                  if (error instanceof ApiError) {
                    console.log('API Error:', {
                      message: error.message,
                      response: error.response,
                      axiosError: error.axiosError,
                    });
                    showBoundary(error);
                  } else {
                    console.log('Generic Error:', error);
                  }
                },
              },
            );
          }}
          disabled={registerMutation.isPending}
          className="rounded-md bg-green-600 px-6 py-3 text-white transition hover:bg-green-700 disabled:opacity-50"
        >
          {registerMutation.isPending ? '등록 중...' : 'Test Register Mutation'}
        </button>

        <button
          onClick={decrement}
          className="rounded-md bg-red-600 px-6 py-3 text-white transition hover:bg-red-700"
        >
          Decrement
        </button>
        <p className="text-center text-gray-600">
          Edit
          <code className="rounded bg-gray-200 px-1">src/pages/Main.tsx</code>
          and save to test HMR
        </p>
      </div>

      <p className="mt-8 text-gray-500">
        Click on the Vite and React logos to learn more
      </p>
    </div>
  );
}
