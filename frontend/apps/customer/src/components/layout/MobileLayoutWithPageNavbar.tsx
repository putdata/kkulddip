import BottomNavbar from '@/components/common/BottomNavbar/BottomNavbar';
import PageNavbar from '@/components/common/PageNavbar/PageNavbar';
import { Outlet } from 'react-router-dom';

export default function MobileLayoutWithPageNavbar() {
  return (
    <div className="mx-auto min-h-screen w-full max-w-md bg-white">
      <PageNavbar />
      <div className="pb-14 pt-12">
        <Outlet />
      </div>
      <BottomNavbar />
    </div>
  );
}
