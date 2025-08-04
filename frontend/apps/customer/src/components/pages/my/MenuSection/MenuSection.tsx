import {
  ChevronRight,
  ShoppingBag,
  Star,
  Clock,
  MapPin,
  Bell,
  Settings,
  HelpCircle,
  Shield,
  Gift,
  CreditCard,
  Heart,
  Wallet,
} from 'lucide-react';

export interface MenuItem {
  id: string;
  label: string;
  onClick?: () => void;
}

export interface MenuSectionProps {
  title: string;
  items: MenuItem[];
}

const getMenuIcon = (id: string) => {
  const iconMap: { [key: string]: React.ReactNode } = {
    'order-history': <ShoppingBag className="h-4 w-4" />,
    'reviews': <Star className="h-4 w-4" />,
    'reorder': <Clock className="h-4 w-4" />,
    'address': <MapPin className="h-4 w-4" />,
    'notifications': <Bell className="h-4 w-4" />,
    'app-settings': <Settings className="h-4 w-4" />,
    'customer-service': <HelpCircle className="h-4 w-4" />,
    'privacy-policy': <Shield className="h-4 w-4" />,
    'coupons': <Gift className="h-4 w-4" />,
    'points': <Wallet className="h-4 w-4" />,
    'payment': <CreditCard className="h-4 w-4" />,
    'favorites': <Heart className="h-4 w-4" />,
  };

  return iconMap[id] || <Settings className="h-4 w-4" />;
};

const MenuSection = ({ title, items }: MenuSectionProps) => {
  return (
    <div className="mt-2 rounded-xl bg-white px-4 py-4">
      <h3 className="mb-3 font-semibold text-gray-900">{title}</h3>
      <div className="space-y-3">
        {items.map(item => (
          <button
            key={item.id}
            onClick={item.onClick}
            className="flex w-full items-center justify-between rounded-lg px-2 py-2 transition-colors hover:bg-gray-50"
          >
            <div className="flex items-center space-x-3">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gray-100">
                <div className="text-gray-600">{getMenuIcon(item.id)}</div>
              </div>
              <span className="text-gray-900">{item.label}</span>
            </div>
            <ChevronRight className="h-4 w-4 text-gray-400" />
          </button>
        ))}
      </div>
    </div>
  );
};

export default MenuSection;
