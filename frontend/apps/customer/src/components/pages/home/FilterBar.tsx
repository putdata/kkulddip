import { Toggle } from '@/components/ui/toggle';

const FilterBar = () => {
  return (
    <div className="flex gap-2 px-5">
      <Toggle
        variant="outline"
        aria-label="pickup available"
        className="data-[state=on]:bg-amber-500 data-[state=on]:font-bold data-[state=on]:text-white"
      >
        픽업가능
      </Toggle>
      <Toggle
        variant="outline"
        aria-label="within 1km"
        className="data-[state=on]:bg-amber-500 data-[state=on]:font-bold data-[state=on]:text-white"
      >
        1km 이내
      </Toggle>
      <Toggle
        variant="outline"
        aria-label="addtional discount"
        className="data-[state=on]:bg-amber-500 data-[state=on]:font-bold data-[state=on]:text-white"
      >
        추가 할인중
      </Toggle>
    </div>
  );
};

export default FilterBar;
