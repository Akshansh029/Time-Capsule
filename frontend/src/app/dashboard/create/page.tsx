"use client";
import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import api from "@/lib/api";
import { Navbar } from "@/components/Navbar";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import DatePicker from "@/components/DatePicker";
import TimePicker from "@/components/TimePicker";
import {
  Shield,
  Lock,
  Globe,
  Sparkles,
  ArrowLeft,
  Loader2,
  Clock as ClockIcon,
} from "lucide-react";
import Link from "next/link";

const createCapsuleSchema = z.object({
  title: z
    .string()
    .min(3, "Title must be at least 3 characters")
    .max(75, "Title must not exceed 75 characters"),
  description: z
    .string()
    .min(1, "Message is required")
    .max(1000, "Message must not exceed 1000 characters"),
  unlockDate: z.date().refine((date) => date > new Date(), {
    message: "Unlock date must be in the future",
  }),
  isPrivate: z.boolean(),
});

type CreateCapsuleFormValues = z.infer<typeof createCapsuleSchema>;

const CreateCapsulePage = () => {
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [selectedTime, setSelectedTime] = useState("12:00");

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    control,
    formState: { errors },
  } = useForm<CreateCapsuleFormValues>({
    resolver: zodResolver(createCapsuleSchema),
    defaultValues: {
      title: "",
      description: "",
      isPrivate: true,
      unlockDate: new Date(new Date().setDate(new Date().getDate() + 1)),
    },
  });

  const isPrivate = watch("isPrivate");
  const currentUnlockDate = watch("unlockDate");

  // Sync date and time
  useEffect(() => {
    if (currentUnlockDate && selectedTime) {
      const [hours, minutes] = selectedTime.split(":").map(Number);
      const newDate = new Date(currentUnlockDate);
      newDate.setHours(hours || 0, minutes || 0, 0, 0);

      // Only set if different to avoid loops
      if (newDate.getTime() !== currentUnlockDate.getTime()) {
        setValue("unlockDate", newDate, { shouldValidate: true });
      }
    }
  }, [selectedTime, currentUnlockDate, setValue]);

  const onSubmit = async (data: CreateCapsuleFormValues) => {
    setIsSubmitting(true);
    try {
      const formattedData = {
        ...data,
        unlockDate: data.unlockDate.toISOString(),
      };

      await api.post("/capsules", formattedData);
      toast.success("Artifact Sealed", {
        description: "Your time capsule has been safely interred in the vault.",
      });
      router.push("/dashboard");
    } catch (error) {
      console.error("Failed to create capsule:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#131313] text-foreground font-sans">
      <Navbar />

      <main className="container mx-auto px-6 pt-32 pb-20 max-w-3xl">
        <Link
          href="/dashboard"
          className="inline-flex items-center text-xs uppercase tracking-widest text-muted-foreground hover:text-primary transition-colors mb-8 group"
        >
          <ArrowLeft className="w-4 h-4 mr-2 group-hover:-translate-x-1 transition-transform" />
          Back to Archive
        </Link>

        <div className="space-y-12">
          <div className="space-y-4 text-center">
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-primary/10 border border-primary/20 mb-4 animate-in fade-in zoom-in duration-700">
              <Sparkles className="w-8 h-8 text-primary" />
            </div>
            <h1 className="font-serif text-4xl md:text-5xl">
              Inter a New <span className="text-primary italic">Artifact</span>
            </h1>
            <p className="text-muted-foreground uppercase tracking-[0.2em] text-[10px] max-w-md mx-auto leading-loose">
              Prepare your legacy. Once sealed, a capsule remains dormant until
              the appointed hour of resurrection.
            </p>
          </div>

          <form
            onSubmit={handleSubmit(onSubmit)}
            className="space-y-8 animate-in slide-in-from-bottom-8 duration-1000"
          >
            <div className="glass-variant p-8 md:p-12 rounded-[2rem] space-y-8 relative overflow-hidden">
              <div className="absolute top-0 right-0 w-64 h-64 bg-primary/5 blur-[100px] -mr-32 -mt-32 rounded-full" />

              <div className="space-y-3 relative">
                <Label
                  htmlFor="title"
                  className="text-xs uppercase tracking-widest text-primary/70 font-semibold"
                >
                  Artifact Title
                </Label>
                <Input
                  id="title"
                  placeholder="The Chronicles of our Youth..."
                  className={`bg-white/5 border-white/10 focus:border-primary/50 focus:ring-primary/20 transition-all py-6 text-lg ${errors.title ? "border-red-500/50" : ""}`}
                  {...register("title")}
                />
                {errors.title && (
                  <p className="text-[10px] text-red-400 uppercase tracking-wider">
                    {errors.title.message}
                  </p>
                )}
              </div>

              <div className="space-y-3">
                <Label
                  htmlFor="description"
                  className="text-xs uppercase tracking-widest text-primary/70 font-semibold"
                >
                  Archival Message
                </Label>
                <Textarea
                  id="description"
                  placeholder="Record your testimony for the future investigators..."
                  className={`bg-white/5 border-white/10 focus:border-primary/50 focus:ring-primary/20 transition-all min-h-[200px] leading-relaxed ${errors.description ? "border-red-500/50" : ""}`}
                  {...register("description")}
                />
                {errors.description && (
                  <p className="text-[10px] text-red-400 uppercase tracking-wider">
                    {errors.description.message}
                  </p>
                )}
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div className="space-y-3">
                  <Label className="text-xs uppercase tracking-widest text-primary/70 font-semibold">
                    Temporal Window
                  </Label>
                  <div className="grid grid-cols-1 sm:grid-cols-[1fr,140px] gap-3">
                    <Controller
                      control={control}
                      name="unlockDate"
                      render={({ field }) => (
                        <DatePicker
                          date={field.value}
                          setDate={field.onChange}
                          error={!!errors.unlockDate}
                        />
                      )}
                    />
                    <TimePicker
                      value={selectedTime}
                      onChange={setSelectedTime}
                      error={!!errors.unlockDate}
                    />
                  </div>
                  {errors.unlockDate && (
                    <p className="text-[10px] text-red-400 uppercase tracking-wider">
                      {errors.unlockDate.message}
                    </p>
                  )}
                </div>

                <div className="space-y-3">
                  <Label className="text-xs uppercase tracking-widest text-primary/70 font-semibold">
                    Visibility Protocol
                  </Label>
                  <div className="grid grid-cols-2 gap-2 bg-white/5 p-1 rounded-lg border border-white/5">
                    <button
                      type="button"
                      onClick={() => setValue("isPrivate", true)}
                      className={`flex items-center justify-center space-x-2 py-2 rounded-md transition-all text-[10px] uppercase tracking-wider ${
                        isPrivate
                          ? "bg-primary text-primary-foreground shadow-lg"
                          : "text-muted-foreground hover:text-foreground"
                      }`}
                    >
                      <Lock className="w-3 h-3" />
                      <span>Private</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => setValue("isPrivate", false)}
                      className={`flex items-center justify-center space-x-2 py-2 rounded-md transition-all text-[10px] uppercase tracking-wider ${
                        !isPrivate
                          ? "bg-primary text-primary-foreground shadow-lg"
                          : "text-muted-foreground hover:text-foreground"
                      }`}
                    >
                      <Globe className="w-3 h-3" />
                      <span>Public</span>
                    </button>
                  </div>
                </div>
              </div>

              {/* Info Box */}
              <div className="bg-primary/5 border border-primary/10 rounded-2xl p-6 flex gap-4 mt-8">
                <Shield className="w-6 h-6 text-primary flex-shrink-0" />
                <div className="space-y-1">
                  <p className="text-xs font-serif text-primary">
                    Sacred Covenant
                  </p>
                  <p className="text-[10px] text-muted-foreground leading-relaxed">
                    By sealing this artifact, you acknowledge that its contents
                    will remain hidden from all eyes—including your own—until
                    the specified date of resurrection.
                  </p>
                </div>
              </div>
            </div>

            <div className="flex flex-col md:flex-row items-center justify-center gap-6 pt-4">
              <Button
                type="button"
                variant="ghost"
                className="uppercase tracking-widest text-xs text-muted-foreground hover:text-foreground px-8 order-2 md:order-1"
                asChild
              >
                <Link href="/dashboard">Renounce</Link>
              </Button>
              <Button
                type="submit"
                disabled={isSubmitting}
                className="gold-gradient text-primary-foreground font-bold uppercase tracking-widest text-sm px-12 py-7 rounded-full shadow-2xl shadow-primary/20 hover:scale-105 transition-all hover:opacity-90 active:scale-95 disabled:opacity-50 order-1 md:order-2"
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                    Sealing...
                  </>
                ) : (
                  <>
                    Seal the Capsule
                    <Shield className="w-5 h-5 ml-2" />
                  </>
                )}
              </Button>
            </div>
          </form>
        </div>
      </main>

      {/* Background Ambience */}
      <div className="fixed inset-0 pointer-events-none -z-10 bg-[radial-gradient(circle_at_center,transparent_0%,#000_100%)] opacity-50" />
    </div>
  );
};

export default CreateCapsulePage;
