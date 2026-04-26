"use client";

import { useState, useEffect } from "react";

import { parseDate } from "chrono-node";
import { CalendarIcon } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";

function formatDate(date: Date | undefined) {
  if (!date) {
    return "";
  }

  return date.toLocaleDateString("en-US", {
    day: "2-digit",
    month: "long",
    year: "numeric",
  });
}

interface DatePickerProps {
  date: Date | undefined;
  setDate: (date: Date | undefined) => void;
  error?: boolean;
}

const DatePicker = ({ date, setDate, error }: DatePickerProps) => {
  const [open, setOpen] = useState(false);
  const [value, setValue] = useState(date ? formatDate(date) : "");
  const [month, setMonth] = useState<Date | undefined>(date || new Date());

  // Sync internal state with external prop
  useEffect(() => {
    if (date) {
      setValue(formatDate(date));
      setMonth(date);
    }
  }, [date]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    setValue(val);
    const parsedDate = parseDate(val);

    if (parsedDate) {
      setDate(parsedDate);
      // setMonth(parsedDate); // Let the useEffect handle this
    }
  };

  return (
    <div className="w-full space-y-2">
      <div className="relative">
        <Input
          id="date"
          value={value}
          placeholder="Tomorrow, next Friday, or 2026-05-10"
          className={`bg-white/5 border-white/10 focus:border-primary/50 focus:ring-primary/20 transition-all pr-12 h-12 text-sm w-full ${error ? "border-red-500/50" : ""}`}
          onChange={handleInputChange}
          onKeyDown={(e) => {
            if (e.key === "ArrowDown") {
              e.preventDefault();
              setOpen(true);
            }
          }}
        />
        <Popover open={open} onOpenChange={setOpen}>
          <PopoverTrigger asChild>
            <Button
              id="date-picker-trigger"
              variant="ghost"
              type="button"
              className="absolute right-1 top-1/2 -translate-y-1/2 size-10 hover:bg-white/10 text-muted-foreground hover:text-primary transition-colors z-20"
            >
              <CalendarIcon className="size-4" />
              <span className="sr-only">Pick a date</span>
            </Button>
          </PopoverTrigger>
          <PopoverContent
            className="w-auto p-0 glass-variant border-primary/20 shadow-2xl z-50 backdrop-blur-3xl"
            align="end"
            side="bottom"
            sideOffset={8}
          >
            <Calendar
              mode="single"
              selected={date}
              month={month}
              onMonthChange={setMonth}
              disabled={(date) =>
                date < new Date(new Date().setHours(0, 0, 0, 0))
              }
              onSelect={(selectedDate) => {
                setDate(selectedDate);
                if (selectedDate) {
                  setValue(formatDate(selectedDate));
                }
                setOpen(false);
              }}
              className="bg-[#1c1b1b]"
              initialFocus
            />
          </PopoverContent>
        </Popover>
      </div>
    </div>
  );
};

export default DatePicker;
