import BottomNavbar from '@/components/common/BottomNavbar/BottomNavbar';
import NavigationBar from '@/components/common/NavigationBar/NavigationBar';
import { Outlet } from 'react-router-dom';

export default function MobileLayoutWithNavbar() {
  return (
    <div className="mx-auto min-h-dvh w-full max-w-md bg-white">
      <NavigationBar />
      <div className="pt-25 pb-14">
        <Outlet />
      </div>
      <BottomNavbar />
    </div>
  );
}
