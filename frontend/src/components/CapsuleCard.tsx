import React from "react";
import { CapsuleDto } from "@/types/capsule";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Lock, Unlock, Globe, LockKeyhole, Calendar, User } from "lucide-react";
import { format } from "date-fns";

import Link from "next/link";

interface CapsuleCardProps {
  capsule: CapsuleDto;
}

const CapsuleCard: React.FC<CapsuleCardProps> = ({ capsule }) => {
  const isLocked = capsule.status === "LOCKED";

  return (
    <Link href={`/capsule/${capsule.slug}`} className="block h-full">
      <Card className="glass-variant h-full group hover:bg-white/10 transition-all duration-500 overflow-hidden border-none relative">
        <CardHeader className="pb-2">
          <div className="flex justify-between items-start mb-2">
            <Badge
              variant={isLocked ? "outline" : "default"}
              className={`${isLocked ? "border-primary/50 text-primary" : "gold-gradient text-primary-foreground"} uppercase tracking-widest text-[10px] px-2 py-0.5`}
            >
              {isLocked ? (
                <Lock className="w-3 h-3 mr-1 inline" />
              ) : (
                <Unlock className="w-3 h-3 mr-1 inline" />
              )}
              {capsule.status}
            </Badge>
            {capsule.isPrivate ? (
              <LockKeyhole className="w-4 h-4 text-muted-foreground" />
            ) : (
              <Globe className="w-4 h-4 text-muted-foreground" />
            )}
          </div>
          <CardTitle className="font-serif text-xl text-foreground group-hover:text-primary transition-colors duration-300">
            {capsule.title}
          </CardTitle>
        </CardHeader>

        <CardContent className="pt-2 text-sm text-muted-foreground space-y-3">
          <div className="flex items-center space-x-2">
            <Calendar className="w-4 h-4 text-primary/70" />
            <span>
              {isLocked
                ? `Unlocks: ${format(new Date(capsule.unlockDate), "MMM d, yyyy")}`
                : "Available Now"}
            </span>
          </div>
          <div className="flex items-center space-x-2">
            <User className="w-4 h-4 text-primary/70" />
            <span>{capsule.ownerName}</span>
          </div>
        </CardContent>

        <CardFooter className="pt-4 pb-4 border-t border-white/5 flex justify-between items-center text-[10px] uppercase tracking-widest text-muted-foreground">
          <span>Created {format(new Date(capsule.createdAt), "MMM yyyy")}</span>
          <span className="text-primary opacity-0 group-hover:opacity-100 transition-opacity duration-300 font-bold">
            Resurrect →
          </span>
        </CardFooter>

        {/* Decorative gold line */}
        <div className="absolute bottom-0 left-0 w-0 h-[2px] bg-primary transition-all duration-500 group-hover:w-full" />
      </Card>
    </Link>
  );
};

export default CapsuleCard;
