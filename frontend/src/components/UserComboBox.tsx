"use client";

import * as React from "react";
import { Check, ChevronsUpDown, Loader2, Search, UserPlus } from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import api from "@/lib/api";

export interface UserSuggestion {
  id: string;
  name: string;
  email: string;
}

interface UserComboBoxProps {
  onSelect: (user: UserSuggestion) => void;
  selectedEmails: string[];
}

export function UserComboBox({ onSelect, selectedEmails }: UserComboBoxProps) {
  const [open, setOpen] = React.useState(false);
  const [query, setQuery] = React.useState("");
  const [loading, setLoading] = React.useState(false);
  const [suggestions, setSuggestions] = React.useState<UserSuggestion[]>([]);

  React.useEffect(() => {
    const timer = setTimeout(async () => {
      if (query.length < 2) {
        setSuggestions([]);
        return;
      }

      setLoading(true);
      try {
        const response = await api.get(`/users/search?q=${query}`);

        setSuggestions(response.data);
      } catch (error) {
        console.error("Failed to search users:", error);
        // Fallback to empty if search fails (endpoint might not exist yet)
        setSuggestions([]);
      } finally {
        setLoading(false);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [query]);

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className="w-full justify-between bg-white/5 border-white/10 hover:bg-white/10 text-muted-foreground h-11 rounded-xl px-4"
        >
          <div className="flex items-center gap-2">
            <Search className="w-4 h-4 opacity-50" />
            <span className="text-xs uppercase tracking-widest font-bold">
              Find Custodians...
            </span>
          </div>
          <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent
        className="w-[var(--radix-popover-trigger-width)] p-0 glass-variant border-primary/20 shadow-2xl"
        align="start"
      >
        <Command shouldFilter={false}>
          <CommandInput
            placeholder="Search by name or email..."
            value={query}
            onValueChange={setQuery}
            className="border-none focus:ring-0"
          />
          <CommandList className="max-h-[250px]">
            {loading && (
              <div className="flex items-center justify-center py-6">
                <Loader2 className="w-5 h-5 animate-spin text-primary" />
              </div>
            )}

            {query.length >= 2 && !loading && suggestions.length === 0 && (
              <CommandEmpty className="py-6 text-center text-xs uppercase tracking-widest text-muted-foreground">
                No witnesses found in the vault.
              </CommandEmpty>
            )}

            {query.length < 2 && !loading && (
              <div className="py-6 text-center text-[10px] uppercase tracking-[0.2em] text-muted-foreground/50">
                Type at least 2 characters...
              </div>
            )}

            <CommandGroup>
              {suggestions.map((user) => {
                const isAlreadySelected = selectedEmails.includes(user.email);
                return (
                  <CommandItem
                    key={user.id}
                    value={user.email}
                    disabled={isAlreadySelected}
                    onSelect={() => {
                      onSelect(user);
                      setOpen(false);
                      setQuery("");
                    }}
                    className={cn(
                      "flex flex-col items-start gap-1 p-3 cursor-pointer transition-colors",
                      "hover:bg-primary/10 aria-selected:bg-primary/20",
                      isAlreadySelected && "opacity-50 cursor-not-allowed",
                    )}
                  >
                    <div className="flex items-center justify-between w-full">
                      <span className="text-sm font-semibold text-foreground">
                        {user.name}
                      </span>
                      {isAlreadySelected && (
                        <Check className="w-3 h-3 text-primary" />
                      )}
                    </div>
                    <span className="text-[10px] text-muted-foreground uppercase tracking-tight">
                      {user.email}
                    </span>
                  </CommandItem>
                );
              })}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
