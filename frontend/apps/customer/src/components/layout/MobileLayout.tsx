import BottomNavbar from '@/BottomNavbar';
import { Outlet } from 'react-router-dom';

export default function MobileLayout() {
  return (
    <div className="mx-auto min-h-screen w-full max-w-md bg-white">
      <Outlet />
      <BottomNavbar />
    </div>
  );
}
