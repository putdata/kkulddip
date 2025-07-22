import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import "./index.css";
import { useCounter } from "common";

function App() {
  const { count, increment, decrement } = useCounter();

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100 p-6">
      <div className="flex space-x-6 mb-6">
        <a href="https://vite.dev" target="_blank" rel="noreferrer">
          <img src={viteLogo} className="h-16 w-16" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank" rel="noreferrer">
          <img src={reactLogo} className="h-16 w-16" alt="React logo" />
        </a>
      </div>

      <h1 className="text-4xl font-bold mb-8">User App</h1>

      <div className="bg-white p-6 rounded-lg shadow-md flex flex-col items-center space-y-4">
        <p className="text-2xl font-semibold">count is {count}</p>
        <button
          onClick={increment}
          className="px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
        >
          Increment
        </button>
        <button
          onClick={decrement}
          className="px-6 py-3 bg-red-600 text-white rounded-md hover:bg-red-700 transition"
        >
          Decrement
        </button>
        <p className="text-gray-600 text-center">
          Edit <code className="bg-gray-200 px-1 rounded">src/App.tsx</code> and
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
