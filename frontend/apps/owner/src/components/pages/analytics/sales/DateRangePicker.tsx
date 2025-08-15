import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Calendar } from '@/components/ui/calendar';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { CalendarIcon } from 'lucide-react';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import { cn } from '@/lib/utils';
import type { DateRange } from 'react-day-picker';

interface DateRangePickerProps {
  onClose?: (range: DateRange | undefined) => void;
  placeholder?: string;
  className?: string;
  disabled?: (date: Date) => boolean;
  defaultValue?: DateRange;
  displayValue?: DateRange;
}

const DateRangePicker = ({
  onClose,
  placeholder = '날짜 범위를 선택하세요',
  className,
  disabled,
  defaultValue,
  displayValue,
}: DateRangePickerProps) => {
  const [date, setDate] = useState<DateRange | undefined>(defaultValue);

  return (
    <div className={cn('grid gap-2', className)}>
      <Popover
        onOpenChange={(open) => {
          // popover가 닫힐 때 날짜 범위가 완전히 선택되어 있으면 조회
          if (!open && date?.from && date?.to && onClose) {
            onClose(date);
          }
        }}
      >
        <PopoverTrigger asChild>
          <Button
            id="date"
            variant="outline"
            className={cn(
              'w-[300px] justify-start text-left font-normal',
              !displayValue && 'text-muted-foreground',
            )}
          >
            <CalendarIcon className="mr-2 h-4 w-4" />
            {displayValue?.from ? (
              displayValue.to ? (
                <>
                  {format(displayValue.from, 'LLL dd, y', { locale: ko })} -{' '}
                  {format(displayValue.to, 'LLL dd, y', { locale: ko })}
                </>
              ) : (
                format(displayValue.from, 'LLL dd, y', { locale: ko })
              )
            ) : (
              <span>{placeholder}</span>
            )}
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-auto p-0" align="start">
          <div onClick={(e) => e.stopPropagation()}>
            <Calendar
              mode="range"
              defaultMonth={date?.from}
              selected={date}
              onSelect={setDate}
              numberOfMonths={2}
              disabled={disabled}
              locale={ko}
            />
          </div>
        </PopoverContent>
      </Popover>
    </div>
  );
};

export { DateRangePicker };