import { useLocation, useNavigate } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';
import { Bell, ShoppingCart, ChevronLeft } from 'lucide-react';

const PageNavbar = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const getPageTitle = () => {
    switch (location.pathname) {
      case ROUTE_PATH.SEARCH:
        return '검색';
      case ROUTE_PATH.LIKE:
        return '찜';
      case ROUTE_PATH.ORDER:
        return '주문내역';
      case ROUTE_PATH.MY:
        return '마이꿀띱';
      default:
        return '꿀띱';
    }
  };

  return (
    <div className="fixed left-0 right-0 top-0 z-50 border-t bg-white">
      <nav className="flex items-center justify-between bg-white px-4 py-3 shadow-md">
        <div className="flex items-center space-x-2">
          <ChevronLeft
            className="mr-2 h-5 w-5"
            onClick={() => {
              navigate(-1);
            }}
          />

          <span className="text-lg font-semibold text-amber-600">
            {getPageTitle()}
          </span>
        </div>
        <div className="flex space-x-4">
          <Bell className="mr-3 h-5 w-5" />
          <ShoppingCart
            className="mr-1 h-5 w-5"
            onClick={() => navigate(ROUTE_PATH.PAY)}
          />
        </div>
      </nav>
    </div>
  );
};

export default PageNavbar;
