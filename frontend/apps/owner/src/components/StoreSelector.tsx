import { ChevronsUpDown, Check, Store } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar';
import { useStoreSelection } from '@/hooks/useStoreSelection';

const StoreSwitcher = () => {
  const { stores, selectedStore, selectStore } = useStoreSelection();

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <div className="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
                <Store className="size-4" />
              </div>
              <div className="flex flex-col gap-0.5 leading-none">
                <span className="font-medium">
                  {selectedStore?.storeName || '가게 선택'}
                </span>
                <span className="text-sidebar-foreground/60 text-xs">
                  {selectedStore?.storeAddress || '가게를 선택해주세요'}
                </span>
              </div>
              <ChevronsUpDown className="ml-auto" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            className="w-[--radix-dropdown-menu-trigger-width] min-w-56"
            align="start"
          >
            {stores.map(store => (
              <DropdownMenuItem
                key={store.storeId}
                onSelect={() => selectStore(store)}
              >
                <div className="flex w-full items-center gap-2">
                  <div className="bg-background flex size-6 items-center justify-center rounded-sm border">
                    <Store className="size-4 shrink-0" />
                  </div>
                  <div className="flex flex-1 flex-col gap-0.5">
                    <span className="font-medium">{store.storeName}</span>
                    <span className="text-muted-foreground text-xs">
                      {store.storeAddress}
                    </span>
                  </div>
                  {selectedStore?.storeId === store.storeId && (
                    <Check className="ml-auto size-4" />
                  )}
                </div>
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
    </SidebarMenu>
  );
};

export default StoreSwitcher;
