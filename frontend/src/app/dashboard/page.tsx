"use client";

import React, { useEffect, useState } from "react";
import api from "@/lib/api";
import { CapsuleDto, PagedResponse } from "@/types/capsule";
import CapsuleCard from "@/components/CapsuleCard";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Search,
  Plus,
  ChevronLeft,
  ChevronRight,
  Loader2,
  Archive,
  Users,
  Globe,
  Settings as SettingsIcon,
  LayoutDashboard,
  LockOpen,
} from "lucide-react";
import { Navbar } from "@/components/Navbar";
import { toast } from "sonner";
import Link from "next/link";

const DashboardPage = () => {
  const [capsules, setCapsules] = useState<CapsuleDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [pageSize] = useState(6);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [activeTab, setActiveTab] = useState("my");

  const fetchCapsules = async () => {
    setLoading(true);
    try {
      const endpoint =
        activeTab === "shared" ? "/capsules/shared" : "/capsules";
      const response = await api.get<PagedResponse<CapsuleDto>>(endpoint, {
        params: {
          page: page,
          size: pageSize,
          search: search || undefined,
        },
      });

      let fetchedCapsules = response.data.content;
      if (activeTab === "my") {
        fetchedCapsules = fetchedCapsules.filter((c) => c.status === "LOCKED");
      } else if (activeTab === "public") {
        fetchedCapsules = fetchedCapsules.filter((c) => !c.isPrivate);
      } else if (activeTab === "unlocked") {
        fetchedCapsules = fetchedCapsules.filter(
          (c) => c.status === "UNLOCKED",
        );
      }

      setCapsules(fetchedCapsules);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
    } catch (error) {
      console.error("Failed to fetch capsules:", error);
      toast.error("Failed to load your archive.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCapsules();
  }, [page, search, activeTab]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    fetchCapsules();
  };

  const navItems = [
    { id: "my", label: "My Capsules", icon: LayoutDashboard },
    { id: "shared", label: "Shared with me", icon: Users },
    { id: "public", label: "Public Vaults", icon: Globe },
    { id: "unlocked", label: "Unlocked Vaults", icon: LockOpen },
  ];

  return (
    <div className="min-h-screen bg-[#131313] text-foreground">
      <Navbar />
      <main className="container mx-auto px-6 pt-28 pb-20">
        <div className="flex flex-col lg:flex-row gap-12">
          {/* Sidebar - Desktop */}
          <aside className="hidden lg:block w-64 space-y-8">
            <div>
              <h2 className="font-serif text-sm uppercase tracking-[0.2em] text-primary mb-6">
                Navigation
              </h2>
              <nav className="space-y-2">
                {navItems.map((item) => (
                  <button
                    key={item.id}
                    onClick={() => setActiveTab(item.id)}
                    className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-all duration-300 ${
                      activeTab === item.id
                        ? "bg-primary/10 text-primary border-l-2 border-primary"
                        : "text-muted-foreground hover:text-foreground hover:bg-white/5"
                    }`}
                  >
                    <item.icon className="w-4 h-4" />
                    <span className="text-xs uppercase tracking-widest font-medium">
                      {item.label}
                    </span>
                  </button>
                ))}
              </nav>
            </div>

            <div className="pt-8 border-t border-white/5">
              <div className="glass p-6 rounded-2xl relative overflow-hidden group">
                <div className="relative z-10">
                  <h3 className="font-serif text-lg mb-2">
                    The Vault Is Secure
                  </h3>
                  <p className="text-[10px] text-muted-foreground uppercase tracking-wider mb-4 leading-relaxed">
                    Encryption level: Military Grade AES-256. Your memories are
                    safe for eternity.
                  </p>
                  <div className="w-full h-1 bg-white/10 rounded-full overflow-hidden">
                    <div className="w-full h-full bg-primary/50 animate-pulse" />
                  </div>
                </div>
              </div>
            </div>
          </aside>

          {/* Main Content */}
          <div className="flex-1 space-y-10">
            {/* Header Section */}
            <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
              <div className="space-y-2">
                <h1 className="font-serif text-4xl md:text-5xl text-foreground">
                  The Eternal{" "}
                  <span className="text-primary italic">Archive</span>
                </h1>
                <p className="text-muted-foreground text-sm uppercase tracking-[0.1em]">
                  {totalElements} Sacred artifacts found in your vault
                </p>
              </div>

              <div className="flex items-center gap-4">
                <form onSubmit={handleSearch} className="relative group">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground group-focus-within:text-primary transition-colors" />
                  <Input
                    placeholder="Search artifacts..."
                    className="pl-10 w-full md:w-64 bg-white/5 border-white/10 focus:border-primary/50 focus:ring-primary/20 transition-all"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                  />
                </form>
                <Link href="/dashboard/create">
                  <Button className="gold-gradient text-primary-foreground font-bold uppercase tracking-widest text-xs px-6 hover:scale-105 transition-transform active:scale-95">
                    <Plus className="w-4 h-4 mr-2" />
                    Create New
                  </Button>
                </Link>
              </div>
            </div>

            {/* Grid Section */}
            {loading ? (
              <div className="h-[400px] flex items-center justify-center">
                <div className="flex flex-col items-center space-y-4">
                  <Loader2 className="w-10 h-10 text-primary animate-spin" />
                  <span className="text-xs uppercase tracking-[0.3em] text-primary/70 animate-pulse">
                    Consulting the Oracle...
                  </span>
                </div>
              </div>
            ) : capsules.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                {capsules.map((capsule) => (
                  <CapsuleCard key={capsule.id} capsule={capsule} />
                ))}
              </div>
            ) : (
              <div className="h-[400px] glass rounded-3xl flex flex-col items-center justify-center space-y-6 text-center px-6">
                <div className="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center border border-white/10">
                  <Archive className="w-8 h-8 text-muted-foreground" />
                </div>
                <div className="space-y-2">
                  <h3 className="font-serif text-2xl">The Vault is Empty</h3>
                  <p className="text-muted-foreground max-w-xs mx-auto text-sm">
                    No time capsules have been interred in this section of the
                    archive yet.
                  </p>
                </div>
                <Link href="/dashboard/create">
                  <Button
                    variant="outline"
                    className="border-primary/30 text-primary hover:bg-primary/10 uppercase tracking-widest text-xs"
                  >
                    Begin Your Legacy
                  </Button>
                </Link>
              </div>
            )}

            {/* Pagination Section */}
            {totalPages > 1 && (
              <div className="pt-10 border-t border-white/5 flex items-center justify-between">
                <div className="text-[10px] uppercase tracking-[0.2em] text-muted-foreground">
                  Page{" "}
                  <span className="text-primary font-bold">{page + 1}</span> of{" "}
                  <span className="text-foreground">{totalPages}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => setPage(Math.max(0, page - 1))}
                    disabled={page === 0}
                    className="hover:text-primary disabled:opacity-30"
                  >
                    <ChevronLeft className="w-5 h-5" />
                  </Button>
                  <div className="flex gap-1">
                    {Array.from({ length: totalPages }).map((_, i) => (
                      <button
                        key={i}
                        onClick={() => setPage(i)}
                        className={`w-1.5 h-1.5 rounded-full transition-all duration-300 ${
                          page === i
                            ? "bg-primary w-4"
                            : "bg-white/20 hover:bg-white/40"
                        }`}
                      />
                    ))}
                  </div>
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                    disabled={page === totalPages - 1}
                    className="hover:text-primary disabled:opacity-30"
                  >
                    <ChevronRight className="w-5 h-5" />
                  </Button>
                </div>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default DashboardPage;
