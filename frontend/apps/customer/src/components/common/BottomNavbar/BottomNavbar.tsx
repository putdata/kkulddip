import { Heart, Package, User, Home, Search } from 'lucide-react';
import type { ReactElement } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';
import { cloneElement } from 'react';

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
      icon: <Home className="h-6 w-6" />,
      onClick: onHomeClickHandler,
      label: '홈',
      path: ROUTE_PATH.HOME,
    },
    {
      icon: <Search className="h-6 w-6" />,
      onClick: onSearchClickHandler,
      label: '검색',
      path: ROUTE_PATH.SEARCH,
    },
    {
      icon: <Heart className="h-6 w-6" />,
      onClick: onLikeClickHandler,
      label: '찜',
      path: ROUTE_PATH.LIKE,
    },
    {
      icon: <Package className="h-6 w-6" />,
      onClick: onOrderClickHandler,
      label: '주문내역',
      path: ROUTE_PATH.ORDER,
    },
    {
      icon: <User className="h-6 w-6" />,
      onClick: onMyPageClickHandler,
      label: '마이',
      path: ROUTE_PATH.MY,
    },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 z-50 h-16 border-t bg-white shadow-lg">
      <div className="flex h-full items-center justify-around">
        {gnbProps.map(item => {
          const isActive = location.pathname === item.path;

          return (
            <button
              key={item.label}
              onClick={item.onClick}
              className={`flex h-full flex-1 flex-col items-center justify-center gap-1 transition-all duration-200 ${
                isActive
                  ? 'text-amber-600'
                  : 'text-gray-400 hover:text-gray-600'
              }`}
            >
              <div
                className={`transition-transform duration-200 ${isActive ? 'scale-110' : 'scale-100'}`}
              >
                {cloneElement(
                  item.icon as ReactElement<{
                    className?: string;
                    fill?: string;
                  }>,
                  {
                    className: `h-6 w-6 ${isActive ? 'stroke-2' : 'stroke-1.5'}`,
                    fill: isActive ? 'currentColor' : 'none',
                  },
                )}
              </div>
              <span
                className={`text-[10px] transition-all duration-200 ${
                  isActive ? 'font-semibold' : 'font-normal'
                }`}
              >
                {item.label}
              </span>
              {isActive && (
                <div className="absolute bottom-0 h-0.5 w-12 rounded-full bg-amber-600" />
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
}

export default BottomNavbar;
