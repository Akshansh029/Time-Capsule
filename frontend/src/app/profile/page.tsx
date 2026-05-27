"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/lib/api";
import { ActiveUserResponse, UpdateUsernameRequestDto } from "@/types/user";
import { useAuthStore } from "@/store/authStore";
import { Navbar } from "@/components/Navbar";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { toast } from "sonner";
import {
  User,
  Mail,
  Calendar,
  Archive,
  FileText,
  Users,
  ArrowLeft,
  Trash2,
  Edit2,
  Loader2,
  Lock,
  History,
  ShieldAlert,
} from "lucide-react";
import { format } from "date-fns";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import Link from "next/link";

const updateNameSchema = z.object({
  name: z
    .string()
    .min(3, "Name must be at least 3 characters")
    .max(50, "Name must be at most 50 characters"),
});

const ProfilePage = () => {
  const router = useRouter();
  const { logout, setAuth, accessToken } = useAuthStore();
  const [profile, setProfile] = useState<ActiveUserResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isEditDialogOpen, setIsEditOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteOpen] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<UpdateUsernameRequestDto>({
    resolver: zodResolver(updateNameSchema),
  });

  const fetchProfile = async () => {
    try {
      setIsLoading(true);
      // Attempting to fetch active user details.
      // User requested ActiveUserResponse display.
      const response = await api.get<ActiveUserResponse>("/users/me");
      setProfile(response.data);
      setValue("name", response.data.name);
    } catch (error: any) {
      console.error("Failed to fetch profile:", error);
      toast.error("Failed to load profile details.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const onUpdateName = async (data: UpdateUsernameRequestDto) => {
    setIsUpdating(true);
    try {
      const response = await api.patch<ActiveUserResponse>(
        "/users/username",
        data,
      );
      setProfile(response.data);

      // Update the auth store user name as well
      const currentUser = useAuthStore.getState().user;
      if (currentUser && accessToken) {
        setAuth({ ...currentUser, name: response.data.name }, accessToken);
      }

      toast.success("Name updated successfully!");
      setIsEditOpen(false);
    } catch (error: any) {
      toast.error(error.response?.data?.message || "Failed to update name.");
    } finally {
      setIsUpdating(false);
    }
  };

  const onDeleteAccount = async () => {
    setIsDeleting(true);
    try {
      await api.delete("/users");
      toast.success("Account deleted successfully.");
      logout();
      router.push("/");
    } catch (error: any) {
      toast.error(error.response?.data?.message || "Failed to delete account.");
    } finally {
      setIsDeleting(false);
      setIsDeleteOpen(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#131313] flex items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="w-12 h-12 text-primary animate-spin" />
          <p className="text-muted-foreground uppercase tracking-[0.3em] text-[10px]">
            Accessing Your Records...
          </p>
        </div>
      </div>
    );
  }

  if (!profile) return null;

  return (
    <div className="min-h-screen bg-[#131313] text-foreground font-sans selection:bg-primary/30">
      <Navbar />

      <main className="container mx-auto px-6 pt-32 pb-20 max-w-4xl">
        <Link
          href="/dashboard"
          className="inline-flex items-center text-xs uppercase tracking-widest text-muted-foreground hover:text-primary transition-colors mb-12 group"
        >
          <ArrowLeft className="w-4 h-4 mr-2 group-hover:-translate-x-1 transition-transform" />
          Back to Archive
        </Link>

        <div className="space-y-12 animate-in fade-in duration-1000">
          <div className="flex flex-col md:flex-row items-start md:items-end justify-between gap-8">
            <div className="space-y-4">
              <h1 className="font-serif text-4xl md:text-6xl tracking-tight">
                Custodian <span className="text-primary italic">Profile</span>
              </h1>
              <p className="text-muted-foreground uppercase tracking-[0.25em] text-[10px] max-w-md leading-loose">
                IDENTITY & TEMPORAL STATS
              </p>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Profile Info Section */}
            <div className="lg:col-span-2 space-y-8">
              <div className="glass-variant p-8 md:p-10 rounded-[3rem] border-primary/10 relative overflow-hidden group">
                <div className="absolute top-0 right-0 w-64 h-64 bg-primary/5 blur-[100px] -mr-32 -mt-32 rounded-full" />

                <div className="relative space-y-8 z-10">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-6">
                      <div className="w-20 h-20 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center">
                        <User className="w-10 h-10 text-primary" />
                      </div>
                      <div>
                        <h2 className="text-2xl font-serif">{profile.name}</h2>
                        <p className="text-sm text-muted-foreground uppercase tracking-widest mt-1">
                          {profile.email}
                        </p>
                      </div>
                    </div>

                    <Dialog
                      open={isEditDialogOpen}
                      onOpenChange={setIsEditOpen}
                    >
                      <DialogTrigger asChild>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="hover:text-primary rounded-full w-10 h-10 bg-white/5 border border-white/10"
                        >
                          <Edit2 className="w-4 h-4" />
                        </Button>
                      </DialogTrigger>
                      <DialogContent>
                        <DialogHeader>
                          <DialogTitle>Update Identity</DialogTitle>
                          <DialogDescription>
                            Change your custodian name in the eternal vault.
                          </DialogDescription>
                        </DialogHeader>
                        <form
                          onSubmit={handleSubmit(onUpdateName)}
                          className="space-y-4 py-4"
                        >
                          <div className="space-y-2">
                            <Label
                              htmlFor="name"
                              className="text-xs uppercase tracking-widest text-muted-foreground"
                            >
                              New Name
                            </Label>
                            <Input
                              id="name"
                              {...register("name")}
                              className="bg-white/5 border-white/10 focus:border-primary/50 rounded-xl"
                            />
                            {errors.name && (
                              <p className="text-[10px] text-red-500 uppercase tracking-tighter">
                                {errors.name.message}
                              </p>
                            )}
                          </div>
                          <DialogFooter>
                            <Button
                              type="submit"
                              disabled={isUpdating}
                              className="w-full gold-gradient text-primary-foreground font-bold uppercase tracking-widest text-xs h-11 rounded-xl shadow-lg"
                            >
                              {isUpdating ? (
                                <Loader2 className="w-4 h-4 animate-spin mr-2" />
                              ) : null}
                              Confirm Revelation
                            </Button>
                          </DialogFooter>
                        </form>
                      </DialogContent>
                    </Dialog>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-8 border-t border-white/5">
                    <div className="flex items-start space-x-4">
                      <div className="w-10 h-10 rounded-xl bg-white/5 border border-white/10 flex items-center justify-center shrink-0">
                        <Mail className="w-4 h-4 text-primary/60" />
                      </div>
                      <div className="space-y-1">
                        <p className="text-[10px] uppercase tracking-widest text-muted-foreground">
                          Email Address
                        </p>
                        <p className="text-sm font-medium">{profile.email}</p>
                      </div>
                    </div>

                    <div className="flex items-start space-x-4">
                      <div className="w-10 h-10 rounded-xl bg-white/5 border border-white/10 flex items-center justify-center shrink-0">
                        <Calendar className="w-4 h-4 text-primary/60" />
                      </div>
                      <div className="space-y-1">
                        <p className="text-[10px] uppercase tracking-widest text-muted-foreground">
                          Journey Began
                        </p>
                        <p className="text-sm font-medium">
                          {format(new Date(profile.createdAt), "MMMM do, yyyy")}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* Stats Highlights */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="glass-variant p-6 rounded-[2rem] border-white/5 space-y-4">
                  <Archive className="w-5 h-5 text-primary/60" />
                  <div>
                    <p className="text-[9px] uppercase tracking-widest text-muted-foreground">
                      Own Capsules
                    </p>
                    <p className="text-2xl font-serif text-primary mt-1">
                      {profile.noOfOwnedCapsules}
                    </p>
                  </div>
                </div>
                <div className="glass-variant p-6 rounded-[2rem] border-white/5 space-y-4">
                  <FileText className="w-5 h-5 text-primary/60" />
                  <div>
                    <p className="text-[9px] uppercase tracking-widest text-muted-foreground">
                      Archived Artifacts
                    </p>
                    <p className="text-2xl font-serif text-primary mt-1">
                      {profile.noOfOwnedContents}
                    </p>
                  </div>
                </div>
                <div className="glass-variant p-6 rounded-[2rem] border-white/5 space-y-4">
                  <Users className="w-5 h-5 text-primary/60" />
                  <div>
                    <p className="text-[9px] uppercase tracking-widest text-muted-foreground">
                      Custodian Networks
                    </p>
                    <p className="text-2xl font-serif text-primary mt-1">
                      {profile.noOfMemberOfCapsules}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Sidebar info */}
            <div className="space-y-8">
              <div className="glass-variant p-8 rounded-[2.5rem] border-white/5 space-y-6">
                <div className="flex items-center space-x-3 text-primary/80">
                  <ShieldAlert className="w-5 h-5" />
                  <h3 className="text-xs uppercase tracking-[0.2em] font-bold">
                    Temporal Security
                  </h3>
                </div>

                <div className="space-y-4">
                  <div className="flex items-start space-x-4">
                    <Lock className="w-4 h-4 text-muted-foreground mt-0.5" />
                    <div>
                      <p className="text-[11px] font-medium">
                        Account Integrity
                      </p>
                      <p className="text-[10px] text-muted-foreground mt-0.5 leading-relaxed">
                        Your identity is protected with end-to-end archival
                        standards.
                      </p>
                    </div>
                  </div>

                  <div className="flex items-start space-x-4">
                    <History className="w-4 h-4 text-muted-foreground mt-0.5" />
                    <div>
                      <p className="text-[11px] font-medium">Session Logs</p>
                      <p className="text-[10px] text-muted-foreground mt-0.5 leading-relaxed">
                        Access is monitored for security and temporal
                        consistency.
                      </p>
                    </div>
                  </div>
                </div>

                <div className="pt-6 border-t border-white/5 space-y-4">
                  <p className="text-[10px] text-muted-foreground italic leading-relaxed">
                    Account deletion is irreversible. All interred artifacts
                    will be permanently lost to the void.
                  </p>

                  <Dialog
                    open={isDeleteDialogOpen}
                    onOpenChange={setIsDeleteOpen}
                  >
                    <DialogTrigger asChild>
                      <Button
                        variant="ghost"
                        className="w-full border border-red-500/20 text-red-500 hover:bg-red-500/10 hover:text-red-400 uppercase tracking-widest text-[10px] font-bold h-11 rounded-xl px-6"
                      >
                        <Trash2 className="w-4 h-4 mr-2" />
                        Destroy Account
                      </Button>
                    </DialogTrigger>
                    <DialogContent className="border-red-500/20">
                      <DialogHeader>
                        <DialogTitle className="text-red-500">
                          Atomic Deletion
                        </DialogTitle>
                        <DialogDescription className="text-muted-foreground">
                          This action is absolute. Your legacy will be erased
                          from the vault forever. Are you certain?
                        </DialogDescription>
                      </DialogHeader>
                      <div className="p-4 bg-red-500/5 border border-red-500/10 rounded-2xl flex gap-4">
                        <ShieldAlert className="w-6 h-6 text-red-500 shrink-0" />
                        <p className="text-[11px] text-red-400/80 leading-relaxed italic">
                          No capsule will survive. The temporal records
                          associated with {profile.email} will be purged.
                        </p>
                      </div>
                      <DialogFooter className="gap-3 sm:gap-0">
                        <Button
                          variant="ghost"
                          onClick={() => setIsDeleteOpen(false)}
                          className="flex-1 border border-white/10 uppercase tracking-widest text-[10px] h-11 rounded-xl"
                        >
                          Retreat
                        </Button>
                        <Button
                          onClick={onDeleteAccount}
                          disabled={isDeleting}
                          className="flex-1 bg-red-600 hover:bg-red-700 text-white font-bold uppercase tracking-widest text-[10px] h-11 rounded-xl"
                        >
                          {isDeleting ? (
                            <Loader2 className="w-4 h-4 animate-spin mr-2" />
                          ) : null}
                          Erase Permanently
                        </Button>
                      </DialogFooter>
                    </DialogContent>
                  </Dialog>
                </div>
              </div>

              <div className="bg-primary/5 border border-primary/10 rounded-3xl p-6 flex gap-4">
                <Lock className="w-6 h-6 text-primary flex-shrink-0" />
                <div className="space-y-1">
                  <p className="text-[11px] font-serif text-primary italic">
                    Sacred Anonymity
                  </p>
                  <p className="text-[10px] text-muted-foreground leading-relaxed">
                    Your artifacts remain yours until the moment of revelation.
                    We do not peek into the soul of your vaults.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default ProfilePage;
