import { ReactNode } from 'react';
import { LucideIcon } from 'lucide-react';

interface SettingsPageSectionProps {
  icon: LucideIcon;
  title: string;
  description: string;
  children: ReactNode;
}

const SettingsPageSection = ({
  icon: Icon,
  title,
  description,
  children,
}: SettingsPageSectionProps) => {
  return (
    <div className="space-y-4">
      <div className="border-t pt-6">
        <div className="mb-4 flex items-center gap-3">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-100">
            <Icon className="h-4 w-4 text-blue-600" />
          </div>
          <div>
            <h2 className="text-lg font-semibold text-gray-900">{title}</h2>
            <p className="text-sm text-gray-600">{description}</p>
          </div>
        </div>
        {children}
      </div>
    </div>
  );
};

export default SettingsPageSection;
