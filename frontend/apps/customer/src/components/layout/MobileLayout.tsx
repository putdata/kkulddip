import { Outlet } from 'react-router-dom';

export default function MobileLayout() {
  return (
    <div className="mx-auto min-h-dvh w-full max-w-md bg-white">
      <Outlet />
    </div>
  );
}
