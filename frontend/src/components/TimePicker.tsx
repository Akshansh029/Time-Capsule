import { useState, useEffect } from "react";
import { Clock } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { cn } from "@/lib/utils";

interface TimePickerProps {
  value: string;
  onChange: (value: string) => void;
  error?: boolean;
}

const TimePicker = ({ value, onChange, error }: TimePickerProps) => {
  const [open, setOpen] = useState(false);

  // Parse initial value (HH:mm)
  const [hours, setHours] = useState(value ? value.split(":")[0] : "12");
  const [minutes, setMinutes] = useState(value ? value.split(":")[1] : "00");
  const [period, setPeriod] = useState(parseInt(hours) >= 12 ? "PM" : "AM");

  // Format 24h for storage, 12h for display
  const displayHours = parseInt(hours) % 12 || 12;

  useEffect(() => {
    if (value) {
      const [h, m] = value.split(":");
      setHours(h);
      setMinutes(m);
      setPeriod(parseInt(h) >= 12 ? "PM" : "AM");
    }
  }, [value]);

  const handleTimeChange = (
    newHours: string,
    newMinutes: string,
    newPeriod: string,
  ) => {
    let h = parseInt(newHours);
    if (newPeriod === "PM" && h < 12) h += 12;
    if (newPeriod === "AM" && h === 12) h = 0;

    const formattedH = h.toString().padStart(2, "0");
    const formattedM = newMinutes.padStart(2, "0");
    onChange(`${formattedH}:${formattedM}`);
  };

  const hourOptions = Array.from({ length: 12 }, (_, i) => (i + 1).toString());
  const minuteOptions = ["00", "15", "30", "45"];

  return (
    <div className="w-full">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="ghost"
            className={cn(
              "w-full h-12 bg-white/5 border border-white/10 hover:bg-white/10 text-foreground justify-between px-4 font-normal transition-all",
              error && "border-red-500/50",
              open && "border-primary/50 ring-1 ring-primary/20",
            )}
          >
            <span className="text-sm">
              {displayHours}:{minutes} {period}
            </span>
            <Clock className="size-4 text-muted-foreground" />
          </Button>
        </PopoverTrigger>
        <PopoverContent
          className="w-64 p-4 glass-variant border-primary/20 shadow-2xl backdrop-blur-3xl z-50"
          align="end"
          sideOffset={8}
        >
          <div className="grid grid-cols-3 gap-4">
            {/* Hours */}
            <div className="space-y-2">
              <p className="text-[9px] uppercase tracking-widest text-primary/50 text-center font-semibold">
                Hour
              </p>
              <div className="flex flex-col gap-1 max-h-40 overflow-y-auto pr-1 scrollbar-hide">
                {hourOptions.map((h) => (
                  <button
                    key={h}
                    type="button"
                    onClick={() => {
                      const newH =
                        period === "PM"
                          ? parseInt(h) === 12
                            ? "12"
                            : (parseInt(h) + 12).toString()
                          : parseInt(h) === 12
                            ? "00"
                            : h.padStart(2, "0");
                      handleTimeChange(h, minutes, period);
                    }}
                    className={cn(
                      "text-xs py-1.5 rounded transition-colors",
                      parseInt(hours) % 12 === parseInt(h) % 12
                        ? "bg-primary text-primary-foreground font-bold"
                        : "hover:bg-white/5",
                    )}
                  >
                    {h}
                  </button>
                ))}
              </div>
            </div>

            {/* Minutes */}
            <div className="space-y-2">
              <p className="text-[9px] uppercase tracking-widest text-primary/50 text-center font-semibold">
                Min
              </p>
              <div className="flex flex-col gap-1 pr-1">
                {minuteOptions.map((m) => (
                  <button
                    key={m}
                    type="button"
                    onClick={() =>
                      handleTimeChange(
                        (parseInt(hours) % 12 || 12).toString(),
                        m,
                        period,
                      )
                    }
                    className={cn(
                      "text-xs py-1.5 rounded transition-colors",
                      minutes === m
                        ? "bg-primary text-primary-foreground font-bold"
                        : "hover:bg-white/5",
                    )}
                  >
                    {m}
                  </button>
                ))}
                <div className="mt-2 pt-2 border-t border-white/5">
                  <input
                    type="text"
                    placeholder="MM"
                    maxLength={2}
                    className="w-full bg-white/5 border border-white/10 rounded text-[10px] py-1 text-center outline-none focus:border-primary/30"
                    onChange={(e) => {
                      const val = e.target.value.replace(/\D/g, "");
                      if (val.length === 2 && parseInt(val) < 60) {
                        handleTimeChange(
                          (parseInt(hours) % 12 || 12).toString(),
                          val,
                          period,
                        );
                      }
                    }}
                  />
                </div>
              </div>
            </div>

            {/* AM/PM */}
            <div className="space-y-2">
              <p className="text-[9px] uppercase tracking-widest text-primary/50 text-center font-semibold">
                Period
              </p>
              <div className="flex flex-col gap-1">
                {["AM", "PM"].map((p) => (
                  <button
                    key={p}
                    type="button"
                    onClick={() =>
                      handleTimeChange(
                        (parseInt(hours) % 12 || 12).toString(),
                        minutes,
                        p,
                      )
                    }
                    className={cn(
                      "text-xs py-1.5 rounded transition-colors",
                      period === p
                        ? "bg-primary text-primary-foreground font-bold"
                        : "hover:bg-white/5",
                    )}
                  >
                    {p}
                  </button>
                ))}
              </div>
            </div>
          </div>

          <Button
            className="w-full mt-4 h-8 text-[10px] uppercase tracking-widest"
            onClick={() => setOpen(false)}
          >
            Confirm Time
          </Button>
        </PopoverContent>
      </Popover>
    </div>
  );
};

export default TimePicker;
