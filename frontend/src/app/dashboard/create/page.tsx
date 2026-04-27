"use client";
import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useForm, Controller, useFieldArray } from "react-hook-form";
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
import { UserComboBox } from "@/components/UserComboBox";
import {
  Shield,
  Lock,
  Globe,
  Sparkles,
  ArrowLeft,
  Loader2,
  Clock as ClockIcon,
  Plus,
  Trash2,
  UserPlus,
  Type,
  Image as ImageIcon,
  File as FileIcon,
  X,
} from "lucide-react";
import Link from "next/link";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

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
  contents: z
    .array(
      z.object({
        type: z.enum(["TEXT", "IMAGE", "FILE"]),
        body: z.string().optional(),
        fileUrl: z.string().optional(),
      }),
    )
    .max(10, "Maximum of 10 items allowed"),
  members: z.array(
    z.object({
      userEmail: z.string().email("Invalid email address"),
      userName: z.string().optional(),
      role: z.enum(["ADMIN", "CONTRIBUTOR", "VIEWER"]),
    }),
  ),
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
      isPrivate: false,
      unlockDate: new Date(new Date().setDate(new Date().getDate() + 1)),
      contents: [],
      members: [],
    },
  });

  const {
    fields: contentFields,
    append: appendContent,
    remove: removeContent,
  } = useFieldArray({
    control,
    name: "contents",
  });

  const {
    fields: memberFields,
    append: appendMember,
    remove: removeMember,
  } = useFieldArray({
    control,
    name: "members",
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
        contents: data.contents.map((content) => ({
          type: content.type,
          body: content.type === "TEXT" ? content.body : undefined,
          fileUrl: content.type !== "TEXT" ? content.fileUrl : undefined,
        })),
        members: data.members.map(({ userEmail, role }) => ({
          userEmail,
          role,
        })),
      };

      await api.post("/capsules", formattedData);
      toast.success("Artifact Sealed", {
        description: "Your time capsule has been safely interred in the vault.",
      });
      router.push("/dashboard");
    } catch (error: any) {
      console.error("Failed to create capsule:", error);
      toast.error("Interment Failed", {
        description:
          error.response?.data?.message ||
          "Verify your protocols and try again.",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#131313] text-foreground font-sans">
      <Navbar />

      <main className="container mx-auto px-6 pt-32 pb-20 max-w-4xl">
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

              <div className="grid grid-cols-1 md:grid-cols-2 gap-8 relative z-10">
                <div className="space-y-6">
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
                      Archival Teaser
                    </Label>
                    <Textarea
                      id="description"
                      placeholder="Give a brief hint for the investigators..."
                      className={`bg-white/5 border-white/10 focus:border-primary/50 focus:ring-primary/20 transition-all min-h-[100px] leading-relaxed ${errors.description ? "border-red-500/50" : ""}`}
                      {...register("description")}
                    />
                    {errors.description && (
                      <p className="text-[10px] text-red-400 uppercase tracking-wider">
                        {errors.description.message}
                      </p>
                    )}
                  </div>
                </div>

                <div className="space-y-6">
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
              </div>

              {/* CONTENTS SECTION */}
              <div className="space-y-6 pt-8 border-t border-white/5 relative z-10">
                <div className="flex items-center justify-between">
                  <div className="space-y-1">
                    <Label className="text-xs uppercase tracking-widest text-primary/70 font-semibold">
                      Artifact Contents
                    </Label>
                    <p className="text-[9px] text-muted-foreground uppercase tracking-widest italic">
                      COLLECT THE TESTIMONIES (MAX 10)
                    </p>
                  </div>
                  <div className="flex gap-2">
                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      onClick={() => appendContent({ type: "TEXT", body: "" })}
                      className="border-primary/20 text-primary hover:bg-primary/10 rounded-full h-8 text-[9px] uppercase tracking-widest font-bold"
                    >
                      <Type className="w-3 h-3 mr-1.5" />
                      Text
                    </Button>
                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      onClick={() =>
                        appendContent({ type: "IMAGE", fileUrl: "" })
                      }
                      className="border-primary/20 text-primary hover:bg-primary/10 rounded-full h-8 text-[9px] uppercase tracking-widest font-bold"
                    >
                      <ImageIcon className="w-3 h-3 mr-1.5" />
                      Image
                    </Button>
                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      onClick={() =>
                        appendContent({ type: "FILE", fileUrl: "" })
                      }
                      className="border-primary/20 text-primary hover:bg-primary/10 rounded-full h-8 text-[9px] uppercase tracking-widest font-bold"
                    >
                      <FileIcon className="w-3 h-3 mr-1.5" />
                      File
                    </Button>
                  </div>
                </div>

                <div className="space-y-4">
                  {contentFields.map((field, index) => (
                    <div
                      key={field.id}
                      className="p-5 rounded-2xl bg-white/5 border border-white/5 relative group animate-in slide-in-from-right-4 duration-500"
                    >
                      <button
                        type="button"
                        onClick={() => removeContent(index)}
                        className="absolute top-4 right-4 text-muted-foreground hover:text-red-400 opacity-0 group-hover:opacity-100 transition-opacity"
                      >
                        <X className="w-4 h-4" />
                      </button>

                      <div className="flex gap-4">
                        <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
                          {watch(`contents.${index}.type`) === "TEXT" && (
                            <Type className="w-4 h-4 text-primary" />
                          )}
                          {watch(`contents.${index}.type`) === "IMAGE" && (
                            <ImageIcon className="w-4 h-4 text-primary" />
                          )}
                          {watch(`contents.${index}.type`) === "FILE" && (
                            <FileIcon className="w-4 h-4 text-primary" />
                          )}
                        </div>
                        <div className="flex-1 space-y-4">
                          <div className="flex items-center justify-between">
                            <p className="text-[10px] uppercase tracking-widest font-bold text-primary/50">
                              {watch(`contents.${index}.type`)} Entry #
                              {index + 1}
                            </p>
                          </div>

                          {watch(`contents.${index}.type`) === "TEXT" ? (
                            <Textarea
                              placeholder="Record the sacred text..."
                              className=" border-white/5 bg-transparent focus:border-primary/30 min-h-[100px] text-sm"
                              {...register(`contents.${index}.body` as const)}
                            />
                          ) : (
                            <Input
                              placeholder={
                                watch(`contents.${index}.type`) === "IMAGE"
                                  ? "Enter image URL..."
                                  : "Enter file URL..."
                              }
                              className="bg-transparent border-white/5 focus:border-primary/30 text-sm"
                              {...register(
                                `contents.${index}.fileUrl` as const,
                              )}
                            />
                          )}
                        </div>
                      </div>
                    </div>
                  ))}

                  {contentFields.length === 0 && (
                    <div className="py-12 border border-dashed border-white/10 rounded-3xl text-center">
                      <p className="text-muted-foreground text-[10px] uppercase tracking-widest">
                        No contents added to this artifact yet
                      </p>
                    </div>
                  )}
                </div>
              </div>

              {/* MEMBERS SECTION */}
              <div className="space-y-6 pt-8 border-t border-white/5 relative z-10">
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                  <div className="space-y-1">
                    <Label className="text-xs uppercase tracking-widest text-primary/70 font-semibold">
                      Custodian Network
                    </Label>
                    <p className="text-[9px] text-muted-foreground uppercase tracking-widest italic">
                      WHO ELSE CAN WITNESS THE RESURRECTION?
                    </p>
                  </div>
                  <div className="w-full md:w-80">
                    <UserComboBox
                      selectedEmails={memberFields.map((m) =>
                        watch(`members.${memberFields.indexOf(m)}.userEmail`),
                      )}
                      onSelect={(user) => {
                        appendMember({
                          userEmail: user.email,
                          userName: user.name,
                          role: "VIEWER",
                        });
                      }}
                    />
                  </div>
                </div>

                <div className="space-y-4">
                  {memberFields.map((field, index) => (
                    <div
                      key={field.id}
                      className="p-4 rounded-xl bg-white/5 border border-white/5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 animate-in slide-in-from-left-4 duration-500 hover:bg-white/[0.08] transition-colors"
                    >
                      <div className="flex items-center space-x-4 flex-1 min-w-0">
                        <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
                          <UserPlus className="w-5 h-5 text-primary" />
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-xs font-semibold text-foreground truncate">
                            {watch(`members.${index}.userName`) ||
                              watch(`members.${index}.userEmail`).split("@")[0]}
                          </p>
                          <p className="text-[10px] text-muted-foreground uppercase tracking-tight truncate">
                            {watch(`members.${index}.userEmail`)}
                          </p>
                        </div>
                      </div>

                      <div className="flex items-center gap-3">
                        <Controller
                          control={control}
                          name={`members.${index}.role` as const}
                          render={({ field }) => (
                            <Select
                              onValueChange={field.onChange}
                              defaultValue={field.value}
                            >
                              <SelectTrigger className="h-10 w-32 rounded-xl bg-white/5 border-white/10 text-[10px] uppercase tracking-widest font-bold">
                                <SelectValue placeholder="Role" />
                              </SelectTrigger>
                              <SelectContent>
                                <SelectItem
                                  value="VIEWER"
                                  className="text-[10px] uppercase tracking-widest font-bold"
                                >
                                  Viewer
                                </SelectItem>
                                <SelectItem
                                  value="CONTRIBUTOR"
                                  className="text-[10px] uppercase tracking-widest font-bold"
                                >
                                  Contributor
                                </SelectItem>
                                <SelectItem
                                  value="ADMIN"
                                  className="text-[10px] uppercase tracking-widest font-bold"
                                >
                                  Admin
                                </SelectItem>
                              </SelectContent>
                            </Select>
                          )}
                        />

                        <Button
                          type="button"
                          variant="ghost"
                          size="icon"
                          onClick={() => removeMember(index)}
                          className="h-10 w-10 rounded-xl text-muted-foreground hover:text-red-400 hover:bg-red-400/5 mt-0"
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </div>
                  ))}

                  {memberFields.length === 0 && (
                    <div className="py-8 border border-dashed border-white/10 rounded-3xl text-center">
                      <p className="text-muted-foreground text-[10px] uppercase tracking-widest">
                        Sole Custodian: Only you will witness this
                      </p>
                      <p className="text-[8px] text-muted-foreground/50 uppercase tracking-[0.2em] mt-1">
                        Use the search above to invite others
                      </p>
                    </div>
                  )}
                </div>
              </div>

              {/* Info Box */}
              <div className="bg-primary/5 border border-primary/10 rounded-2xl p-6 flex gap-4 mt-8 relative z-10">
                <Shield className="w-6 h-6 text-primary flex-shrink-0" />
                <div className="space-y-1">
                  <p className="text-xs font-serif text-primary">
                    Sacred Covenant
                  </p>
                  <p className="text-[10px] text-muted-foreground leading-relaxed">
                    By sealing this artifact, you acknowledge that its contents
                    will remain hidden from all eyes—including your own—until
                    the specified date of resurrection. Members invited will be
                    granted access according to their designated roles.
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
