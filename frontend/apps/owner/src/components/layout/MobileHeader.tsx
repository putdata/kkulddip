import { SidebarTrigger } from '@/components/ui/sidebar';
import { User } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface MobileHeaderProps {
  onSettingsClick: () => void;
}

const MobileHeader = ({ onSettingsClick }: MobileHeaderProps) => {
  return (
    <header className="bg-background fixed top-0 z-40 flex h-14 w-full items-center justify-between border-b px-4 md:hidden">
      <SidebarTrigger className="h-8 w-8" />
      <Button
        variant="ghost"
        size="icon"
        className="h-8 w-8"
        onClick={onSettingsClick}
      >
        <User className="h-4 w-4" />
      </Button>
    </header>
  );
};

export default MobileHeader;
