import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-100 p-6">
      <div className="text-center">
        <h1 className="mb-4 text-9xl font-bold text-gray-300">404</h1>
        <h2 className="mb-4 text-3xl font-semibold text-gray-700">
          Page Not Found
        </h2>
        <p className="mb-8 text-gray-600">
          The page you are looking for does not exist.
        </p>
        <Link
          to="/"
          className="rounded-md bg-blue-600 px-6 py-3 text-white transition hover:bg-blue-700"
        >
          Go Home
        </Link>
      </div>
    </div>
  );
}