import { Heart, Package, User, Home, Search } from 'lucide-react';
import type { ReactElement } from 'react';
import { useNavigate } from 'react-router-dom';

interface GnbPropsInterface {
  icon: ReactElement;
  onClick: () => void;
  label: string;
}

function BottomNavbar() {
  const navigate = useNavigate();
  const onSearchClickHandler = () => navigate('/search');
  const onHomeClickHandler = () => navigate('/');
  const onMyPageClickHandler = () => navigate('/my');
  const onLikeClickHandler = () => navigate('/like');
  const onOrderClickHandler = () => navigate('/order');

  const gnbProps: GnbPropsInterface[] = [
    {
      icon: <Home />,
      onClick: onHomeClickHandler,
      label: '홈',
    },
    {
      icon: <Search />,
      onClick: onSearchClickHandler,
      label: '검색',
    },
    {
      icon: <Heart />,
      onClick: onLikeClickHandler,
      label: '찜',
    },
    {
      icon: <Package />,
      onClick: onOrderClickHandler,
      label: '주문내역',
    },
    {
      icon: <User />,
      onClick: onMyPageClickHandler,
      label: '마이',
    },
  ];

  return (
    <div className="border-t-1 rounded-t-lg border-t-gray-300 bg-gray-100 p-1.5">
      <div className="flex items-center justify-evenly text-xs text-gray-500">
        {gnbProps.map(item => (
          <div
            className="flex flex-1 flex-col items-center p-4 px-5"
            onClick={item.onClick}
          >
            {item.icon}
            <div className="mt-1 text-center">{item.label}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default BottomNavbar;
