import BottomNavbar from '@/components/common/BottomNavbar/BottomNavbar';
import NavigationBar from '@/components/common/NavigationBar/NavigationBar';
import { Outlet } from 'react-router-dom';

export default function MobileLayoutWithNavbar() {
  return (
    <div className="mx-auto min-h-screen w-full max-w-md bg-white">
      <NavigationBar />
      <Outlet />
      <BottomNavbar />
    </div>
  );
}
