import { Input } from '@/components/ui/input';
import { SearchIcon } from 'lucide-react';

const SearchBar = () => {
  return (
    // <div>
    //   <div className="bg-color-grey-98 flex w-96 items-start justify-center overflow-hidden rounded-[10px] pb-2.5 pl-8 pr-3 pt-1.5">
    //     <div className="inline-flex flex-1 flex-col items-start justify-start overflow-hidden pb-[0.80px] pt-[3.20px]">
    //       <div className="text-color-grey-46 justify-center self-stretch text-sm">
    //         음식점이나 음식을 검색하세요
    //       </div>
    //     </div>
    //   </div>
    //   <div className="absolute left-[10px] top-[7.80px] inline-flex h-5 w-5 flex-col items-start justify-start">
    //     <div className="text-color-azure-65 justify-center font-sans text-sm font-normal leading-tight">
    //       🔍
    //     </div>
    //   </div>
    // </div>
    <div className="w-full items-center px-5 py-2">
      <div className="relative">
        <div className="text-muted-foreground absolute left-2.5 top-2.5 h-4 w-4">
          <SearchIcon className="h-4 w-4" />
        </div>
        <Input
          id="search"
          type="search"
          placeholder="음식점이나 음식을 검색하세요"
          className="bg-background w-full rounded-lg pl-8"
        />
      </div>
    </div>
  );
};

export default SearchBar;
