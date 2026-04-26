"use client";
import React, { useState, useEffect } from "react";

interface TimeLeft {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
}

interface CapsuleCountdownProps {
  unlockDate: string;
  onUnlock?: () => void;
}

const CapsuleCountdown: React.FC<CapsuleCountdownProps> = ({
  unlockDate,
  onUnlock,
}) => {
  const calculateTimeLeft = (): TimeLeft => {
    const difference = +new Date(unlockDate) - +new Date();
    let timeLeft: TimeLeft = { days: 0, hours: 0, minutes: 0, seconds: 0 };

    if (difference > 0) {
      timeLeft = {
        days: Math.floor(difference / (1000 * 60 * 60 * 24)),
        hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
        minutes: Math.floor((difference / 1000 / 60) % 60),
        seconds: Math.floor((difference / 1000) % 60),
      };
    }
    return timeLeft;
  };

  const [timeLeft, setTimeLeft] = useState<TimeLeft>(calculateTimeLeft());

  useEffect(() => {
    const timer = setInterval(() => {
      const remaining = calculateTimeLeft();
      setTimeLeft(remaining);

      if (
        remaining.days === 0 &&
        remaining.hours === 0 &&
        remaining.minutes === 0 &&
        remaining.seconds === 0
      ) {
        clearInterval(timer);
        if (onUnlock) onUnlock();
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [unlockDate]);

  const TimeUnit = ({ value, label }: { value: number; label: string }) => (
    <div className="flex flex-col items-center">
      <div className="text-3xl md:text-5xl font-mono text-primary font-bold tabular-nums">
        {value.toString().padStart(2, "0")}
      </div>
      <div className="text-[10px] uppercase tracking-widest text-muted-foreground mt-1 font-sans">
        {label}
      </div>
    </div>
  );

  return (
    <div className="flex items-center justify-center space-x-6 md:space-x-12 py-8 px-12 glass-variant rounded-[3rem] border-primary/20 shadow-2xl relative overflow-hidden group">
      <div className="absolute inset-0 bg-primary/5 opacity-0 group-hover:opacity-100 transition-opacity duration-1000" />
      <TimeUnit value={timeLeft.days} label="Days" />
      <div className="text-3xl md:text-5xl font-mono text-primary/30 pt-1">
        :
      </div>
      <TimeUnit value={timeLeft.hours} label="Hrs" />
      <div className="text-3xl md:text-5xl font-mono text-primary/30 pt-1">
        :
      </div>
      <TimeUnit value={timeLeft.minutes} label="Min" />
      <div className="text-3xl md:text-5xl font-mono text-primary/30 pt-1">
        :
      </div>
      <TimeUnit value={timeLeft.seconds} label="Sec" />
    </div>
  );
};

export default CapsuleCountdown;
