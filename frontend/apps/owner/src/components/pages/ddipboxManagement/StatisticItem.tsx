import { type ReactNode } from 'react';

interface StatisticItemProps {
  label: string;
  value: string | number;
  icon: ReactNode;
  trend?: 'up' | 'down' | 'neutral';
}

const StatisticItem = ({
  label,
  value,
  icon,
  trend = 'neutral',
}: StatisticItemProps) => {
  const trendColors = {
    up: 'text-green-600',
    down: 'text-red-600',
    neutral: 'text-gray-600',
  };

  return (
    <div className="flex items-center gap-3">
      <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-gray-100">
        <div className="h-5 w-5 text-gray-600">{icon}</div>
      </div>
      <div className="min-w-0 flex-1">
        <div className="text-sm font-medium text-gray-900">{label}</div>
        <div className={`text-lg font-semibold ${trendColors[trend]}`}>
          {value}
        </div>
      </div>
    </div>
  );
};

export default StatisticItem;