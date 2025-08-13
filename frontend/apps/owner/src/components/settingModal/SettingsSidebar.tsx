import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { settingsItems, type SettingItemId } from '@/constants/settingItems';

interface SettingsSidebarProps {
  selectedTab: SettingItemId;
  onTabChange: (tab: SettingItemId) => void;
  className?: string;
}

const SettingsSidebar = ({
  selectedTab,
  onTabChange,
  className,
}: SettingsSidebarProps) => {
  const isMobileLayout = className?.includes('flex-row');

  return (
    <div className={cn('flex flex-col space-y-1', className)}>
      {!isMobileLayout && (
        <div className="pb-2">
          <h3 className="mb-1 px-2 text-sm font-semibold text-gray-900">
            설정
          </h3>
        </div>
      )}
      {settingsItems.map(item => {
        const Icon = item.icon;
        const isSelected = selectedTab === item.id;

        return (
          <Button
            key={item.id}
            variant={isSelected ? 'secondary' : 'ghost'}
            className={cn(
              isMobileLayout
                ? 'h-auto flex-1 flex-col justify-center gap-1 px-2 py-2 text-xs font-normal'
                : 'h-8 justify-start px-2 py-1.5 text-sm font-normal',
              isSelected && 'bg-gray-100 text-gray-900',
            )}
            onClick={() => onTabChange(item.id)}
          >
            <Icon className={cn(isMobileLayout ? 'h-4 w-4' : 'mr-2 h-4 w-4')} />
            <span className={cn(isMobileLayout && 'text-xs')}>
              {item.label}
            </span>
          </Button>
        );
      })}
    </div>
  );
};

export default SettingsSidebar;
