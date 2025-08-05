import { Gift, CreditCard, Heart, Wallet } from 'lucide-react';

export interface QuickAction {
  id: string;
  label: string;
  icon: React.ReactNode;
  bgColor: string;
  iconColor: string;
  onClick?: () => void;
}

export interface QuickActionsProps {
  actions?: QuickAction[];
}

const defaultActions: QuickAction[] = [
  {
    id: 'coupons',
    label: '쿠폰함',
    icon: <Gift className="h-6 w-6" />,
    bgColor: 'bg-orange-100',
    iconColor: 'text-orange-600',
  },
  {
    id: 'points',
    label: '포인트',
    icon: <Wallet className="h-6 w-6" />,
    bgColor: 'bg-green-100',
    iconColor: 'text-green-600',
  },
  {
    id: 'payment',
    label: '결제수단',
    icon: <CreditCard className="h-6 w-6" />,
    bgColor: 'bg-blue-100',
    iconColor: 'text-blue-600',
  },
  {
    id: 'favorites',
    label: '찜한가게',
    icon: <Heart className="h-6 w-6" />,
    bgColor: 'bg-red-100',
    iconColor: 'text-red-600',
  },
];

const QuickActions = ({ actions = defaultActions }: QuickActionsProps) => {
  return (
    <div className="border-b-1 mt-1 border-gray-100 bg-white px-4 py-4">
      <div className="grid grid-cols-4 gap-4">
        {actions.map(action => (
          <button
            key={action.id}
            onClick={action.onClick}
            className="flex flex-col items-center space-y-2 rounded-lg pb-2"
          >
            <div
              className={`h-12 w-12 ${action.bgColor} flex items-center justify-center rounded-full`}
            >
              <div className={action.iconColor}>{action.icon}</div>
            </div>
            <span className="text-xs text-gray-700">{action.label}</span>
          </button>
        ))}
      </div>
    </div>
  );
};

export default QuickActions;
