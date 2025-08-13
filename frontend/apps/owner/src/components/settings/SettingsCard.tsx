import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { LucideIcon } from 'lucide-react';

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
      className={`group transition-all ${
        isActive
          ? 'cursor-pointer hover:shadow-md'
          : 'cursor-not-allowed opacity-60'
      }`}
      onClick={isActive ? onClick : undefined}
    >
      <CardHeader className="pb-3">
        <div className="flex items-center gap-3">
          <div
            className={`flex h-10 w-10 items-center justify-center rounded-lg ${
              isActive ? 'bg-blue-100 group-hover:bg-blue-200' : 'bg-gray-100'
            }`}
          >
            <Icon
              className={`h-5 w-5 ${
                isActive ? 'text-blue-600' : 'text-gray-400'
              }`}
            />
          </div>
          <div>
            <CardTitle
              className={`text-base ${
                isActive ? 'text-gray-900' : 'text-gray-500'
              }`}
            >
              {title}
            </CardTitle>
            <CardDescription className="text-sm">{description}</CardDescription>
          </div>
        </div>
      </CardHeader>
      {!isActive && (
        <CardContent className="pt-0">
          <span className="text-xs text-gray-400">준비 중</span>
        </CardContent>
      )}
    </Card>
  );
};

export default SettingsCard;
