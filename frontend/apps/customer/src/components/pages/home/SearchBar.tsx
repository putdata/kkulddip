import { useState } from 'react';
import { Input } from '@/components/ui/input';
import { SearchIcon } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const SearchBar = () => {
  const [searchInput, setSearchInput] = useState('');
  const navigate = useNavigate();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchInput.trim()) {
      navigate(`/search?keyword=${encodeURIComponent(searchInput.trim())}`);
    }
  };

  return (
    <div className="w-full items-center px-5 py-2">
      <form onSubmit={handleSearch} className="relative">
        <div className="text-muted-foreground absolute left-2.5 top-2.5 h-4 w-4">
          <SearchIcon className="h-4 w-4" />
        </div>
        <Input
          id="search"
          type="search"
          placeholder="음식점이나 음식을 검색하세요"
          className="bg-background w-full rounded-lg pl-8"
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
        />
      </form>
    </div>
  );
};

export default SearchBar;
