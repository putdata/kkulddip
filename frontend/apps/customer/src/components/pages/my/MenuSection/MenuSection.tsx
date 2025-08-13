import { ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { type ReactNode } from 'react';

export interface MenuItem {
  id: string;
  label: string;
  path: string;
  icon: ReactNode;
}

export interface MenuSectionProps {
  title: string;
  items: MenuItem[];
}

const MenuSection = ({ title, items }: MenuSectionProps) => {
  const navigate = useNavigate();
  return (
    <div className="mt-2 rounded-xl bg-white px-4 py-4">
      <h3 className="mb-3 font-semibold text-gray-900">{title}</h3>
      <div className="space-y-3">
        {items.map(item => (
          <button
            key={item.id}
            onClick={() => navigate(item.path)}
            className="flex w-full items-center justify-between rounded-lg px-2 py-2 transition-colors hover:bg-gray-50"
          >
            <div className="flex items-center space-x-3">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gray-100">
                <div className="text-gray-600">{item.icon}</div>
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
