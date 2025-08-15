import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import type { LucideIcon } from 'lucide-react';

interface SettingsCardProps {
  icon: LucideIcon;
  title: string;
  description: string;
  isActive?: boolean;
  onClick?: () => void;
}

const SettingsCard = ({
  icon: Icon,
  title,
  description,
  isActive = false,
  onClick,
}: SettingsCardProps) => {
  return (
    <Card
      className={`group cursor-pointer transition-all hover:shadow-md ${
        isActive ? 'border-blue-200 bg-blue-50' : 'hover:border-gray-300'
      }`}
      onClick={onClick}
    >
      <CardHeader className="pb-3">
        <div className="flex items-center gap-3">
          <div
            className={`flex h-10 w-10 items-center justify-center rounded-lg ${
              isActive ? 'bg-blue-200' : 'bg-gray-100 group-hover:bg-gray-200'
            }`}
          >
            <Icon
              className={`h-5 w-5 ${
                isActive ? 'text-blue-700' : 'text-gray-600'
              }`}
            />
          </div>
          <div>
            <CardTitle
              className={`text-base ${
                isActive
                  ? 'text-blue-900'
                  : 'text-gray-700 group-hover:text-gray-900'
              }`}
            >
              {title}
            </CardTitle>
            <CardDescription className="text-sm">{description}</CardDescription>
          </div>
        </div>
      </CardHeader>
    </Card>
  );
};

export default SettingsCard;
