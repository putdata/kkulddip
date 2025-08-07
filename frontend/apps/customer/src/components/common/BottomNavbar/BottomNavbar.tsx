import { Heart, Package, User, Home, Search } from 'lucide-react';
import type { ReactElement } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';

interface GnbPropsInterface {
  icon: ReactElement;
  onClick: () => void;
  label: string;
  path: string;
}

function BottomNavbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const onSearchClickHandler = () => navigate(ROUTE_PATH.SEARCH);
  const onHomeClickHandler = () => navigate(ROUTE_PATH.HOME);
  const onMyPageClickHandler = () => navigate(ROUTE_PATH.MY);
  const onLikeClickHandler = () => navigate(ROUTE_PATH.LIKE);
  const onOrderClickHandler = () => navigate(ROUTE_PATH.ORDER);

  const gnbProps: GnbPropsInterface[] = [
    {
      icon: <Home className="h-5 w-5" />,
      onClick: onHomeClickHandler,
      label: '홈',
      path: ROUTE_PATH.HOME,
    },
    {
      icon: <Search className="h-5 w-5" />,
      onClick: onSearchClickHandler,
      label: '검색',
      path: ROUTE_PATH.SEARCH,
    },
    {
      icon: <Heart className="h-5 w-5" />,
      onClick: onLikeClickHandler,
      label: '찜',
      path: ROUTE_PATH.LIKE,
    },
    {
      icon: <Package className="h-5 w-5" />,
      onClick: onOrderClickHandler,
      label: '주문내역',
      path: ROUTE_PATH.ORDER,
    },
    {
      icon: <User className="h-5 w-5" />,
      onClick: onMyPageClickHandler,
      label: '마이',
      path: ROUTE_PATH.MY,
    },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 z-50 border-t bg-white">
      <div className="flex items-center justify-evenly text-xs text-gray-500">
        {gnbProps.map(item => {
          const isActive = location.pathname === item.path;

          return (
            <div
              key={item.label}
              onClick={item.onClick}
              className={`flex flex-1 flex-col items-center p-1 py-2 ${
                isActive ? 'bg-yellow-50' : ''
              }`}
            >
              {item.icon}
              <div
                className={`mt-1 text-xs ${isActive ? 'font-semibold text-black' : ''}`}
              >
                {item.label}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default BottomNavbar;
