import reactLogo from './assets/react.svg';
import viteLogo from '/vite.svg';
import { useCounter } from 'common';

function App() {
  const { count, increment, decrement } = useCounter();

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

      <h1 className="mb-8 text-4xl font-bold">Owner App</h1>

      <div className="flex flex-col items-center space-y-4 rounded-lg bg-white p-6 shadow-md">
        <p className="text-2xl font-semibold">count is {count}</p>
        <button
          onClick={increment}
          className="rounded-md bg-blue-600 px-6 py-3 text-white transition hover:bg-blue-700"
        >
          Increment
        </button>
        <button
          onClick={decrement}
          className="rounded-md bg-red-600 px-6 py-3 text-white transition hover:bg-red-700"
        >
          Decrement
        </button>
        <p className="text-center text-gray-600">
          Edit <code className="rounded bg-gray-200 px-1">src/App.tsx</code> and
          save to test HMR
        </p>
      </div>

      <p className="mt-8 text-gray-500">
        Click on the Vite and React logos to learn more
      </p>
    </div>
  );
}

export default App;
