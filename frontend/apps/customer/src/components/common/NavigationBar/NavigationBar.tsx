import { MapPin, Bell, Menu } from 'lucide-react';

const NavigationBar = () => {
  return (
    <nav className="flex items-center justify-between bg-white px-4 py-3 shadow-md">
      <div className="flex items-center space-x-2">
        <img src="/logo.png" alt="logo" className="h-6 w-6" />
        <span className="text-lg font-semibold text-amber-600">꿀띱</span>
      </div>

      <div className="flex items-center space-x-4">
        <button className="flex items-center rounded-3xl bg-gray-100 p-1.5 pr-2.5 text-sm text-gray-700">
          <MapPin className="mr-1 h-4 w-4" />
          강남구
        </button>
        <button className="flex items-center text-gray-700">
          <Bell className="mr-1 h-4 w-4" />
        </button>
        <button className="text-sm text-gray-700">
          <Menu className="mr-1 h-4 w-4" />
        </button>
      </div>
    </nav>
  );
};

export default NavigationBar;
