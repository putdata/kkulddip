import { Bell } from 'lucide-react';

const EmptyNotifications = () => {
  return (
    <div className="flex h-[200px] items-center justify-center">
      <div className="text-muted-foreground text-center">
        <Bell className="mx-auto mb-2 h-8 w-8 opacity-50" />
        <p>알림이 없습니다</p>
        <p className="text-sm">새로운 알림이 오면 여기에 표시됩니다</p>
      </div>
    </div>
  );
};

export default EmptyNotifications;
