import SearchBar from '@/components/pages/home/SearchBar';
import { ROUTE_PATH } from '@/router';
import { useNavigate } from 'react-router-dom';
import { MapPin, Bell, ShoppingCart } from 'lucide-react';
import { Button } from '@/components/ui/button';

const NavigationBar = () => {
  const navigate = useNavigate();
  return (
    <nav className="h-min-25 fixed left-0 right-0 top-0 z-50 flex flex-col bg-white px-4 pb-2 pt-3 shadow-md">
      <div className="flex justify-between">
        <div className="flex items-center space-x-2">
          <img src="/logo_orange.png" alt="logo" className="h-10 w-10" />
          <span
            className="text-lg font-semibold text-amber-600"
            style={{ fontFamily: 'Cafe24 Ssurround, sans-serif' }}
          >
            꿀띱
          </span>
        </div>

        <div className="flex items-center space-x-4">
          <button className="flex items-center rounded-3xl bg-gray-100 p-1.5 pr-2.5 text-sm text-gray-700">
            <MapPin className="mr-1 h-4 w-4" />
            강남구
          </button>
          <button
            className="flex items-center text-gray-700"
            onClick={() => navigate(ROUTE_PATH.NOTIFICATION)}
          >
            <Bell className="mr-1 h-4 w-4" />
          </button>
          <button className="text-sm text-gray-700">
            <ShoppingCart
              className="mr-1 h-4 w-4"
              onClick={() => navigate(ROUTE_PATH.PAY)}
            />
          </button>
        </div>
      </div>
      <div className="flex items-center justify-between">
        <SearchBar />
        {/* TODO: 일단은 마이페이지로 이동, STREAMS 로 이동 필요 */}
        {/* <Button variant="destructive" onClick={() => navigate(ROUTE_PATH.STERAMS)}> */}
        <Button
          variant="destructive"
          className="rounded-xl"
          onClick={() => navigate(ROUTE_PATH.MY)}
        >
          LIVE
        </Button>
      </div>
    </nav>
  );
};

export default NavigationBar;
